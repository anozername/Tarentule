package app.test;

import app.engine.LoadBalancer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by pitton on 2017-02-20.
 */
@Path("/test/engine")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestEngine {
    @GET
    public Response getJsonResponse() {
        //Création de notre tâche principale qui se charge de découper son travail en sous-tâches
        LoadBalancer loadBalancer = new LoadBalancer();

        Long start = System.currentTimeMillis();
        long result = loadBalancer.pool().invoke(loadBalancer); //compute()
        Long end = System.currentTimeMillis();

        return Response.status(Response.Status.OK).entity(new ResponseEngine(end - start, result)).build();
    }

    public class ResponseEngine {
        Long temps;
        Long response;

        ResponseEngine(Long temps, Long response) {
            setTemps(temps);
            setResponse(response);
        }

        void setTemps(Long temps) {
            this.temps = temps;
        }

        void setResponse(Long response) {
            this.response = response;
        }
    }
}