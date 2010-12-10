package eu.iksproject.fise.engines.salsadev;

import com.thoughtworks.xstream.XStream;
import eu.iksproject.fise.engines.salsadev.core.xml.converter.CategoryConverter;
import eu.iksproject.fise.engines.salsadev.core.xml.converter.DocumentConverter;
import eu.iksproject.fise.engines.salsadev.core.xml.converter.KeywordConverter;
import eu.iksproject.fise.engines.salsadev.core.xml.converter.SearchDescriptorConverter;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class TestXml {

    @Test
    public void testKeywordsXml() {
        String keywords = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<keywords>\n"
                + "    <keyword score=\"3.0\">methods</keyword>\n"
                + "    <keyword score=\"1.0\">web-tier</keyword>\n"
                + "    <keyword score=\"1.0\">application</keyword>\n"
                + "    <keyword score=\"1.0\">simply</keyword>\n"
                + "    <keyword score=\"1.0\">code</keyword>\n"
                + "    <keyword score=\"1.0\">exposing</keyword>\n"
                + "    <keyword score=\"1.0\">approach</keyword>\n"
                + "    <keyword score=\"1.0\">class</keyword>\n"
                + "    <keyword score=\"1.0\">glue</keyword>\n"
                + "    <keyword score=\"1.0\">carry</keyword>\n"
                + "</keywords>";

        XStream xstream = new XStream();

        xstream.alias("keywords", KeywordList.class);
        xstream.addImplicitCollection(KeywordList.class, "keywords");

        xstream.registerConverter(new KeywordConverter());
        xstream.alias("keyword", Keyword.class);

        KeywordList keywordList = (KeywordList) xstream.fromXML(keywords);
        Assert.assertNotNull(keywordList.getKeywords());
        Assert.assertEquals(10, keywordList.getKeywords().size());
    }

    @Test
    public void testCategoriesXml() {
        String categories = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<categoryHits>\n"
                + "    <categoryHit score=\"NaN\" name=\"IT\"/>\n"
                + "</categoryHits>";

        XStream xStream = new XStream();
        xStream.alias("categoryHits", CategoryList.class);
        xStream.addImplicitCollection(CategoryList.class, "categories");

        xStream.registerConverter(new CategoryConverter());
        xStream.alias("categoryHit", Category.class);

        CategoryList categoryListDto = (CategoryList) xStream.fromXML(categories);
        Assert.assertNotNull(categoryListDto);
        Assert.assertEquals(1, categoryListDto.getCategories().size());
    }

    @Test
    public void testDocumentHitListDtoXmls() throws IOException {
        String searchResponseString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<documentHits>"
                + "   <documentHit uid=\"un-named-content-9ef937d2-1a43-4d08-aad0-f57c28e55fc9\">"
                + "       <metadata name=\"gibraltar_facade\">UI indexing connector</metadata>"
                + "       <metadata name=\"sd:source\">un-named-content-9ef937d2-1a43-4d08-aad0-f57c28e55fc9</metadata>"
                + "       <metadata name=\"title\">IT doc</metadata>"
                + "       <snippet>With this approach there are no requirements on the signature of the methods that carry "
                + "                out action execution, nor is there any requirement to extend from a Web Flow specific "
                + "                base class. Basically, you are not required to write a custom Action  ..."
                + "       </snippet>"
                + "       <score>0.9502727246990746</score>"
                + "   </documentHit>" + "</documentHits>";

        XStream xStream = new XStream();
        xStream.alias("documentHits", DocumentList.class);
        xStream.addImplicitCollection(DocumentList.class, "documents");

        xStream.registerConverter(new DocumentConverter());
        xStream.alias("documentHit", Document.class);

        DocumentList documentList = (DocumentList) xStream.fromXML(searchResponseString);
        Assert.assertNotNull(documentList);
        Assert.assertEquals(1, documentList.getDocuments().size());

    }

    @Test
    public void testSearchDescriptorXml() {
        String query = "Web Flow specific base class. Basically, you are not required to write a custom Action implementation at all--you simply instruct Spring Web Flow to call your business methods directly. The need for custom \"glue code\" to bind your web-tier to your middle-tier is eliminated.";

        String goodSDXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<searchdescriptor type=\"search\" threshold=\"0.0\" page=\"0\" numresults=\"5\">"
                + "   <query>Web Flow specific base class. Basically, you are not required to write a custom Action "
                + "          implementation at all--you simply instruct Spring Web Flow to call your business methods "
                + "          directly. The need for custom &quot;glue code&quot; to bind your web-tier to your "
                + "          middle-tier is eliminated."
                + "   </query>"
                + "   <queryconstrain operator=\"AND\">"
                + "       <atomic operator=\"EQUALS\" key=\"title\">IT doc</atomic>"
                + "       <atomic operator=\"EQUALS\" key=\"gibraltar_facade\">UI indexing connector</atomic>"
                + "       <atomic operator=\"EQUALS\" key=\"sd:source\">un-named-content-9ef937d2-1a43-4d08-aad0-f57c28e55fc9</atomic>"
                + "   </queryconstrain>" + "</searchdescriptor>";

        String badSDXmlString = "<com.salsadev.domain.api.search.SearchDescriptor>\n"
                + "  <__page>0</__page>\n"
                + "  <__type>search</__type>\n"
                + "  <__numresults>5</__numresults>\n"
                + "  <__threshold>0.0</__threshold>\n"
                + "  <__query>Web Flow specific base class. Basically, you are not required to write a custom Action implementation at all--you simply instruct Spring Web Flow to call your business methods directly. The need for custom &quot;glue code&quot; to bind your web-tier to your middle-tier is eliminated.</__query>\n"
                + "</com.salsadev.domain.api.search.SearchDescriptor>";

        SearchDescriptor searchDescriptor = new SearchDescriptor();
        searchDescriptor.setNumresults(5);
        searchDescriptor.setPage(0);
        searchDescriptor.setType("search");
        searchDescriptor.setThreshold(0);
        searchDescriptor.setQuery(query);

        List<AtomicTerm> atomicTerms = new ArrayList<AtomicTerm>();
        AtomicTerm atomicTerm = new AtomicTerm();
        atomicTerm.setKey("title");
        atomicTerm.setOperator("EQUALS");
        atomicTerm.setValue("IT doc");
        atomicTerms.add(atomicTerm);

        atomicTerm = new AtomicTerm();
        atomicTerm.setKey("gibraltar_facade");
        atomicTerm.setOperator("EQUALS");
        atomicTerm.setValue("UI indexing connector");
        atomicTerms.add(atomicTerm);

        atomicTerm = new AtomicTerm();
        atomicTerm.setKey("sd:source");
        atomicTerm.setOperator("EQUALS");
        atomicTerm.setValue("un-named-content-9ef937d2-1a43-4d08-aad0-f57c28e55fc9");
        atomicTerms.add(atomicTerm);

        ConstrainTerm constrainTerm = new ConstrainTerm();
        constrainTerm.setAtomics(atomicTerms);
        constrainTerm.setOperator("AND");
        searchDescriptor.setQueryconstrain(constrainTerm);

        /*
         * JAXBContext jaxbContext =
         * JAXBContext.newInstance(DocumentHitListDto.class,
         * DocumentHitListItemDto.class, DocumentSummaryDto.class,
         * DocumentDto.class, SearchDescriptor.class, ConstrainTerm.class,
         * AtomicTerm.class);
         *
         * Marshaller marshaller = jaxbContext.createMarshaller();
         *
         * marshaller.marshal(searchDescriptor, System.out);
         */

        XStream xStream = new XStream();
        xStream.registerConverter(new SearchDescriptorConverter());
        xStream.alias("searchdescriptor", SearchDescriptor.class);

        System.out.println(xStream.toXML(searchDescriptor));

    }
}
