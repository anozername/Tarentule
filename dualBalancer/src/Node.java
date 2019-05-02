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

    public long work(){
        long work = 0;
        for(int i = 0; i<11000111; i++){
            work += (long)(Math.random() * ((100 - 1) + 1)) + 1;
        }

        return work;
    }

    private long divide() throws Exception {
        //Nodes to parallelize the reading of the file
        List<Node> list = new ArrayList<>();
        //On parcourt le contenu
        for (int j = 0; j < 10; j++) {
            //S'il s'agit d'un dossier, on crée une sous-tâche
            if (j % 2 == 0) {
                //Nous créons donc un nouvel objet Node
                //Qui se chargera de parcourir une partie du fichier
                Node scrappper = new Node(false);
                //Nous l'ajoutons à la liste des tâches en cours pour récupérer le résultat plus tard
                list.add(scrappper);
                //C'est cette instruction qui lance l'action en tâche de fond
                scrappper.fork();
            }
        }

        //Et, enfin, nous récupérons le résultat de toutes les tâches de fond
        for (Node f : list){
            result += f.join();
        }

        //Nous renvoyons le résultat final
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