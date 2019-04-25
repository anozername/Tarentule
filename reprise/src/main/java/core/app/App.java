package core.app;

import core.exception.RuntimeExceptionMapper;
import core.filter.GsonProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("")
public class App extends Application {

	@Override
	public Set<Object> getSingletons() {
		Set<Object> sets = new HashSet<>(1);
		sets.add(new TestEndpoint());
		sets.add(new TestIndex());
		return sets;
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> sets = new HashSet<>(1);
		sets.add(GsonProvider.class);
		sets.add(RuntimeExceptionMapper.class);
		return sets;
	}

}
