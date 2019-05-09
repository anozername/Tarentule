package main.app.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Node extends RecursiveTask<Long> {
    private long result = 0;
    private boolean balance;

    Node(boolean balanced){
        balance = balanced;
        //System.out.println(balance+" noding..."); // bad pun, very bad pun
    }

    private long work(){
        long work = 0;
        for(int i = 0; i<111000; i++){
            work += (long)(Math.random() * ((100 - 1) + 1)) + 1;
        }

        return work;
    }

    private long divide() {
        List<Node> list = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            if (j % 2 == 0) {
                Node scrapper = new Node(false);
                list.add(scrapper);
                scrapper.fork();
            }
        }

        for (Node f : list){
            result += f.join();
        }

        return result;
    }

    protected Long compute() {
        long compute = 0;
        try {
            if ( balance){
                compute = this.divide();
            }
            else {
                compute += work();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compute;
    }
}