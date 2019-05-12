package main.app.test;

import main.app.core.entity.Account;
import main.app.engine.LoadBalancer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by pitton on 2017-02-20.
 */
@Path("/test/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestEndpoint {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String helloWorld() {
		return "Hello World";
	}

	@GET
	@Path("/list")
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

	@GET
	@Path("/json")
	public Response json() {
		return Response.status(Response.Status.OK).entity(new ResponseEndpoint(true)).build();
	}

	public class ResponseEndpoint {
		Boolean response;
		String app = "RESTFUL API - java index";
		String id = "intellij";

		ResponseEndpoint(Boolean response) {
			setResponse(response);
		}

		void setResponse(Boolean response) {
			this.response = response;
		}
	}
}
