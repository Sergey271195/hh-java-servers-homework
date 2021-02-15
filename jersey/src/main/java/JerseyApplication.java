import application.EmbeddedServer;

public class JerseyApplication {
  public static void main(String[] args) throws Exception {
    new EmbeddedServer().run();
  }
}
