package application;

import application.serilalization.CounterResponse;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JerseyAppTest extends BaseTest {

    private final int REPEATS = 10;
    private final int SUBTRACTION_VALUE = 6;

    @Test
    public void testInitialGetRequest() throws IOException {
        HttpResponse<String> response = getCurrentCounterResponse();
        Assertions.assertEquals(200, response.statusCode());
        CounterResponse counterResponse = mapper.readValue(response.body(), CounterResponse.class);
        Assertions.assertEquals(0L, counterResponse.getValue());
    }

    @Test
    public void testInitialPostRequest() throws IOException {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(0L, initialCounterValue);

        HttpRequest request = createPostRequest(BASE_URL + ":" + PORT + "/counter");
        HttpResponse<String> response = executeRequest(request);
        Assertions.assertEquals(200, response.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, postIncrementCounterValue);
    }

    @Test
    public void testClearCounterRequestWithMissingCookies() throws IOException {
        incrementCounterWithPostRequest();
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, initialCounterValue);

        HttpRequest clearCounterRequest = createPostRequest(BASE_URL + ":" + PORT + "/counter/clear");
        HttpResponse<String> clearResponse = executeRequest(clearCounterRequest);
        Assertions.assertEquals(401, clearResponse.statusCode());
        Assertions.assertEquals("Missing hh-auth cookie or wrong value", clearResponse.body());

        long postClearCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, postClearCounterValue);
    }

    @Test
    public void testClearCounterRequestWithWrongCookieName() throws IOException {
        incrementCounterWithPostRequest();
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, initialCounterValue);

        HttpRequest clearCounterRequest =
                createPostRequestWithHeader(
                        BASE_URL + ":" + PORT + "/counter/clear",
                        "Cookie", "hh-auths=longenoughvalue"
                );
        HttpResponse<String> clearResponse = executeRequest(clearCounterRequest);
        Assertions.assertEquals(401, clearResponse.statusCode());
        Assertions.assertEquals("Missing hh-auth cookie or wrong value", clearResponse.body());

        long postClearCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, postClearCounterValue);
    }

    @Test
    public void testClearCounterRequestWithWrongCookieLength() throws IOException {
        incrementCounterWithPostRequest();
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, initialCounterValue);

        HttpRequest clearCounterRequest =
                createPostRequestWithHeader(
                        BASE_URL + ":" + PORT + "/counter/clear",
                        "Cookie", "hh-auth=tooshort"
                );
        HttpResponse<String> clearResponse = executeRequest(clearCounterRequest);
        Assertions.assertEquals(401, clearResponse.statusCode());
        Assertions.assertEquals("Missing hh-auth cookie or wrong value", clearResponse.body());

        long postClearCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, postClearCounterValue);
    }

    @Test
    public void testClearCounterRequestWithRightCookie() throws IOException {
        incrementCounterWithPostRequest();
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, initialCounterValue);

        HttpRequest clearCounterRequest =
                createPostRequestWithHeader(
                        BASE_URL + ":" + PORT + "/counter/clear",
                        "Cookie", "hh-auth=longenoughvalue"
                );
        HttpResponse<String> clearResponse = executeRequest(clearCounterRequest);
        Assertions.assertEquals(200, clearResponse.statusCode());

        long postClearCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(0, postClearCounterValue);
    }

    @Test
    public void testRepeatedPostRequest() throws IOException {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(0, initialCounterValue);

        for (int i = 0; i < REPEATS; i++) {
            incrementCounterWithPostRequest();
        }

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, postIncrementCounterValue);
    }

    @Test
    public void testDeleteRequestWithWrongHeaderName() throws IOException {

        for (int i = 0; i < REPEATS; i++) {
            incrementCounterWithPostRequest();
        }

        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, initialCounterValue);

        HttpRequest deleteRequest =
                createDeleteRequestWithHeader(
                        BASE_URL + ":" + PORT + "/counter",
                "Authorization", "Token not_so_random_token"
                );
        HttpResponse deleteResponse = executeRequest(deleteRequest);
        Assertions.assertEquals(400, deleteResponse.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, postIncrementCounterValue);
    }

    @Test
    public void testDeleteRequestWithWrongHeaderValue() throws IOException {

        for (int i = 0; i < REPEATS; i++) {
            incrementCounterWithPostRequest();
        }

        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, initialCounterValue);

        HttpRequest deleteRequest =
                createDeleteRequestWithHeader(
                        BASE_URL + ":" + PORT + "/counter",
                        "Subtraction-Value", "Token not_so_random_token"
                );
        HttpResponse deleteResponse = executeRequest(deleteRequest);
        Assertions.assertEquals(400, deleteResponse.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, postIncrementCounterValue);
    }

    @Test
    public void testDeleteRequestWithRightHeaderValue() throws IOException {

        for (int i = 0; i < REPEATS; i++) {
            incrementCounterWithPostRequest();
        }

        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, initialCounterValue);

        HttpRequest deleteRequest =
                createDeleteRequestWithHeader(
                        BASE_URL + ":" + PORT + "/counter",
                        "Subtraction-Value", String.valueOf(SUBTRACTION_VALUE)
                );
        HttpResponse deleteResponse = executeRequest(deleteRequest);
        Assertions.assertEquals(200, deleteResponse.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS - SUBTRACTION_VALUE, postIncrementCounterValue);
    }

    @Test
    public void testDeleteRequestWithNegativeValue() throws IOException {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(0, initialCounterValue);

        int negativeValue = -10;

        HttpRequest deleteRequest =
                createDeleteRequestWithHeader(
                        BASE_URL + ":" + PORT + "/counter",
                        "Subtraction-Value", String.valueOf(negativeValue)
                );
        HttpResponse deleteResponse = executeRequest(deleteRequest);
        Assertions.assertEquals(200, deleteResponse.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(Math.abs(negativeValue), postIncrementCounterValue);
    }

    private int testInt = 0;

    private void increment() {
        testInt++;
    }

    @Test
    public void threadSafetyTest() throws IOException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 100; i++) {
            threadPool.submit(() -> incrementCounterWithPostRequest());
        }
        threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(Math.abs(100), postIncrementCounterValue);
    }

}
