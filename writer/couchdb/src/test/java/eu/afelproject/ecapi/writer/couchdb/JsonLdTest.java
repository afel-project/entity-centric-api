package eu.afelproject.ecapi.writer.couchdb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

import eu.afelproject.ecapi.writer.couchdb.json.RdfWrapper;

public class JsonLdTest {

    String data = "<http://www.wikidata.org/entity/Q190>"
                  + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"
                  + " <http://www.wikidata.org/entity/Q787> .";

    @Test
    public void testNt() throws Exception {
        Object jsonObject = JsonLdProcessor.fromRDF(data);
        assertNotNull(jsonObject);
        assertTrue(jsonObject instanceof List);
        String printed = JsonUtils.toString(jsonObject);
        assertNotNull(printed);
        assertTrue(printed.matches(".+['\\\"]@id['\\\"].+,.*['\\\"]@type['\\\"].+"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testNtEmbedded() throws Exception {
        Map object = new HashMap();
        object.put("_id", "0123456789abcdef");
        object.put("data", JsonLdProcessor.fromRDF(data));
        String printed = JsonUtils.toString(object);
        assertNotNull(printed);
        assertTrue(printed.contains("\"_id\":\""));
        assertTrue(printed.contains("\"data\":[{"));
    }

    @Test
    public void testOrgJsonConvert() throws Exception {
        JSONObject converted = RdfWrapper.toOrgJson(data, "0123456789abcdef");
        assertNotNull(converted);
        assertTrue(converted.has("_id"));
        assertTrue(converted.has("data"));
        assertTrue(converted.get("data") instanceof JSONArray);
        assertTrue(1 == converted.getJSONArray("data").length());
    }

    @Test
    public void testSimpleBean() throws Exception {
        DummyBean jsonObject = new DummyBean();
        jsonObject.setField1("value1");
        String serialized = JsonUtils.toString(jsonObject);
        assertNotNull(serialized);
        assertTrue(serialized.matches(".+['\\\"]field1['\\\"]\\s*:\\s*['\\\"]value1['\\\"].+"));
    }

}
