import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CounterServlet extends HttpServlet {

    private Counter counter;

    @Override
    public void init() throws ServletException {
        this.counter = (Counter) getServletContext().getAttribute("counter");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(counter.getCurrentValue());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        counter.postIncrement();
        resp.getWriter().print(counter.getCurrentValue());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long subtractionValue = substationValue(req);
        if (subtractionValue == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Missing Subtraction-Value Header or wrong value");
        } else {
            counter.deleteDecrement(subtractionValue);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }


    private Long substationValue(HttpServletRequest req) {
        if (!headerIsMissing(req)) {
            return parseHeaderValue(req.getHeader("Subtraction-Value"));
        } return null;
    }

    private boolean headerIsMissing(HttpServletRequest req) {
        return req.getHeader("Subtraction-Value")  == null;
    }

    private Long parseHeaderValue(String header) {
        try { return Long.valueOf(header); }
        catch (NumberFormatException e) {
            return null;
        }
    }

}
