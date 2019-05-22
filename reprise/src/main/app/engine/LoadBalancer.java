package main.app.engine;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import main.Main;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LoadBalancer {
    private int fake_nb_lines = 16000;
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

    private int[] balance(int last, int max, int processors){
        int beginning = last+1; //skip line 0 (header) / 1 (usually blank)

        System.out.println(processors);
        int ending = beginning + processors*(fake_nb_lines/max);
        //TODO check first & last lines

        if (ending + fake_nb_lines/max > fake_nb_lines)
                ending = fake_nb_lines;

        return new int[]{beginning, ending};
    }

    public long distribute(){
        //TODO parallelize ?
        long compute = 0;
        List<Future<HttpResponse<JsonNode>>> futures = new ArrayList<>();

        //TODO adapt to the query parsed

        int max = 0;
        for (Map.Entry<String, JSONObject> entry : neighborhood.entrySet()) { //TODO opti
            max += entry.getValue().getLong("processor");
        }
        System.out.println("total proco : "+max);
        int[] scope = new int[]{0, 0};
        for (Map.Entry<String, JSONObject> entry : neighborhood.entrySet()) {
            scope = balance(scope[1], max, entry.getValue().getInt("processor"));
            Future<HttpResponse<JsonNode>> future = Unirest.post("http://"+entry.getKey()+"/test/engine/work").header("accept", "application/json").field("beginning", scope[0]).field("file", "file.csv").field("ending", scope[1]).asJsonAsync();
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