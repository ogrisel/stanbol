package eu.iksproject.fise.engines.opennlp.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.clerezza.rdf.core.Literal;
import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.Resource;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.TypedLiteral;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.iksproject.fise.engines.autotagging.impl.ConfiguredAutotaggerProvider;
import eu.iksproject.fise.servicesapi.ContentItem;
import eu.iksproject.fise.servicesapi.EngineException;
import eu.iksproject.fise.servicesapi.rdf.Properties;
import eu.iksproject.fise.servicesapi.rdf.TechnicalClasses;

public class TestNamedEntityExtractionEnhancementEngine extends Assert {

    public static final String SINGLE_SENTENCE = "Dr. Patrick Marshall (1869 - November 1950) was a"
            + " geologist who lived in New Zealand and worked at the University of Otago.";

    public static final String MULTI_SENCTENCES = "The life of Patrick Marshall\n\n"
            + "Dr Patrick Marshall (1869 - November 1950) was a"
            + " geologist who lived in New Zealand and worked at the"
            + " University of Otago. This is another unrelated sentence"
            + " without any name.\n"
            + "A new paragraph is being written. This paragraph has two sentences.";

    static NamedEntityExtractionEnhancementEngine nerEngine = new NamedEntityExtractionEnhancementEngine();

    @BeforeClass
    public static void setUpServices() throws IOException {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(ConfiguredAutotaggerProvider.LUCENE_INDEX_PATH, "");
        MockComponentContext context = new MockComponentContext(properties);
        nerEngine.activate(context);
    }

    @AfterClass
    public static void shutdownServices() {
        nerEngine.deactivate(null);
    }

    public static ContentItem wrapAsContentItem(final String id,
            final String text) {
        return new ContentItem() {

            SimpleMGraph metadata = new SimpleMGraph();

            public InputStream getStream() {
                return new ByteArrayInputStream(text.getBytes());
            }

            public String getMimeType() {
                return "text/plain";
            }

            public MGraph getMetadata() {
                return metadata;
            }

            public String getId() {
                return id;
            }
        };
    }

    @Test
    public void testPersonNamesExtraction() {
        Collection<String> names = nerEngine.extractPersonNames(SINGLE_SENTENCE);
        assertEquals(1, names.size());
        assertTrue(names.contains("Patrick Marshall"));
    }

    @Test
    public void testPersonNameOccurencesExtraction() {
        Map<String, List<NameOccurrence>> nameOccurrences = nerEngine.extractPersonNameOccurrences(MULTI_SENCTENCES);
        assertEquals(1, nameOccurrences.size());

        List<NameOccurrence> pmOccurences = nameOccurrences.get("Patrick Marshall");
        assertNotNull(pmOccurences);
        assertEquals(2, pmOccurences.size());

        NameOccurrence firstOccurence = pmOccurences.get(0);
        assertEquals("Patrick Marshall", firstOccurence.name);
        assertEquals(12, firstOccurence.start.intValue());
        assertEquals(28, firstOccurence.end.intValue());
        assertEquals(0.98, firstOccurence.confidence, 0.005);

        NameOccurrence secondOccurrence = pmOccurences.get(1);
        assertEquals("Patrick Marshall", secondOccurrence.name);
        assertEquals(33, secondOccurrence.start.intValue());
        assertEquals(49, secondOccurrence.end.intValue());
        assertEquals(0.97, secondOccurrence.confidence, 0.005);
    }

    @Test
    public void testLocationNamesExtraction() {
        Collection<String> names = nerEngine.extractLocationNames(SINGLE_SENTENCE);
        assertEquals(1, names.size());
        assertTrue(names.contains("New Zealand"));
    }

    @Test
    public void testComputeEnhancements()
            throws EngineException {
        ContentItem ci = wrapAsContentItem("my doc id", SINGLE_SENTENCE);
        nerEngine.computeEnhancements(ci);
        MGraph g = ci.getMetadata();
        int textAnnotationCount = checkAllTextAnnotations(g,SINGLE_SENTENCE);
        assertEquals(3, textAnnotationCount);

        //This Engine dose no longer create entityAnnotations
//        int entityAnnotationCount = checkAllEntityAnnotations(g);
//        assertEquals(2, entityAnnotationCount);
    }

