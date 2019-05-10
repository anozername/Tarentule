package main.app.engine;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import main.Main;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class LoadBalancer extends RecursiveTask<Long> {
    public LoadBalancer(){
        //System.out.println("Balancing...");
    }

    private int benchmarking(){
        return Runtime.getRuntime().availableProcessors();
    }

    public ForkJoinPool pool(){
        return new ForkJoinPool(benchmarking());
    }

    private long distribute(){
        long join = 0;
        List<Node> list = new ArrayList<>();
        //TODO adapt to the query parsed
        for(int i = 0; i<5; i++){
            if(i%2==0){
                Node node = new Node(true);
                list.add(node);
                node.fork();
            }
        }

        for(Node f : list) {
            join += f.join();
        }

        return join;
    }

    protected Long compute() {
        long compute = 0;
        List<Future<HttpResponse<JsonNode>>> futures = new ArrayList<>();

        for (String externalAddresses : Main.externalNodes){
            Future<HttpResponse<JsonNode>> future = Unirest.get("http://"+externalAddresses+"/test/engine").asJsonAsync();
            futures.add(future);
        }

        try {
            compute += this.distribute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Future<HttpResponse<JsonNode>> future : futures){
            try {
                HttpResponse<JsonNode> response = future.get();
                String result = response.getBody().getObject().toString();
                System.out.println(result);
                JSONObject jsonObj = new JSONObject(result);
                Long response_long = jsonObj.getLong("response");
                compute += response_long;
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return compute;
    }
}