package client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientDriver {
    private static Client client = new Client();
    public static void main(String[] args) {
        try {
            while (client.getClientSocket() != null) {
                
                // Read welcome message from the server
                String welcomeMessage = client.readMessage();
                System.out.println(welcomeMessage);
                String helpMessage = client.readMessage();
                System.out.println(helpMessage);

                 // Read the user's choice and send it to the server
                String choice = client.readConsole();
                client.sendMessage(choice);

                // split choice into arguments if available
                String firstArg = choice.split(" ")[0];

                if (firstArg.equals("exit")) {
                    break;
                }

                switch(firstArg){
                    case("help"):
                        System.out.println(client.readMessage());
                        System.out.println(client.readMessage());
                        System.out.println(client.readMessage());
                        System.out.println(client.readMessage());
                        System.out.println(client.readMessage());
                        System.out.println(client.readMessage());
                        System.out.println(client.readMessage());
                        break;
                    default:
                        System.out.println(client.readMessage());
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
        }
    }
}
