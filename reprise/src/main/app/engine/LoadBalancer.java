package main.app.engine;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import main.Main;
import main.app.core.entity.Lines;
import main.app.core.search.CSVHelper;
import main.app.core.search.CastHelper;
import main.app.core.search.Parser;
import org.json.JSONObject;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LoadBalancer {
    public LoadBalancer(){
        System.out.println("Balancing...");
    }

    public String distribute(String query){
        Gson g = new Gson();
        String compute = "";
        JSONObject json = new JSONObject();

        //TODO adapt to the query parsed

        int max = 0;
        for (Map.Entry<String, JSONObject> entry : Main.neighborhood.entrySet()) { //TODO opti
            max += entry.getValue().getLong("processor");
        }
        for (Map.Entry<String, JSONObject> entry : Main.neighborhood.entrySet()) {
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
                    String response_string = jsonObj.getString("response");
                    //System.out.println("qsdf"+response_string);
                    //System.out.println(g.fromJson(response_string, Lines.class).get(0)[0].getClass());
                    //System.out.println(g.fromJson(response_string, Lines.class).get(0)[0]);
                    li.addAll(String2Lines(response_string));
                    System.out.println(li.get(0)[0]);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            for (Map.Entry<String, JSONObject> entry : Main.neighborhood.entrySet()) {
                try {
                    Future<HttpResponse<JsonNode>> future = (Future<HttpResponse<JsonNode>>) entry.getValue().get("future");
                    HttpResponse<JsonNode> response = future.get();
                    String result = response.getBody().getObject().toString();
                    String response_string = new JSONObject(result).getString("response");
                    li = li.compute(String2Lines(response_string), Parser.getGroupBy());

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return Parser.getLinesSelect(li);
    }

    private Lines String2Lines (String raw){ //DO NOT SUPP, cute
        Lines lines = new Lines();
        String[] array = raw.split("\n");
        for (String s : array) lines.add(CSVHelper.read(s.split(",")).toArray());
        return lines;
    }
}