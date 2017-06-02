package eu.afelproject.ecapi.writer.couchdb;

import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import com.github.jsonldjava.jena.JenaJSONLD;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class JenaIntegrationTest {

    @Test
    public void testSimpleBean() throws Exception {
        Model object = ModelFactory.createDefaultModel();
        object.add(ResourceFactory.createResource("http://www.wikidata.org/entity/Q190"), RDF.type,
            ResourceFactory.createResource("http://www.wikidata.org/entity/Q787"));
        RDFDataMgr.write(System.out, object, JenaJSONLD.JSONLD_FORMAT_FLAT);
        // String serialized = JsonUtils.toString(object);
        // assertNotNull(serialized);
        // System.out.println(serialized);
        // assertTrue(serialized.matches(".+['\\\"]field1['\\\"]\\s*:\\s*['\\\"]value1['\\\"].+"));
    }

}
