package main;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.cli.*;
import org.json.JSONObject;

public class Main {
    public static List<String> externalNodes = new ArrayList<>();
    public static String file_path;

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
            e.printStackTrace();/*
        }
        finally {
            jettyServer.destroy();
        */
        }
    }

    public static int option(String[] args){
        // TODO File control
        // TODO make args[0] global

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
                //TODO
                System.out.println("TODO debug mode");
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
        externalNodes.add("localhost:"+port);

        return port;
    }

    public static void addExternalNode(String command){
        String[] command_splitted = command.split(" ");
        if (command_splitted.length == 1){
            System.out.println("Missing argument : 'addExternalNode <address>'");
        }
        for (int i = 1; i < command_splitted.length;i++) {
            String address_string = command_splitted[i];
            try {
                HttpResponse<JsonNode> jsonResponse = Unirest.get("http://" + address_string + "/test/api/json").asJson();
                String result = jsonResponse.getBody().getObject().toString();
                JSONObject jsonObj = new JSONObject(result);
                System.out.println(jsonObj);
                externalNodes.add(address_string);
            } catch (UnirestException e) {
                //e.printStackTrace();
                System.out.println(address_string + " is not a valid node address.");
            }
        }
    }

    private static void stop(){
        System.out.println("Bye.");
        System.exit(0);
    }

    public static void main(String[] args) {
        // <-- Jetty embedded --> // <-- Option setup -->
        jetty(option(args));

        // User input
        while (true) {
            Scanner myObj = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Enter command :");

            String command = myObj.nextLine();  // Read user input
            System.out.println("The command is: " + command);  // Output user input
            switch (command.split(" ")[0]) {
                case "stop":
                    stop();
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