package client;

import java.io.BufferedReader;


public class ClientDriver {
    private static Client client = new Client();
    public static void main(String[] args) {
        try {
            while (client.getClientSocket() != null) {

                // Read welcome messages from the server
                while (true) {
                    System.out.println(client.readMessage());
                    if (client.getLastMessage() == null) 
                        break;
                }

                BufferedReader reader = client.getClientConsole();
                // Read the user's choice and send it to the server
                if (reader.ready()) {
                    String choice = reader.readLine();
                    client.sendMessage(choice);
                }
                    // split choice into arguments if available
                //     String firstArg = choice.split(" ")[0];

                //     if (firstArg.equals("exit")) {
                //         break;
                //     }

                //     switch(firstArg){
                //         case("help"):
                //             System.out.println(reader.readLine());
                //             System.out.println(reader.readLine());
                //             System.out.println(reader.readLine());
                //             System.out.println(reader.readLine());
                //             System.out.println(reader.readLine());
                //             System.out.println(reader.readLine());
                //             System.out.println(reader.readLine());
                //             break;
                //         default:
                //             System.out.println(reader.readLine());
                //             break;
                //     }
                // }  
    
                

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
