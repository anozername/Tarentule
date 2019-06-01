import com.mashape.unirest.http.Unirest;
import main.Main;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

@Consumes(MediaType.APPLICATION_JSON)
public class TestClass {
    @Before
    public void before() throws Exception {
        String[] args = {"-F", "../tiny.csv"};

        Main.jetty(Main.option(args));
    }

    @After
    public void after() throws Exception {
        Main.externalNodes = new ArrayList<>(); // reset
    }

    @org.junit.Test
    public void testEndPoint() throws Exception {
        for (String externalAddresses : Main.externalNodes) {
            Assert.assertEquals("ok",
                    "{\"app\":\"RESTFUL API - java index\",\"response\":true,\"id\":\"intellij\"}",
                    Unirest.get("http://" + externalAddresses + "/test/api/json").asJson().getBody().getObject().toString()+""
            );
        }
    }

    @org.junit.Test
    public void testMultipleBenchmark() throws Exception {
        String[] args = {"-F", "../tiny.csv", "-P","8081"};
        Main.jetty(Main.option(args));

        // Main.addExternalNode("addExternalNode localhost:8081"); //same Main.externalNodes so useless
        for (String externalAddresses : Main.externalNodes) {
            System.out.println(externalAddresses);
            Assert.assertEquals(
                    "{\"app\":\"RESTFUL API - java index\",\"response\":true,\"id\":\"intellij\"}",
                    Unirest.get("http://" + externalAddresses + "/test/api/json").asJson().getBody().getObject().toString()+""
            );
        }

        Assert.assertEquals(2,Main.externalNodes.size());
    }

    @org.junit.Test
    public void testInsertion() {
        try {
            URL url = new URL("http://localhost:8080/test/index/insert");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                Assert.assertEquals("insert pas ok", strTemp+"");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @org.junit.Test
    public void testQuery() {
        try {
            URL url = new URL("http://localhost:8080/test/index/insert");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                Assert.assertEquals("insert pas ok", strTemp+"");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            URL url = new URL("http://localhost:8080/test/index/find?query=SELECT%20AVG(total_amount)%20WHERE%20(passenger_count%20=%201%20AND%20VendorID%20=%201)");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                Assert.assertEquals("{\"response\":\"ok\"}", strTemp+"");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
