import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.Scanner;
import org.apache.commons.cli.*;

public class Main {

    private static void jetty(int port) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(port);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "app");

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

    private static int option(String[] args){
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

            String inputFilePath = cmd.getOptionValue("input_option");
            if(cmd.hasOption("port")) {
                System.out.println(port_string);
                port_string = cmd.getOptionValue("port");
                System.out.println(port_string);
            }
            if(cmd.hasOption("debug")) {
                //TODO
                System.out.println("debug not implemented");
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

        return port;
    }

    public static void main(String[] args) {
        // <-- Jetty embedded --> // <-- Option setup -->
        jetty(option(args));

        // User input
        while (true) {
            Scanner myObj = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Enter command :");

            String command = myObj.nextLine();  // Read user input
            System.out.println("command is: " + command);  // Output user input
            if (command.equals("stop")){
                System.exit(0);
            }
        }

    }

}