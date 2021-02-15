import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class ClearCounterServlet extends HttpServlet {

    private Counter counter;

    @Override
    public void init() throws ServletException {
        this.counter = (Counter) getServletContext().getAttribute("counter");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (rightAuthCookie(req)) {

            resp.setStatus(HttpServletResponse.SC_OK);
            counter.clear();
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Missing hh-auth cookie or wrong value");
        }
    }

    private boolean rightAuthCookie(HttpServletRequest req) {
        Cookie hhAuthCookie = getAuthCookie(req);
        return hhAuthCookie.getValue().length() >= 10;
    }

    private Cookie getAuthCookie(HttpServletRequest req) {
        try {
            return Arrays.stream(req.getCookies())
                    .peek(cookie -> System.out.println(cookie.getName() + cookie.getValue()))
                    .filter(cookie -> cookie.getName().equals("hh-auth"))
                    .findFirst()
                    .get();
        } catch (NullPointerException | NoSuchElementException e) {
            return new Cookie("missingCookie", "0"); }
    }

}
