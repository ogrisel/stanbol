package eu.iksproject.fise.engines.zemanta.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.EnumMap;
import java.util.Map;

import org.apache.clerezza.rdf.core.Graph;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.apache.clerezza.rdf.jena.parser.JenaParserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class wraps the Zemanta API into one method.
 * Zemanta is able to return RDF-XML so parsing the response into
 * a Graph object is simple.
 *
 * @author michaelmarth
 * @author westei (Rupert Westenthaler)
 */
public class ZemantaAPIWrapper {

    private static final Logger log = LoggerFactory.getLogger(ZemantaEnhancementEngine.class);

    private static String apiKey;

    private static final String URL = "http://api.zemanta.com/services/rest/0.0/";

    public ZemantaAPIWrapper(String key) {
        apiKey = key;
    }

    public Graph enhance(String textToAnalyze) {
        InputStream is = sendRequest(textToAnalyze);
        Graph zemantaResponseGraph = parseResponse(is);
        return zemantaResponseGraph;
    }

    private InputStream sendRequest(String textToAnalyze) {
        try {
            URL url = new URL(URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            Map<ZemantaPropertyEnum,String> requestProperties = new EnumMap<ZemantaPropertyEnum, String>(ZemantaPropertyEnum.class);
            requestProperties.put(ZemantaPropertyEnum.api_key, apiKey);
            requestProperties.put(ZemantaPropertyEnum.text, URLEncoder.encode(textToAnalyze, "UTF8"));
            //added an method that adds the default parameters
            StringBuilder data = getRequestData(requestProperties);
//            StringBuilder data = new StringBuilder();

//            data.append("method=zemanta.suggest&").append("api_key=").append(
//                    apiKey).append("&").append("format=rdfxml&").append(
//                    "freebase=1").append("&text=").append(
//                    URLEncoder.encode(textToAnalyze, "UTF8"));

            httpURLConnection.addRequestProperty("Content-Length", Integer
                    .toString(data.length()));

            log.info("sending data to Zemanta: " + data);

            DataOutputStream dataOutputStream = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dataOutputStream.write(data.toString().getBytes());
            dataOutputStream.close();

            InputStream is = httpURLConnection.getInputStream();
            return is;

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(),e);
        } catch (MalformedURLException e) {
            log.error(e.getMessage(),e);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }

        return null;
    }

    private StringBuilder getRequestData(Map<ZemantaPropertyEnum,String> requestProperties){
        StringBuilder data = new StringBuilder();
        boolean first = true;
        for (ZemantaPropertyEnum property : ZemantaPropertyEnum.values()){
            String value = requestProperties.get(property);
            if (value == null && property.hasDefault()) {
                value = property.defaultValue();
            }
            //NOTE: value == null may still be OK
            if (property.allowedValue(value)) {
                //NOTE: also this may still say that NULL is OK
                if (value != null) {
                    if (!first) {
                        data.append('&');
                    } else {
                        first = false;
                    }
                    data.append(property.name());
                    data.append('=');
                    data.append(value);
                } //else property is not present
            } else {
                //Illegal parameter
                log.warn("Value "+value+" is not valied for property "+property);
            }
        }
        return data;
    }

    private Graph parseResponse(InputStream is) {
        JenaParserProvider jenaParserProvider = new JenaParserProvider();
        //NOTE(rw): the new third parameter is the base URI used to resolve relative paths
        Graph g = jenaParserProvider.parse(is, SupportedFormat.RDF_XML,null);
        log.debug("graph: " + g.toString());
        return g;
    }

}
