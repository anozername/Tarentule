import com.mashape.unirest.http.Unirest;
import main.Main;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

@Consumes(MediaType.APPLICATION_JSON)
public class TestClass {
    @Before
    public void before() throws Exception {
        String[] args = {"-F", "../tinyload.csv"};
        Main.jetty(Main.option(args));

        //Thread main = new Thread(new Main());
    }

    @After
    public void after() throws Exception {
        Main.externalNodes = new ArrayList<>(); // reset
    }

    @org.junit.Test
    public void testEndPoint() throws Exception {
        for (String externalAddresses : Main.externalNodes) {
            Assert.assertEquals(  "{\"app\":\"RESTFUL API - java index\",\"response\":true,\"id\":\"intellij\"}",
                                    Unirest.get("http://" + externalAddresses + "/test/api/json").asJson().getBody().getObject().toString()+""
            );
        }
    }

    /*
    @org.junit.Test
    public void testExternalNode() throws Exception {
        String[] args = {"-F", "../tinyload.csv", "-P","8081"};
        //TODO Main.jetty(Main.option(args)); //IMPOSSIBRU

        // Main.addExternalNode("addExternalNode localhost:8081"); //same Main.externalNodes therefor useless
        URL url = new URL("http://localhost:8080/test/network/list");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String strTemp = "";
        while (null != (strTemp = br.readLine())) {
            Assert.assertEquals("[localhost:8080, localhost:8081]", strTemp+"");
        }
    }
    */

    @org.junit.Test
    public void testQuery() throws Exception{
        JSONObject jsonObject = (JSONObject) new JSONObject(Unirest.get("http://localhost:8080/test/index/json/?query=SELECT%20AVG(passenger_count)%20WHERE%20(store_and_fwd_flag%20=%20M)").asJson().getBody().getObject().toString()).get("localhost:8080");
        Assert.assertEquals(  "{\"nb 0\":\"avg :1.0\"}", jsonObject.get("response").toString());
    }

    /*
    @org.junit.Test
    public void testMultipleQuery() throws Exception{
        String[] args = {"-F", "../tinyload.csv", "-P","8081"};
        //TODO Main.jetty(Main.option(args)); //IMPOSSIBRU
        new Thread(new Runnable() {
            public void run() {
                try {
                    //start a new jvm with 256m of memory with the MyClass passing 2 parameters
                    String cmd = "java Main.java -F ../tinyload.csv -P 8081";
                    Process p = Runtime.getRuntime().exec(cmd);
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = br.readLine();
                    while (line != null) {
                        line = br.readLine();
                    }
                    br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    line = br.readLine();
                    while (line != null) {
                        line = br.readLine();
                    }
                    System.out.println("ok");
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }).start();

        Main.addExternalNode("addExternalNode localhost:8081");
        JSONObject jsonObject = (JSONObject) new JSONObject(Unirest.get("http://localhost:8080/test/index/json/?query=SELECT%20AVG(passenger_count)%20WHERE%20(store_and_fwd_flag%20=%20M)").asJson().getBody().getObject().toString()).get("localhost:8080");

        Assert.assertEquals(  "{\"nb 0\":\"avg :3.0\"}", jsonObject.get("response").toString());
    }
    */
}
