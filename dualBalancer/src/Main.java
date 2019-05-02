public class Main {
    public static void main(String[] args) {
        //Création de notre tâche principale qui se charge de découper son travail en sous-tâches
        LoadBalancer loadBalancer = new LoadBalancer();

        Long start = System.currentTimeMillis();
        long result = loadBalancer.pool().invoke(loadBalancer); //compute()
        Long end = System.currentTimeMillis();

        System.out.println("result : " + result + " .");
        System.out.println("time : " + (end - start)+ " ms");
    }
}