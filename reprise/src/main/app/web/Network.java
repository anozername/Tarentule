package main.app.web;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import main.Main;
import org.json.JSONObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by pitton on 2017-02-20.
 */
@Path("/test/network")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Network {
    @GET
    public Response network() {
        //Single
        return Response.status(Response.Status.OK).entity(new ResponseNetwork()).build();
    }

    @GET
    @Path("/list")
    @Produces(MediaType.TEXT_HTML)
    public String list() {
        return Main.externalNodes.toString();
    }

    @GET
    @Path("/benchmark")
    public Response benchmark() {
        //Total
        int max_processor = 0;
        long max_heap = 0;

        for (String externalAddresses : Main.externalNodes){
            try {
                JSONObject response = new JSONObject(Unirest.get("http://"+externalAddresses+"/test/network").asJson().getBody().getObject().toString());
                max_processor += response.getLong("processor");
                max_heap += response.getLong("heap");
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

        return Response.status(Response.Status.OK).entity(new ResponseNetwork(max_processor, max_heap)).build();
    }

    public class ResponseNetwork {
        int processor;
        long heap;

        ResponseNetwork() {
            this.processor = Runtime.getRuntime().availableProcessors();
            this.heap = Runtime.getRuntime().freeMemory();
        }

        ResponseNetwork(int max_processor, Long max_heap) {
            this.processor = max_processor;
            this.heap = max_heap;
        }
    }
}