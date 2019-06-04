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
    private Parser parser;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Lines string(@QueryParam("query") String query) throws Exception{
        LoadBalancer loadBalancer = new LoadBalancer();
        System.out.println("query :'"+query+"'");
        return loadBalancer.distribute(query);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/json")
    public Lines json(@QueryParam("query") String query) throws Exception{
        LoadBalancer loadBalancer = new LoadBalancer();
        System.out.println("query :'"+query+"'");
        Lines result = loadBalancer.distribute(query);
        return result;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/insert")
    public String insert() {
        //insertion_test();
        return "insert pas ok";
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/find")
    public Response getIndex(@FormParam("beginning") int beginning, @FormParam("query") String query, @FormParam("ending") int ending) {
        insertion_test(beginning, ending);
        String result = parser.parse(query);
        //return result;
        //return lines.toString();
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
        writer.writeCSVFile(beginning, ending);
        CSVReader reader = new CSVReader(file);
        main.app.core.entity.Index index = new main.app.core.entity.Index(file, reader.readForIndexing());
        parser = new Parser(index);
    }

    public static Parser insertion_static(int beginning, int ending) {
        CSVHelper.determineColumnsAndTypes();
        String file = "test.csv";
        CSVWriter writer = new CSVWriter(file);
        writer.writeCSVFile(beginning, ending);
        CSVReader reader = new CSVReader(file);
        main.app.core.entity.Index index = new main.app.core.entity.Index(file, reader.readForIndexing());
        return new Parser(index);
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
