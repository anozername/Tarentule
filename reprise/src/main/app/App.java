package main.app;

import main.app.test.TestEndpoint;
import main.app.core.filter.GsonProvider;
import main.app.test.TestEngine;
import main.app.test.TestIndex;

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
        sets.add(new TestEngine());
        sets.add(new TestIndex());
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
