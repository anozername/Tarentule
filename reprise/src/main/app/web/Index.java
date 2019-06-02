package main.app.web;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
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
        System.out.println("query :'"+query+"'");
        String result = loadBalancer.distribute(query);

        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/json")
    public Response json(@QueryParam("query") String query) throws Exception{
        LoadBalancer loadBalancer = new LoadBalancer();
        System.out.println("query :'"+query+"'");
        String result = loadBalancer.distribute(query);

        return Response.status(Response.Status.OK).entity(result).build();
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
    public String getIndex(@FormParam("beginning") int beginning, @FormParam("query") String query, @FormParam("ending") int ending) {
        System.out.println(beginning+" "+ending);
        insertion_test(beginning, ending);
        String result = parser.parse(query);
        JSONObject lines = new JSONObject();
        int nb = 0;
        for (String line:result.split("\n")) {
            lines.put("nb "+nb, line);
            nb++;
        }
        //return result;
        return lines.toString();
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
        writer.writeCSVFile(beginning, ending-1); //TODO rm "-1'
        CSVReader reader = new CSVReader(file);
        main.app.core.entity.Index index = new main.app.core.entity.Index(file, reader.readForIndexing());
        parser = new Parser(index);
    }

    public class ResponseQuery {
        public JSONObject lines = new JSONObject();

        ResponseQuery(String response) {
            int nb = 0;
            for (String line:response.split("\n")) {
                this.lines.put("nb "+nb, line);
                nb++;
            }
        }
    }
}
