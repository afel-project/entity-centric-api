package eu.afelproject.ecapi.writer.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mksmart.ecapi.api.storage.StoreWithExistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleJSONStore implements StoreWithExistence<String,String> {

    private Logger log = LoggerFactory.getLogger(getClass());
    private Credentials credentials;
    private URI url;

    public SimpleJSONStore(String serviceUrl, String dataset) {
        this(serviceUrl, dataset, null);
    }

    public SimpleJSONStore(String serviceUrl, String dataset, Credentials credentials) {
        try {
            this.url = URI.create(serviceUrl + "/" + dataset);
        } catch (Exception e1) {
            throw new IllegalArgumentException("serviceUrl must be a well-formed URL");
        }
        this.credentials = credentials;
    }

    @Override
    public boolean exists(String key) {
        // currently not implemented.
        return false;
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
        // currently not implemented
        log.debug("received request for JSON data extracted from document {}", key);
        return null;
    }

    @Override
    public String store(String data) {
        log.debug("received request to store JSON-LD");

        CredentialsProvider provider = new BasicCredentialsProvider();
        // UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("user1", "user1Pass");
        provider.setCredentials(AuthScope.ANY, credentials);

        // just send the data...
        try {
            HttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider)
                    .build();

            // DefaultHttpClient httpClient = new DefaultHttpClient();
            // httpClient.setDefaultCredentialsProvider(provider);
            HttpPost postRequest = new HttpPost(this.url);

            String una = null, pwo = null;

            if (credentials.getUserPrincipal() != null) {
                String un = new String(credentials.getUserPrincipal().getName()).trim();
                if (!un.isEmpty()) {
                    una = un;
                    if (credentials.getPassword() != null) {
                        String pw = new String(credentials.getPassword()).trim();
                        if (!pw.isEmpty()) {
                            pwo = pw;
                        }
                    }

                }
            }
            String encoding = Base64.encodeBase64String((una + ":" + pwo).getBytes());
            postRequest.setHeader("Authorization", "Basic " + encoding);

            StringEntity input = new StringEntity(data);
            input.setContentType("application/json");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String output;
            while ((output = br.readLine()) != null) {
                log.debug(output);
            }
            log.debug("<== 1 successful write to {}", this.url);
            httpClient.getConnectionManager().shutdown();
        } catch (MalformedURLException e) {
            log.error("MaleformedURLException when storing in {}", this.url);
        } catch (IOException e) {
            log.error("IOException when storing in {}", this.url, e);
        }
        return "done";
    }

    @Override
    public String store(String data, String key) {
        log.debug("received request to store JSON-LD as document {} - just sending the data", key);
        return this.store(data);
    }

}
