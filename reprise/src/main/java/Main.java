import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;

public class Main {
        public static void main( String[] args ) throws Exception {
            Server server = new Server(8080);

            WebAppContext root = new WebAppContext();
            root.setContextPath("/");
            root.setDescriptor("webapp/WEB-INF/web.xml");
            URL webAppDir = Thread.currentThread().getContextClassLoader().getResource("webapp");
            if (webAppDir == null) {
                throw new RuntimeException("No webapp directory was found into the JAR file");
            }
            root.setResourceBase(webAppDir.toURI().toString());
            root.setParentLoaderPriority(true);
            //App.getSingleton();
            server.setHandler(root);
            server.start();

            //Monitor monitor = new Monitor(8090, new Server[] {server});
            //monitor.start();
            server.join();
        }
}