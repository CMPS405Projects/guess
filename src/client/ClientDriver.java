package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class ClientDriver {
    private static Client client = new Client();

    public static void main(String[] args) {
        try {
            BufferedReader reader = client.getReader();
            PrintWriter writer = client.getWriter();
            BufferedReader console = client.getClientConsole();
            
            // Display the welcome message from the server
            System.out.println(reader.readLine());
            // Display the help message from the server
            System.out.println(reader.readLine());

            while (true) {
                // Read the user's choice and send it to the server
                String choice = console.readLine();
                writer.println(choice);
                // split choice into arguments if available
                String firstArg = choice.split(" ")[0];

                if (firstArg.equals("exit")) {
                    break;
                }

                switch(firstArg){
                    case("help"):
                        System.out.println(reader.readLine());
                        System.out.println(reader.readLine());
                        System.out.println(reader.readLine());
                        System.out.println(reader.readLine());
                        System.out.println(reader.readLine());
                        System.out.println(reader.readLine());
                        System.out.println(reader.readLine());
                        break;
                    default:
                        System.out.println(reader.readLine());
                        break;
                }
            
            }

            // Handle further communication with the server based on the game's logic
            // For example, sending requests to join a game, etc.

        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
            client.exit(0);
            System.out.println("Client shutting down...");
        }
    }
}
