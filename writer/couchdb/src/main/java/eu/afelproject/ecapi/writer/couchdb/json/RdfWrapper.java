package eu.afelproject.ecapi.writer.couchdb.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

public class RdfWrapper {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static JSONObject toOrgJson(String nt, String docId) {
        Map object = new HashMap();
        object.put("_id", docId);
        JSONObject doc;
        try {
            object.put("data", JsonLdProcessor.fromRDF(nt));
            String printed = JsonUtils.toString(object);
            doc = new JSONObject(printed);
        } catch (JsonLdError | IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

}
