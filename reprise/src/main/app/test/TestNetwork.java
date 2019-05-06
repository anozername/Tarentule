package main.app.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
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
            HttpResponse<JsonNode> jsonResponse = Unirest.get("http://localhost:8081/test/network").asJson();
            result = jsonResponse.getBody().getObject().toString();
            JSONObject jsonObj = new JSONObject(result);
            Long name = jsonObj.getLong("response");
            System.out.println(name);
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return Response.status(Response.Status.OK).entity(result).build();
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