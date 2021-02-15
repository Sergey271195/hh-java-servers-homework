package application;

import application.serilalization.CounterResponse;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.LocalDateTime;

@Path("/counter")
public class RestApp {

    private Counter counter;
    private final String hhAuthCookie = "hh-auth";
    private final String subtractHeader = "Subtraction-Value";

    public RestApp(@Context ServletContext context) {
        counter = (Counter) context.getAttribute("counter");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCounter(@Context ServletContext context) {
        CounterResponse response = new CounterResponse(counter.getValue(), LocalDateTime.now());
        return Response.ok().entity(response).build();
    }

    @POST
    public Response incrementCounter() {
        counter.postIncrement();
        return Response.ok().build();
    }

    @DELETE
    public Response subtractCounter(@Context HttpHeaders headers) {
        Long subtractValue = parseHeaderValue(headers.getHeaderString(subtractHeader));
        if (subtractValue != null) {
            counter.deleteDecrement(subtractValue);
            return Response.ok().build();
        } return Response.status(HttpStatus.BAD_REQUEST_400)
                .entity("Missing Subtraction-Value Header or wrong value")
                .build();
    }

    @POST
    @Path(value = "/clear")
    public Response clearCounter(@Context HttpHeaders headers) {
        Cookie authCookie = headers.getCookies().get(hhAuthCookie);
        if (authCookie != null && authCookie.getValue().length() >= 10) {
            counter.clear();
            return Response.ok().build();
        } return Response.status(HttpStatus.UNAUTHORIZED_401)
                .entity("Missing hh-auth cookie or wrong value")
                .build();

    }

    private Long parseHeaderValue(String header) {
        try { return Long.valueOf(header); }
        catch (NumberFormatException e) { return null; }
    }

}
