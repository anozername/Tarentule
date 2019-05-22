package main.app.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Node extends RecursiveTask<Long> {
    private long result = 0;
    private boolean balanced;
    private int beginning;
    private int ending;

    private Node(boolean balanced){
        this.balanced = balanced;
        System.out.println(balanced+" nodding..."); // bad pun
    }
    public Node(boolean balanced, int beginning, int ending){
        this.balanced = balanced;
        this.beginning = beginning;
        this.ending = ending;
        System.out.println(balanced+" nodding from "+this.beginning+" to "+this.ending+"."); // very bad pun
    }

    private int benchmarking(){
        return Runtime.getRuntime().availableProcessors();
    }

    public ForkJoinPool pool(){
        return new ForkJoinPool(benchmarking());
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
            if (balanced){
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