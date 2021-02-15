package application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class EmbeddedServer {

    public Server createServer() {
        Server server = new Server(8081);
        ServletContextHandler context = new ServletContextHandler();
        context.setAttribute("counter", new Counter());
        server.setHandler(context);
        ServletHolder holder = context.addServlet(ServletContainer.class, "/*");
        holder.setInitOrder(1);
        holder.setInitParameter("javax.ws.rs.Application", "application.ApplicationConfig");
        return server;
    }

    public void run() throws Exception {
        Server server = createServer();
        server.start();
        server.join();
    }

}
