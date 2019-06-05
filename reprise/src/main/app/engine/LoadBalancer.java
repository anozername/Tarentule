package main.app.engine;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import main.Main;
import main.app.core.entity.Lines;
import main.app.core.search.Parser;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LoadBalancer {
    public LoadBalancer(){
        System.out.println("Balancing...");
    }

    public String distribute(String query){
        //TODO parallelize ?
        Gson g = new Gson();
        String compute = "";
        JSONObject json = new JSONObject();

        //TODO adapt to the query parsed
        //     String result = new JSONObject(Unirest.get("http://localhost:8080/test/index/find?query="+query+"&beginning="+beginning+"&ending="+ending).asJson().getBody().getObject().toString()).getString("response");

        int max = 0;
        for (Map.Entry<String, JSONObject> entry : Main.neighborhood.entrySet()) { //TODO opti
            max += entry.getValue().getLong("processor");
        }
        //System.out.println("total proco : "+max);
        for (Map.Entry<String, JSONObject> entry : Main.neighborhood.entrySet()) { // http://localhost:8080/test/index/?query=SELECT AVG(passenger_count) WHERE (store_and_fwd_flag = M)
            Future<HttpResponse<JsonNode>> future = Unirest.post("http://"+entry.getKey()+"/test/index/find").header("accept", "application/json").field("query", query).asJsonAsync();
            entry.getValue().put("address", entry.getKey());
            entry.getValue().put("future", future);
        }

        ArrayList<String> lines = new ArrayList<String>();
        //for(Future<HttpResponse<JsonNode>> future : futures){
        Lines li = new Lines();

        if (Parser.getGroupBy().isEmpty()) {
            for (Map.Entry<String, JSONObject> entry : Main.neighborhood.entrySet()) {
                try {
                    Future<HttpResponse<JsonNode>> future = (Future<HttpResponse<JsonNode>>) entry.getValue().get("future");
                    HttpResponse<JsonNode> response = future.get();
                    String result = response.getBody().getObject().toString();
                    JSONObject jsonObj = new JSONObject(result);
                    //System.out.println("JSONObject : '"+jsonObj.toString()+"'");
                    String response_string = jsonObj.getString("response");
                   // System.out.println("String2Lines(response_string)"+String2Lines(response_string)+"'");
                    //li.addAll(g.fromJson(result, Lines.class));
                    System.out.println("li"+li+"'");
                   // li.addAll(String2Lines(response_string));
                    System.out.println("li"+li+"'");
                    li.addAll(g.fromJson(response_string, Lines.class));
                    //lines.addAll(Arrays.asList(response_string.split("\n")));
                    /*
                    String result = response.getBody().getObject().toString();
                    JSONObject jsonObj = new JSONObject(result);
                    //String  response_string = jsonObj.getString(key);
                    entry.getValue().put("response", jsonObj);
                    entry.getValue().remove("heap");
                    entry.getValue().remove("future");
                    json.put(entry.getKey(),entry.getValue());
                    */
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("merde");
            for (Map.Entry<String, JSONObject> entry : Main.neighborhood.entrySet()) {
                try {
                    Future<HttpResponse<JsonNode>> future = (Future<HttpResponse<JsonNode>>) entry.getValue().get("future");
                    HttpResponse<JsonNode> response = future.get();
                    String result = response.getBody().getObject().toString();
                    JSONObject jsonObj = new JSONObject(result);
                    String response_string = jsonObj.getString("response");
                    li = li.compute(g.fromJson(response_string, Lines.class), Parser.getGroupBy());

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return Parser.getLinesSelect(li);
    }

    private Lines String2Lines (String raw){
        Lines lines = new Lines();
        String[] array = raw.split("\n");
        List<Object[]> arrayList = new ArrayList();
        arrayList.add(array);
        lines.setLines(arrayList);
        return lines;
    }
}