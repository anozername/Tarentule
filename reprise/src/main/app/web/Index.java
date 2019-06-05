package main.app.web;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import main.app.core.entity.Lines;
import main.app.core.search.*;
import main.app.engine.LoadBalancer;
import org.json.JSONObject;

@Path("/test/index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Index {
    private static Parser parser;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String string(@QueryParam("query") String query) throws Exception{
        LoadBalancer loadBalancer = new LoadBalancer();
        String result = loadBalancer.distribute(query);
        System.out.println(result);

        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/json")
    public String json(@QueryParam("query") String query) throws Exception{
        LoadBalancer loadBalancer = new LoadBalancer();
        System.out.println("query :'"+query+"'");
        String result = loadBalancer.distribute(query);
        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/insert")
    public Response insert(@QueryParam("beginning") int beginning, @QueryParam("ending") int ending) {
        System.out.println("indexing from '"+beginning+"' to '"+ending+"'");
        insertion_test(beginning, ending);
        return Response.status(Response.Status.OK).entity(new ResponseQuery("insertion ok")).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/find")
    public Response getIndex(@FormParam("query") String query) {
        System.out.println("find :'"+query+"'");
        String result = parser.parse(query);

        return Response.status(Response.Status.OK).entity(new ResponseQuery(result)).build();
    }


    @GET
    @Path("/exception")
    public Response exception() {
        throw new RuntimeException("oups...");
    }

    /********************************************************		helpers		*/

    public void insertion_test(int beginning, int ending) {
        CSVHelper.determineColumnsAndTypes();
        String file = "test.csv";
        CSVWriter writer = new CSVWriter(file);
        writer.writeCSVFile(beginning, ending-1);
        CSVReader reader = new CSVReader(file);
        main.app.core.entity.Index index = new main.app.core.entity.Index(file, reader.readForIndexing());
        parser = new Parser(index);
    }

    public class ResponseQuery {
        String response;
        ResponseQuery(String response) {
            this.response = response;
        }
    }
    public class ResponseIndex {
        Lines response;
        ResponseIndex(Lines response) {
            this.response = response;
        }
    }
}
