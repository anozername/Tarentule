package main.app.engine;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import main.Main;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LoadBalancer {
    private Map<String, JSONObject> neighborhood = new HashMap<>();

    public LoadBalancer(){
        System.out.println("Balancing...");
        for (String externalAddresses : Main.externalNodes){
            try {
                neighborhood.put(externalAddresses , new JSONObject(Unirest.get("http://"+externalAddresses+"/test/network").asJson().getBody().getObject().toString()));
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
    }

    private int[] balance(JSONObject externalNode){
        int beginning = 0;
        int ending = 100;


        return new int[]{beginning, ending};
    }

    public long distribute(){
        //TODO parallelize ?
        long compute = 0;
        List<Future<HttpResponse<JsonNode>>> futures = new ArrayList<>();

        //TODO adapt to the query parsed

        for (Map.Entry<String, JSONObject> entry : neighborhood.entrySet()) {
            int[] scope = balance(entry.getValue());
            Future<HttpResponse<JsonNode>> future = Unirest.post("http://"+entry.getKey()+"/test/engine/work").header("accept", "application/json").field("beginning", 0).field("ending", 100).asJsonAsync();
            futures.add(future);
        }

        for(Future<HttpResponse<JsonNode>> future : futures){
            try {
                HttpResponse<JsonNode> response = future.get();
                String result = response.getBody().getObject().toString();
                JSONObject jsonObj = new JSONObject(result);
                Long response_long = jsonObj.getLong("result");
                compute += response_long;
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return compute;
    }
}