package app;

import app.core.search.TestEndpoint;
import app.core.search.TestIndex;
import app.engine.LoadBalancer;
import app.engine.exception.RuntimeExceptionMapper;
import app.core.filter.GsonProvider;
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
		sets.add(new LoadBalancer());
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
