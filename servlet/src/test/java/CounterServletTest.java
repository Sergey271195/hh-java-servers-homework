import org.junit.jupiter.api.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CounterServletTest extends BaseTest {

    private int REPEATS = 10;
    private int SUBSTRACTION_VALUE = 6;

    @Test
    @Order(1)
    public void testInitialGetRequest() {
        HttpResponse<String> response = getCurrentCounterResponse();
        Assertions.assertEquals(200, response.statusCode());
        long counterValue = Long.parseLong(response.body());
        Assertions.assertEquals(0L, counterValue);
    }

    @Test
    @Order(2)
    public void testInitialPostRequest() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(0L, initialCounterValue);

        HttpRequest request = createPostRequest("http://localhost:8081/counter");
        HttpResponse<String> response = getResponse(request);
        Assertions.assertEquals(200, response.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, postIncrementCounterValue);
    }

    @Test
    @Order(3)
    public void testClearCounterRequestWithMissingCookies() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, initialCounterValue);

        HttpRequest clearCounterRequest = createPostRequest("http://localhost:8081/counter/clear");
        HttpResponse<String> clearResponse = getResponse(clearCounterRequest);
        Assertions.assertEquals(400, clearResponse.statusCode());
        Assertions.assertEquals("Missing hh-auth cookie or wrong value", clearResponse.body());

        long postClearCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, postClearCounterValue);
    }

    @Test
    @Order(4)
    public void testClearCounterRequestWithWrongCookieName() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, initialCounterValue);

        HttpRequest clearCounterRequest =
                createPostRequestWithHeader(
                        "http://localhost:8081/counter/clear",
                        "Cookie", "hh-auths=longenoughvalue"
                );
        HttpResponse<String> clearResponse = getResponse(clearCounterRequest);
        Assertions.assertEquals(400, clearResponse.statusCode());
        Assertions.assertEquals("Missing hh-auth cookie or wrong value", clearResponse.body());

        long postClearCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, postClearCounterValue);
    }

    @Test
    @Order(5)
    public void testClearCounterRequestWithWrongCookieLength() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, initialCounterValue);

        HttpRequest clearCounterRequest =
                createPostRequestWithHeader(
                        "http://localhost:8081/counter/clear",
                        "Cookie", "hh-auth=tooshort"
                );
        HttpResponse<String> clearResponse = getResponse(clearCounterRequest);
        Assertions.assertEquals(400, clearResponse.statusCode());
        Assertions.assertEquals("Missing hh-auth cookie or wrong value", clearResponse.body());

        long postClearCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, postClearCounterValue);
    }

    @Test
    @Order(6)
    public void testClearCounterRequestWithRightCookie() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(1, initialCounterValue);

        HttpRequest clearCounterRequest =
                createPostRequestWithHeader(
                        "http://localhost:8081/counter/clear",
                        "Cookie", "hh-auth=longenoughvalue"
                );
        HttpResponse<String> clearResponse = getResponse(clearCounterRequest);
        Assertions.assertEquals(200, clearResponse.statusCode());

        long postClearCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(0, postClearCounterValue);
    }

    @Test
    @Order(7)
    public void testRepeatedPostRequest() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(0, initialCounterValue);

        for (int i = 0; i < REPEATS; i++) {
            incrementCounterWithPostRequest();
        }

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, postIncrementCounterValue);
    }

    @Test
    @Order(8)
    public void testDeleteRequestWithWrongHeaderName() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, initialCounterValue);

        HttpRequest deleteRequest =
                createDeleteRequestWithHeader(
                        "http://localhost:8081/counter",
                "Authorization", "Token not_so_random_token"
                );
        HttpResponse deleteResponse = getResponse(deleteRequest);
        Assertions.assertEquals(400, deleteResponse.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, postIncrementCounterValue);
    }

    @Test
    @Order(9)
    public void testDeleteRequestWithWrongHeaderValue() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, initialCounterValue);

        HttpRequest deleteRequest =
                createDeleteRequestWithHeader(
                        "http://localhost:8081/counter",
                        "Subtraction-Value", "Token not_so_random_token"
                );
        HttpResponse deleteResponse = getResponse(deleteRequest);
        Assertions.assertEquals(400, deleteResponse.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, postIncrementCounterValue);
    }

    @Test
    @Order(10)
    public void testDeleteRequestWithRightHeaderValue() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS, initialCounterValue);

        HttpRequest deleteRequest =
                createDeleteRequestWithHeader(
                        "http://localhost:8081/counter",
                        "Subtraction-Value", String.valueOf(SUBSTRACTION_VALUE)
                );
        HttpResponse deleteResponse = getResponse(deleteRequest);
        Assertions.assertEquals(200, deleteResponse.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS - SUBSTRACTION_VALUE, postIncrementCounterValue);
    }

    @Test
    @Order(11)
    public void testCounterCantBeNegative() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(REPEATS - SUBSTRACTION_VALUE, initialCounterValue);

        HttpRequest deleteRequest =
                createDeleteRequestWithHeader(
                        "http://localhost:8081/counter",
                        "Subtraction-Value", String.valueOf(SUBSTRACTION_VALUE)
                );
        HttpResponse deleteResponse = getResponse(deleteRequest);
        Assertions.assertEquals(200, deleteResponse.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(0, postIncrementCounterValue);
    }

    @Test
    @Order(12)
    public void testDeleteRequestWithNegativeValue() {
        long initialCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(0, initialCounterValue);

        int negativeValue = -10;

        HttpRequest deleteRequest =
                createDeleteRequestWithHeader(
                        "http://localhost:8081/counter",
                        "Subtraction-Value", String.valueOf(negativeValue)
                );
        HttpResponse deleteResponse = getResponse(deleteRequest);
        Assertions.assertEquals(200, deleteResponse.statusCode());

        long postIncrementCounterValue = getCurrentCounterValue();
        Assertions.assertEquals(Math.abs(negativeValue), postIncrementCounterValue);
    }



}
