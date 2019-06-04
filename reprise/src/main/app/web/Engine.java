package main.app.web;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import main.app.core.entity.Lines;
import main.app.engine.LoadBalancer;
import main.app.engine.Node;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by pitton on 2017-02-20.
 */
@Path("/test/engine")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Engine {
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getJsonResponse() {
        LoadBalancer loadBalancer = new LoadBalancer();

        String response_string = "";
        Long start = System.currentTimeMillis();
        Future<HttpResponse<JsonNode>> future = Unirest.post("http://localhost:8080/test/engine/work").header("accept", "application/json").field("beginning", 1).field("file", "file").field("query", "query").field("ending", 9).asJsonAsync();
        try {
            HttpResponse<JsonNode> response = future.get();
            String result = response.getBody().getObject().toString();
            JSONObject jsonObj = new JSONObject(result);
            response_string += jsonObj.get("result");
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Long end = System.currentTimeMillis();

        return response_string;
        //return Response.status(Response.Status.OK).entity(new ResponseEngine(end - start, result)).build();
    }

    public class ResponseEngine {
        Long time;
        String response;

        ResponseEngine(Long time, String response) {
            this.time = time;
            this.response = response;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/work")
    public Response work(@FormParam("beginning") int beginning, @FormParam("file") String file, @FormParam("query") String query, @FormParam("ending") int ending) {
        Node node = new Node(true,beginning,file,query,ending);

        Long start = System.currentTimeMillis();
        String result = node.pool().invoke(node)+""; //compute()
        Long end = System.currentTimeMillis();

        return Response.status(Response.Status.OK).entity(new ResponseWork(end - start, result)).build();
    }

    public class ResponseWork {
        long time;
        String result;

        ResponseWork(long time, String result) {
            this.time = time;
            this.result = result;
        }
    }
}