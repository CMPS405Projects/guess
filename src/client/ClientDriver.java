package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class ClientDriver {
    private static Client client = new Client();

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
            PrintWriter writer = new PrintWriter(client.getClientSocket().getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (client.getClientSocket() != null) {

                // Read welcome messages from the server
                // String temp;
                // while ((temp = reader.readLine()) != null) {
                //     System.out.println(temp);
                // }

                System.out.println(reader.readLine());
                System.out.println(reader.readLine());

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
