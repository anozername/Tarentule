package main.app;

import main.app.web.Endpoint;
import main.app.core.filter.GsonProvider;
import main.app.web.Engine;
import main.app.web.Index;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("")
public class App extends Application {

    @Override
    public Set<Object> getSingletons() {
        Set<Object> sets = new HashSet<>(1);
        sets.add(new Endpoint());
        sets.add(new Engine());
        sets.add(new Index());

        return sets;
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> sets = new HashSet<>(1);
        sets.add(GsonProvider.class);
        sets.add(RuntimeException.class);
        return sets;
    }

}
