package eu.afelproject.ecapi.writer.couchdb;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.auth.Credentials;
import org.json.JSONObject;
import org.mksmart.ecapi.api.storage.StoreWithExistence;
import org.mksmart.ecapi.commons.couchdb.client.DocumentProvider;
import org.mksmart.ecapi.commons.couchdb.client.DocumentWriter;
import org.mksmart.ecapi.commons.couchdb.client.RemoteDocumentProvider;
import org.mksmart.ecapi.commons.couchdb.client.RemoteDocumentWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import eu.afelproject.ecapi.writer.couchdb.json.RdfWrapper;

public class NtAsJsonLdStore implements StoreWithExistence<String,String> {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected DocumentProvider<JSONObject> provider;

    /*
     * TODO unify with a provider. TODO handle something better than org.json
     */
    protected DocumentWriter<JSONObject> writer;

    public NtAsJsonLdStore(String serviceUrl, String dataset) {
        this(serviceUrl, dataset, null);
    }

    public NtAsJsonLdStore(String serviceUrl, String dataset, Credentials credentials) {
        URL url;
        try {
            url = new URL(serviceUrl);
        } catch (MalformedURLException e1) {
            throw new IllegalArgumentException("serviceUrl must be a well-formed URL");
        }
        if (credentials == null) {
            this.provider = new RemoteDocumentProvider(url, dataset);
            this.writer = new RemoteDocumentWriter(url, dataset);
        } else {
            this.provider = new RemoteDocumentProvider(url, dataset, credentials);
            this.writer = new RemoteDocumentWriter(url, dataset, credentials);
        }
    }

    @Override
    public boolean exists(String key) {
        return provider.getDocument(key) != null;
    }

    @Override
    public Class<String> getSupportedKeyType() {
        return String.class;
    }

    @Override
    public Class<String> getSupportedValueType() {
        return String.class;
    }

    @Override
    public String retrieve(String key) {
        log.debug("received request for JSON-LD data extracted from document {}", key);
        JSONObject doc = provider.getDocument(key);
        if (doc == null || !doc.has("data")) return null;
        return doc.getJSONArray("data").toString();
    }

    /**
     * The document ID generation policy of this store is as follows: - Take the first subject of an rdf:type
     * property and hash it - if there is none, refuse to store.
     */
    @Override
    public String store(String data) {
        log.debug("received request to store JSON-LD with generated document ID");
        final Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(data.getBytes()), null, "N-TRIPLE");
        ResIterator it = model.listResourcesWithProperty(RDF.type);
        if (!it.hasNext()) throw new RuntimeException("No valid subject in data; refusing to store.");
        String sub = it.nextResource().getURI();
        log.debug("Storing document <{}>", sub);
        log.trace("data:\r\n{}", data);
        String stored = store(data, sub);
        log.debug("Stored as <{}>", stored);
        return sub;
    }

    @Override
    public String store(String data, String key) {
        log.debug("received request to store JSON-LD as document {}", key);
        JSONObject doc = RdfWrapper.toOrgJson(data, key);
        if (!writer.addDocument(doc, key, true)) throw new RuntimeException("Write failed for " + key);
        return key;
    }

}
