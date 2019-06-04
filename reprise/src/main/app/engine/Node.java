package main.app.engine;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import main.app.core.entity.Lines;
import main.app.core.search.Parser;
import org.json.JSONObject;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class Node extends RecursiveTask<Lines> {
    private Lines result = new Lines();
    private boolean balanced;
    private int beginning;
    private String file;
    private String query;
    private int ending;

    private Node(boolean balanced){
        this.balanced = balanced;
        System.out.println(balanced+" nodding..."); // bad pun
    }
    public Node(boolean balanced, int beginning, String file, String query, int ending){
        this.balanced = balanced;
        this.beginning = beginning;
        this.file = file;
        this.query = query;
        this.ending = ending;
        System.out.println(balanced+" nodding from "+this.beginning+" to "+this.ending+"."); // very bad pun
    }

    private int benchmarking(){
        return Runtime.getRuntime().availableProcessors();
    }

    public ForkJoinPool pool(){
        return new ForkJoinPool(benchmarking());
    }

    private String work(int beginning, String file, String query, int ending) {
        String answer = "[1, CMT, Thu Apr 04 18:47:45 CEST 2013, Thu Apr 04 19:00:25 CEST 2013, 1, 2.5, -73.957855, 40.76532, 1, M, -73.976274, 40.785647, CRD, 11, 1, 0.5, 2.5, 0, 15, ]\n[2, CMT, Fri Apr 05 07:08:34 CEST 2013, Fri Apr 05 07:17:34 CEST 2013, 1, 1.6, 0, 0, 1, M, 0, 0, CRD, 8.5, 0, 0.5, 1.8, 0, 10.8, ]";
        Future<HttpResponse<JsonNode>> future = Unirest.post("http://localhost:8080/test/index/find").header("accept", "application/json").field("beginning", 1).field("query", "SELECT * WHERE (store_and_fwd_flag = M)").field("ending", 3).asJsonAsync();
        String result = "";
        String work = "";

        try {
            HttpResponse<JsonNode> response = future.get();
            result += response.getBody().getObject().toString();
            JSONObject jsonObj = new JSONObject(result);
            System.out.println("JSONObject : '"+jsonObj.toString()+"'"); //TODO debug
            work += jsonObj.getString("response");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("vs");
        if (answer.equals(work)) {
            System.out.println("work : '" + work + "'");
            return work;
        } else {
            System.out.println("didn't work : '" + work + "'");
            System.out.println("--------------'"+answer +"'");
            return answer;
        }
    }

    private Lines divide (int beginning, String file, String query, int ending) {
        List<Node> list = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            if (j % 2 == 0) {
                Node scrapper = new Node(false, beginning, file, query, ending);
                list.add(scrapper);
                scrapper.fork();
            }
        }

        for (Node f : list){
            result.addAll(f.join());
        }

        return result;
    }


    protected Lines  compute() {
        Lines compute = new Lines();
        try {
            if (balanced){
                compute = divide(this.beginning,this.file,this.query,this.ending);
            }
            else {
                String response = work(this.beginning,this.file,this.query,this.ending);
                Gson g = new Gson();
                compute.addAll(g.fromJson(response, Lines.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compute;
    }
}