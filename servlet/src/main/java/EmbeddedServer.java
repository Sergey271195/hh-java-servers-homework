import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

public class EmbeddedServer {

    private Server server;
    private Counter counter = new Counter();

    public void run() throws Exception {
        server = new Server(8081);
        ServletContextHandler context = prepareContext();
        server.setHandler(context);
        server.start();
        server.join();
    }

    public Server startServer() throws Exception {
        server = new Server(8081);
        ServletContextHandler context = prepareContext();
        server.setHandler(context);
        server.start();
        return server;
    }

    private ServletContextHandler prepareContext() {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.setAttribute("counter", counter);
        context.addServlet(CounterServlet.class, "/counter");
        context.addServlet(ClearCounterServlet.class, "/counter/clear");
        return context;
    }

}
