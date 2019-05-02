package app.engine;

public class Node extends Thread {
    public Node(String name){
        super(name);
    }
    public void run(){
        for(int i = 0; i < 10; i++)
            System.out.println(this.getName());
    }
}