    /*
     * -----------------------------------------------------------------------
     * Helper Methods to check Text and EntityAnnotations
     * -----------------------------------------------------------------------
     */
 
    /**
     * @param g
     * @return
     */
    private int checkAllTextAnnotations(MGraph g, String content) {
        Iterator<Triple> textAnnotationIterator = g.filter(null,
                Properties.RDF_TYPE, TechnicalClasses.FISE_TEXT_ANNOTATION);
        // test if a textAnnotation is present
        assertTrue(textAnnotationIterator.hasNext());
        int textAnnotationCount = 0;
        while (textAnnotationIterator.hasNext()) {
            UriRef textAnnotation = (UriRef) textAnnotationIterator.next().getSubject();
            // test if selected Text is added
            checkTextAnnotation(g, textAnnotation,content);
            textAnnotationCount++;
        }
        return textAnnotationCount;
    }

    /**
     * Checks if a text annotation is valid
     *
     * @param g
     * @param textAnnotation
     */
    private void checkTextAnnotation(MGraph g, UriRef textAnnotation, String content) {
        Iterator<Triple> selectedTextIterator = g.filter(textAnnotation,
                Properties.FISE_SELECTED_TEXT, null);
        // check if the selected text is added
        assertTrue(selectedTextIterator.hasNext());
        // test if the selected text is part of the TEXT_TO_TEST
        Resource object = selectedTextIterator.next().getObject();
        assertTrue(object instanceof Literal);
        Literal selectedText = (Literal)object;
        object = null;
        assertTrue(SINGLE_SENTENCE.indexOf(selectedText.getLexicalForm()) >= 0);
        // test if context is added
        Iterator<Triple> selectionContextIterator = g.filter(textAnnotation,
                Properties.FISE_SELECTION_CONTEXT, null);
        assertTrue(selectionContextIterator.hasNext());
        // test if the selected text is part of the TEXT_TO_TEST
        object = selectionContextIterator.next().getObject();
        assertTrue(object instanceof Literal);
        assertTrue(SINGLE_SENTENCE.indexOf(((Literal)object).getLexicalForm()) >= 0);
        object = null;
        //test start/end if present
        Iterator<Triple> startPosIterator = g.filter(textAnnotation,
                Properties.FISE_START, null);
        Iterator<Triple> endPosIterator = g.filter(textAnnotation,
                Properties.FISE_END, null);
        //start end is optional, but if start is present, that also end needs to be set
        if(startPosIterator.hasNext()){
        	Resource resource = startPosIterator.next().getObject();
        	//only a single start position is supported
        	assertTrue(!startPosIterator.hasNext());
        	assertTrue(resource instanceof TypedLiteral);
        	TypedLiteral startPosLiteral = (TypedLiteral) resource;
        	resource = null;
        	int start = LiteralFactory.getInstance().createObject(Integer.class, startPosLiteral);
        	startPosLiteral = null;
        	//now get the end
            //end must be defined if start is present
            assertTrue(endPosIterator.hasNext());
            resource = endPosIterator.next().getObject();
        	//only a single end position is supported
        	assertTrue(!endPosIterator.hasNext());
        	assertTrue(resource instanceof TypedLiteral);
        	TypedLiteral endPosLiteral = (TypedLiteral) resource;
        	resource = null;
        	int end = LiteralFactory.getInstance().createObject(Integer.class, endPosLiteral);
        	endPosLiteral = null;
        	//check for equality of the selected text and the text on the selected position in the content
        	//System.out.println("TA ["+start+"|"+end+"]"+selectedText.getLexicalForm()+"<->"+content.substring(start,end));
        	assertTrue(content.substring(start, end).equals(selectedText.getLexicalForm()));
        } else {
        	//if no start position is present, there must also be no end position defined
        	assertTrue(!endPosIterator.hasNext());
        }
    }


}
