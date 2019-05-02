package app.engine;

import app.core.entity.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by pitton on 2017-02-31.
 */
@Path("/node")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoadBalancer {
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String helloWorld() {
        Node t = new Node("A");
        Node t2 = new Node("  B");
        t.start();
        t2.start();
        return "Node Engine";
    }

    @GET
    @Path("/add")
    public List<String> getListInParams(@QueryParam("ids") List<String> ids) {
        System.out.println(ids);
        return ids;
    }

    @POST
    @Path("/entity")
    public Account getAccount(Account account) {
        System.out.println("Received account " + account);
        account.setUpdated(System.currentTimeMillis());
        return account;
    }

    @GET
    @Path("/exception")
    public Response exception() {
        throw new RuntimeException("Mon erreur");
    }

}
