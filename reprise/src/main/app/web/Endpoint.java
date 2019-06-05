package main.app.web;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/test/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Endpoint {
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

	public static class ResponseEndpoint {
		Boolean response;
		String app = "RESTFUL API - java index";
		String id = "intellij";

		public ResponseEndpoint(Boolean response) {
			setResponse(response);
		}

		void setResponse(Boolean response) {
			this.response = response;
		}
	}
}
