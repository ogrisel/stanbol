package eu.iksproject.fise.serviceapi.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import org.apache.clerezza.rdf.core.Literal;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.Resource;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.junit.Test;

import eu.iksproject.fise.servicesapi.ContentItem;
import eu.iksproject.fise.servicesapi.EntityAnnotation;
import eu.iksproject.fise.servicesapi.TextAnnotation;
import eu.iksproject.fise.servicesapi.helper.EnhancementEngineHelper;
import eu.iksproject.fise.servicesapi.helper.RdfEntityFactory;
import eu.iksproject.fise.servicesapi.rdf.Properties;
import eu.iksproject.fise.servicesapi.rdf.TechnicalClasses;

/**
 * Tests if the enhancement interfaces can be used to write valid enhancement
 * @author westei
 *
 */
public class TestEnhancementInterfaces {

	public static final String SINGLE_SENTENCE = "Dr. Patrick Marshall (1869 - November 1950) was a"
        + " geologist who lived in New Zealand and worked at the University of Otago.";
	public static final UriRef TEST_ENHANCEMENT_ENGINE_URI = new UriRef("urn:test:dummyEnhancemenEngine");
	public static ContentItem wrapAsContentItem(final String id, final String text) {
        return new ContentItem() {
            SimpleMGraph metadata = new SimpleMGraph();
            @Override
            public InputStream getStream() { return new ByteArrayInputStream(text.getBytes());}
            @Override
            public String getMimeType() { return "text/plain"; }
	        @Override
            public MGraph getMetadata() { return metadata; }
	        @Override
            public String getId() { return id; }
	    };
	}
	@Test
	public void testEnhancementInterfaces() throws Exception {
		ContentItem ci = wrapAsContentItem("urn:contentItem-"
                + EnhancementEngineHelper.randomUUID(),SINGLE_SENTENCE);
		UriRef ciUri = new UriRef(ci.getId());
		RdfEntityFactory factory = RdfEntityFactory.createInstance(ci.getMetadata());
		long start = System.currentTimeMillis();
		//create an Text Annotation representing an extracted Person
		TextAnnotation personAnnotation = factory.getProxy(
				createEnhancementURI(), TextAnnotation.class);
		personAnnotation.setCreator(TEST_ENHANCEMENT_ENGINE_URI);
		personAnnotation.setCreated(new Date());
		personAnnotation.setExtractedFrom(ciUri);
		personAnnotation.getDcType().add(new UriRef("http://fise.iks-project.eu/cv/annotatation-types/text#Person"));
		personAnnotation.setConfidence(0.8);
		personAnnotation.setSelectedText("Patrick Marshall");
		personAnnotation.setStart(SINGLE_SENTENCE.indexOf(personAnnotation.getSelectedText()));
		personAnnotation.setEnd(personAnnotation.getStart()+personAnnotation.getSelectedText().length());
		personAnnotation.setSelectionContext(SINGLE_SENTENCE);

		//create an Text Annotation representing an extracted Location
		TextAnnotation locationAnnotation = factory.getProxy(
				createEnhancementURI(),	TextAnnotation.class);
		locationAnnotation.setCreator(TEST_ENHANCEMENT_ENGINE_URI);
		locationAnnotation.setCreated(new Date());
		locationAnnotation.setExtractedFrom(ciUri);
		locationAnnotation.getDcType().add(new UriRef("http://fise.iks-project.eu/cv/annotatation-types/text#Location"));
		locationAnnotation.setConfidence(0.78);
		locationAnnotation.setSelectedText("New Zealand");
		locationAnnotation.setStart(SINGLE_SENTENCE.indexOf(locationAnnotation.getSelectedText()));
		locationAnnotation.setEnd(locationAnnotation.getStart()+locationAnnotation.getSelectedText().length());
		locationAnnotation.setSelectionContext(SINGLE_SENTENCE);

		//create an Text Annotation representing an extracted Organisation
		TextAnnotation orgAnnotation = factory.getProxy(
				createEnhancementURI(),	TextAnnotation.class);
		orgAnnotation.setCreator(TEST_ENHANCEMENT_ENGINE_URI);
		orgAnnotation.setCreated(new Date());
		orgAnnotation.setExtractedFrom(ciUri);
		orgAnnotation.getDcType().add(new UriRef("http://fise.iks-project.eu/cv/annotatation-types/text#Organisation"));
		orgAnnotation.setConfidence(0.78);
		orgAnnotation.setSelectedText("University of Otago");
		orgAnnotation.setStart(SINGLE_SENTENCE.indexOf(orgAnnotation.getSelectedText()));
		orgAnnotation.setEnd(orgAnnotation.getStart()+orgAnnotation.getSelectedText().length());
		orgAnnotation.setSelectionContext(SINGLE_SENTENCE);

		// create an Entity Annotation for the person TextAnnotation
		EntityAnnotation patrickMarshall = factory.getProxy(
				createEnhancementURI(), EntityAnnotation.class);
		patrickMarshall.setCreator(TEST_ENHANCEMENT_ENGINE_URI);
		patrickMarshall.setCreated(new Date());
		patrickMarshall.setExtractedFrom(ciUri);
		patrickMarshall.getDcType().add(new UriRef("http://fise.iks-project.eu/cv/annotatation-types/entity#Entity"));
		patrickMarshall.setConfidence(0.56);
		patrickMarshall.getRelations().add(personAnnotation);
		patrickMarshall.setEntityLabel("Patrick Marshall");
		patrickMarshall.setEntityReference(new UriRef("http://rdf.freebase.com/rdf/en/patrick_marshall"));
		patrickMarshall.getEntityTypes().addAll(Arrays.asList(
						new UriRef("http://rdf.freebase.com/ns/people.person"),
						new UriRef("http://rdf.freebase.com/ns/common.topic"),
						new UriRef("http://rdf.freebase.com/ns/education.academic")));
		// and an other for New Zealand
		EntityAnnotation newZealand = factory.getProxy(
				createEnhancementURI(), EntityAnnotation.class);
		newZealand.setCreator(TEST_ENHANCEMENT_ENGINE_URI);
		newZealand.setCreated(new Date());
		newZealand.setExtractedFrom(ciUri);
		newZealand.getDcType().add(new UriRef("http://fise.iks-project.eu/cv/annotatation-types/entity#Entity"));
		newZealand.setConfidence(0.98);
		newZealand.getRelations().add(locationAnnotation);
		newZealand.setEntityLabel("New Zealand");
		newZealand.setEntityReference(new UriRef("http://rdf.freebase.com/rdf/en/new_zealand"));
		newZealand.getEntityTypes().addAll(Arrays.asList(
				new UriRef("http://rdf.freebase.com/ns/location.location"),
				new UriRef("http://rdf.freebase.com/ns/common.topic"),
				new UriRef("http://rdf.freebase.com/ns/location.country")));

		// and an other option for New Zealand
		EntityAnnotation airNewZealand = factory.getProxy(
				createEnhancementURI(), EntityAnnotation.class);
		airNewZealand.setCreator(TEST_ENHANCEMENT_ENGINE_URI);
		airNewZealand.setCreated(new Date());
		airNewZealand.setExtractedFrom(ciUri);
		airNewZealand.getDcType().add(new UriRef("http://fise.iks-project.eu/cv/annotatation-types/entity#Entity"));
		airNewZealand.setConfidence(0.36);
		airNewZealand.getRelations().add(locationAnnotation);
		airNewZealand.setEntityLabel("New Zealand");
		airNewZealand.setEntityReference(new UriRef("http://rdf.freebase.com/rdf/en/air_new_zealand"));
		airNewZealand.getEntityTypes().addAll(Arrays.asList(
				new UriRef("http://rdf.freebase.com/ns/business.sponsor"),
				new UriRef("http://rdf.freebase.com/ns/common.topic"),
				new UriRef("http://rdf.freebase.com/ns/travel.transport_operator"),
				new UriRef("http://rdf.freebase.com/ns/aviation.airline"),
				new UriRef("http://rdf.freebase.com/ns/aviation.aircraft_owner"),
				new UriRef("http://rdf.freebase.com/ns/business.employer"),
				new UriRef("http://rdf.freebase.com/ns/freebase.apps.hosts.com.appspot.acre.juggle.juggle"),
				new UriRef("http://rdf.freebase.com/ns/business.company")));
		System.out.println("creation time "+(System.currentTimeMillis()-start)+"ms");
		//now test the enhancement
		int numberOfTextAnnotations = checkAllTextAnnotations(ci.getMetadata());
		assertTrue(numberOfTextAnnotations == 3);
		int numberOfEntityAnnotations = checkAllEntityAnnotations(ci.getMetadata());
		assertTrue(numberOfEntityAnnotations == 3);
	}
	/**
	 * @return
	 */
	private UriRef createEnhancementURI() {
		//TODO: add some Utility to create Instances to the RdfEntityFactory
		//      this should create a new URI by some default Algorithm
		UriRef enhancementNode = new UriRef("urn:enhancement-"
                + EnhancementEngineHelper.randomUUID());
		return enhancementNode;
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
    private int checkAllEntityAnnotations(MGraph g) {
        Iterator<Triple> entityAnnotationIterator = g.filter(null,
                Properties.RDF_TYPE, TechnicalClasses.FISE_ENTITY_ANNOTATION);
        int entityAnnotationCount = 0;
        while (entityAnnotationIterator.hasNext()) {
            UriRef entityAnnotation = (UriRef) entityAnnotationIterator.next().getSubject();
            // test if selected Text is added
            checkEntityAnnotation(g, entityAnnotation);
            entityAnnotationCount++;
        }
        return entityAnnotationCount;
    }

    /**
     * @param g
     * @return
     */
    private int checkAllTextAnnotations(MGraph g) {
        Iterator<Triple> textAnnotationIterator = g.filter(null,
                Properties.RDF_TYPE, TechnicalClasses.FISE_TEXT_ANNOTATION);
        // test if a textAnnotation is present
        assertTrue("Expecting non-empty textAnnotationIterator", textAnnotationIterator.hasNext());
        int textAnnotationCount = 0;
        while (textAnnotationIterator.hasNext()) {
            UriRef textAnnotation = (UriRef) textAnnotationIterator.next().getSubject();
            // test if selected Text is added
            checkTextAnnotation(g, textAnnotation);
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
    private void checkTextAnnotation(MGraph g, UriRef textAnnotation) {
        Iterator<Triple> selectedTextIterator = g.filter(textAnnotation,
                Properties.FISE_SELECTED_TEXT, null);
        // check if the selected text is added
        assertTrue(selectedTextIterator.hasNext());
        // test if the selected text is part of the TEXT_TO_TEST
        Resource object = selectedTextIterator.next().getObject();
        assertTrue(object instanceof Literal);
        assertTrue(SINGLE_SENTENCE.indexOf(((Literal) object).getLexicalForm()) >= 0);
        // test if context is added
        Iterator<Triple> selectionContextIterator = g.filter(textAnnotation,
                Properties.FISE_SELECTION_CONTEXT, null);
        assertTrue(selectionContextIterator.hasNext());
        // test if the selected text is part of the TEXT_TO_TEST
        object = selectionContextIterator.next().getObject();
        assertTrue(object instanceof Literal);
        assertTrue(SINGLE_SENTENCE.indexOf(((Literal) object).getLexicalForm()) >= 0);
    }

    /**
     * Checks if an entity annotation is valid
     *
     * @param g
     * @param textAnnotation
     */
    private void checkEntityAnnotation(MGraph g, UriRef entityAnnotation) {
        Iterator<Triple> relationToTextAnnotationIterator = g.filter(
                entityAnnotation, Properties.DC_RELATION, null);
        // check if the relation to the text annotation is set
        assertTrue(relationToTextAnnotationIterator.hasNext());
        while (relationToTextAnnotationIterator.hasNext()) {
            // test if the referred annotations are text annotations
            UriRef referredTextAnnotation = (UriRef) relationToTextAnnotationIterator.next().getObject();
            assertTrue(g.filter(referredTextAnnotation, Properties.RDF_TYPE,
                    TechnicalClasses.FISE_TEXT_ANNOTATION).hasNext());
        }

        // test if an entity is referred
        Iterator<Triple> entityReferenceIterator = g.filter(entityAnnotation,
                Properties.FISE_ENTITY_REFERENCE, null);
        assertTrue(entityReferenceIterator.hasNext());
        // test if the reference is an URI
        assertTrue(entityReferenceIterator.next().getObject() instanceof UriRef);
        // test if there is only one entity referred
        assertFalse(entityReferenceIterator.hasNext());

        // finally test if the entity label is set
        Iterator<Triple> entityLabelIterator = g.filter(entityAnnotation,
                Properties.FISE_ENTITY_LABEL, null);
        assertTrue(entityLabelIterator.hasNext());
    }
}
