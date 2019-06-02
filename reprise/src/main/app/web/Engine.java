package main.app.web;

import main.app.engine.LoadBalancer;
import main.app.engine.Node;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by pitton on 2017-02-20.
 */
@Path("/test/engine")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Engine {
    @GET
    public Response getJsonResponse() {
        LoadBalancer loadBalancer = new LoadBalancer();

        Long start = System.currentTimeMillis();
        long result = 0L;//loadBalancer.distribute();
        Long end = System.currentTimeMillis();

        return Response.status(Response.Status.OK).entity(new ResponseEngine(end - start, result)).build();
    }

    public class ResponseEngine {
        Long time;
        Long response;

        ResponseEngine(Long time, Long response) {
            this.time = time;
            this.response = response;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/work")
    public Response work(@FormParam("beginning") int beginning, @FormParam("file") String file, @FormParam("ending") int ending) {
        Node node = new Node(true,beginning,ending);

        Long start = System.currentTimeMillis();
        long result = node.pool().invoke(node); //compute()
        Long end = System.currentTimeMillis();

        return Response.status(Response.Status.OK).entity(new ResponseWork(end - start, result)).build();
    }

    public class ResponseWork {
        long time;
        long result;

        ResponseWork(long time, long result) {
            this.time = time;
            this.result = result;
        }
    }
}