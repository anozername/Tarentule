package main;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import main.app.core.entity.Lines;
import main.app.engine.LoadBalancer;
import main.app.web.Index;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.cli.*;
import org.json.JSONObject;

public class Main {
    public static Map<String, JSONObject> neighborhood = new HashMap<>();
    public static String file_path;
    static int fake_nb_lines = 0;

    public static void jetty(int port) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(port);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "main.app");

        try {
            jettyServer.start();
            System.out.println("jetty started");
            //jettyServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int option(String[] args){
        String port_string = "8080";
        int port = 8080;

        Options options = new Options();

        Option input_option = new Option("F", "file", true, "input file path");
        input_option.setRequired(true);
        options.addOption(input_option);
        Option port_option = new Option("P", "port", true, "port number");
        port_option.setRequired(false);
        options.addOption(port_option);
        Option debug_option = new Option("D", "debug", false, "debug dialog");
        debug_option.setRequired(false);
        options.addOption(debug_option);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            file_path = cmd.getOptionValue("file");
            if(cmd.hasOption("port")) {
                System.out.println(port_string);
                port_string = cmd.getOptionValue("port");
                System.out.println(port_string);
            }
            if(cmd.hasOption("debug")) {
                System.out.println("debug mode : DIY");
            }
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        try {
            port = Integer.parseInt(port_string);
            // TODO check port available
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        try {
            fake_nb_lines = countLines(file_path);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        JSONObject jsonObject = new JSONObject("{\"processor\":"+Runtime.getRuntime().availableProcessors()+",\"heap\":"+Runtime.getRuntime().freeMemory()+"}");
        neighborhood.put("localhost:"+port , jsonObject);
        // System.out.println(Unirest.get("http://localhost:"+port+"/test/index/insert/beginning="+1+"&ending="+fake_nb_lines).asJson());
        //System.out.println(Index.insert(1,fake_nb_lines));

        return port;
    }

    private static int[] balance(int last, int max, int processors){
        int beginning = last+1; //skip line 0 (header) / 1 (usually blank)

        int ending = beginning + processors*(fake_nb_lines/max);
        //TODO check first & last lines

        if (ending + fake_nb_lines/max > fake_nb_lines)
            ending = fake_nb_lines;

        return new int[]{beginning, ending};
    }

    private static void addExternalNode(String command){
        String[] command_splitted = command.split(" ");
        if (command_splitted.length == 1){
            System.out.println("Missing argument : 'addExternalNode <address>'");
        }
        for (int i = 1; i < command_splitted.length;i++) {
            String address_string = command_splitted[i];
            try {
                neighborhood.put(address_string , new JSONObject(Unirest.get(address_string+"/test/network").asJson().getBody().getObject().toString()));
                Unirest.post("http://"+address_string+"/test/index/insert").header("accept", "application/json").field("beginning", 1).field("ending", countLines(file_path)).asJson();
            }
            catch (IOException | UnirestException e){
                //e.printStackTrace();
                System.out.println("http://"+address_string + " is not a valid node address.");
            }
        }
        int[] scope = new int[]{0, 0};
        for (Map.Entry<String, JSONObject> entry : neighborhood.entrySet()) {
            try {
                scope = balance(scope[1], entry.getValue().getInt("processor"), entry.getValue().getInt("processor"));
                Unirest.post(entry.getKey()+"/test/index/insert").header("accept", "application/json").field("beginning", scope[0]).field("ending", scope[1]).asJson();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
    }

    private static int countLines(String filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];
            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }
            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i = 0; i < 1024; ) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }
            // count remaining characters
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }
            return count == 0 ? 1 : count;
        }
    }

    public static void main(String[] args) {
        // <-- Jetty embedded --> // <-- Option setup -->
        jetty(option(args));

        for (Map.Entry<String, JSONObject> entry : neighborhood.entrySet()) {
            try {
                System.out.println(Unirest.get("http://"+entry.getKey()+"/test/index/insert/?beginning="+1+"&ending="+fake_nb_lines).asJson().getBody().getObject().toString());
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

        // User input
        while (true) {
            Scanner myObj = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Enter command :");

            String command = myObj.nextLine();  // Read user input
            System.out.println("The command is: " + command);  // Output user input
            switch (command.split(" ")[0]) {
                case "stop":
                    System.out.println("Bye.");
                    System.exit(0);
                    break;
                case "addExternalNode":
                    addExternalNode(command);
                    break;
                default:
                    System.out.println("Invalid command");
                    break;
            }
        }
    }
}