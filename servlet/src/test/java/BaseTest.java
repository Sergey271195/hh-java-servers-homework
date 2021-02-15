import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class BaseTest {

    private static Server server;


    @BeforeAll
    public static void setUp() throws Exception {
        server = new EmbeddedServer().startServer();
    }

    @AfterAll
    public static void shutDown() throws Exception {
        server.stop();
    }

    protected void incrementCounterWithPostRequest() {
        HttpRequest request = createPostRequest("http://localhost:8081/counter");
        getResponse(request);
    }

    protected long getCurrentCounterValue() {
        HttpResponse<String> counterResponse = getCurrentCounterResponse();
        String responseBody = counterResponse.body();
        return Long.parseLong(responseBody);
    }

    protected HttpResponse<String> getCurrentCounterResponse() {
        HttpRequest request = createGetRequest("http://localhost:8081/counter");
        HttpResponse<String> response = getResponse(request);
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

    protected HttpResponse<String> getResponse(HttpRequest request) {
        try {
            return HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
            return null; }
    }

}
