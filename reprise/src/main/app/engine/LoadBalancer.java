package app.engine;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class LoadBalancer extends RecursiveTask<Long> {
    public LoadBalancer(){
        System.out.println("Balancing...");
    }

    private int benchmarking(){
        return Runtime.getRuntime().availableProcessors();
    }

    public ForkJoinPool pool(){
        return new ForkJoinPool(benchmarking());
    }

    private long distribute() throws Exception{
        long join = 0;
        //Nodes for a specific type of query
        List<Node> list = new ArrayList<>();
        //On parcourt la query
        //TODO adapt to the query parsed
        for(int i = 0; i<5; i++){
            //S'il s'agit d'un dossier, on crée une sous-tâche
            if(i%2==0){
                //Nous créons donc un nouvel objet Node
                //Qui se chargera de parcourir le fichier
                Node node = new Node(true);
                //Nous l'ajoutons à la liste des tâches en cours pour récupérer le résultat plus tard
                list.add(node);
                //C'est cette instruction qui lance l'action en tâche de fond
                node.fork();
            }
        }

        //Et, enfin, nous récupérons le résultat de toutes les tâches de fond
        for(Node f : list)
            join += f.join();

        //Nous renvoyons le résultat final
        return join;
    }

    protected Long compute() {
        long compute = 0;
        try {
            compute = this.distribute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compute;
    }
}