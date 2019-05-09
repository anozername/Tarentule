package main.app.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import main.Main;
import main.app.engine.LoadBalancer;
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
public class TestNetwork {
    @GET
    public Response getJsonResponse() {
        String result = "";

        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get("http://localhost:8081/test/engine").asJson();
            result = jsonResponse.getBody().getObject().toString();
            JSONObject jsonObj = new JSONObject(result);
            Long response = jsonObj.getLong("response");
            System.out.println(response);
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return Response.status(Response.Status.OK).entity(result).build();
    }
    @GET
    @Path("/list")
    @Produces(MediaType.TEXT_HTML)
    public String list() {
        return Main.externalNodes.toString();
    }

    @GET
    @Path("/json")
    public Response json() {
        return Response.status(Response.Status.OK).entity(new ResponseEngine(0L,100L)).build();
    }

    public class ResponseEngine {
        Long time;
        Long response;

        ResponseEngine(Long time, Long response) {
            setTemps(time);
            setResponse(response);
        }

        void setTemps(Long time) {
            this.time = time;
        }

        void setResponse(Long response) {
            this.response = response;
        }
    }
}