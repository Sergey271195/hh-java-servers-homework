package application;

import application.serilalization.CounterResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class BaseTest {

    private static Server server;
    protected ObjectMapper mapper = new ObjectMapper();

    protected final String BASE_URL = "http://localhost";
    protected final int PORT = 8081;

    @BeforeEach
    public void setUp() throws Exception {
        server = new EmbeddedServer().createServer();
        server.start();
    }

    @AfterEach
    public void shutDown() throws Exception {
        server.stop();
    }

    protected void incrementCounterWithPostRequest() {
        HttpRequest request = createPostRequest(BASE_URL + ":" + PORT + "/counter");
        executeRequest(request);
    }

    protected Long getCurrentCounterValue() throws IOException {
        HttpResponse<String> counterResponse = getCurrentCounterResponse();
        CounterResponse response = mapper.readValue(counterResponse.body(), CounterResponse.class);
        return response.getValue();
    }

    protected HttpResponse<String> getCurrentCounterResponse() {
        HttpRequest request = createGetRequest(BASE_URL + ":" + PORT + "/counter");
        HttpResponse<String> response = executeRequest(request);
        return response;
    }

    protected HttpRequest createGetRequest(String url) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
        } catch (URISyntaxException e) { return null; }
    }

    protected HttpRequest createPostRequest(String url) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.noBody()).build();
        } catch (URISyntaxException e) { return null; }
    }

    protected HttpRequest createPostRequestWithHeader(String url, String headerName, String headerValue) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header(headerName, headerValue)
                    .POST(HttpRequest.BodyPublishers.noBody()).build();
        } catch (URISyntaxException e) { return null; }
    }

    protected HttpRequest createDeleteRequestWithHeader(String url, String headerName, String headerValue) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header(headerName, headerValue)
                    .DELETE().build();
        } catch (URISyntaxException e) { return null; }
    }

    protected HttpResponse<String> executeRequest(HttpRequest request) {
        try {
            return HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

    }

}
