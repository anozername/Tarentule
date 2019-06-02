package main.app.engine;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import main.Main;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LoadBalancer {
    private int fake_nb_lines = 0;

    private Map<String, JSONObject> neighborhood = new HashMap<>();

    public LoadBalancer(){
        System.out.println("Balancing...");
        try {
            fake_nb_lines = countLines(Main.file_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        int ending = beginning + processors*(fake_nb_lines/max);
        //TODO check first & last lines

        if (ending + fake_nb_lines/max > fake_nb_lines)
                ending = fake_nb_lines;

        return new int[]{beginning, ending};
    }

    public String distribute(String query){
        //TODO parallelize ?
        String compute = "";
        JSONObject json = new JSONObject();

        //TODO adapt to the query parsed
        //     String result = new JSONObject(Unirest.get("http://localhost:8080/test/index/find?query="+query+"&beginning="+beginning+"&ending="+ending).asJson().getBody().getObject().toString()).getString("response");

        int max = 0;
        for (Map.Entry<String, JSONObject> entry : neighborhood.entrySet()) { //TODO opti
            max += entry.getValue().getLong("processor");
        }
        //System.out.println("total proco : "+max);
        int[] scope = new int[]{0, 0};
        for (Map.Entry<String, JSONObject> entry : neighborhood.entrySet()) { // http://localhost:8080/test/index/?query=SELECT AVG(passenger_count) WHERE (store_and_fwd_flag = M)
            scope = balance(scope[1], max, entry.getValue().getInt("processor"));
            Future<HttpResponse<JsonNode>> future = Unirest.post("http://"+entry.getKey()+"/test/index/find").header("accept", "application/json").field("beginning", scope[0]).field("query", query).field("ending", scope[1]).asJsonAsync();
            entry.getValue().put("address", entry.getKey());
            entry.getValue().put("future", future);
        }

        //for(Future<HttpResponse<JsonNode>> future : futures){
        for (Map.Entry<String, JSONObject> entry : neighborhood.entrySet()) {
            try {
                Future<HttpResponse<JsonNode>> future = (Future<HttpResponse<JsonNode>>) entry.getValue().get("future");
                HttpResponse<JsonNode> response = future.get();
                String result = response.getBody().getObject().toString();
                JSONObject jsonObj = new JSONObject(result);
                //String  response_string = jsonObj.getString(key);
                entry.getValue().put("response", jsonObj);
                entry.getValue().remove("heap");
                entry.getValue().remove("future");
                json.put(entry.getKey(),entry.getValue());
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return json.toString();
    }

    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i=0; i<1024;) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                for (int i=0; i<readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        } finally {
            is.close();
        }
    }
}