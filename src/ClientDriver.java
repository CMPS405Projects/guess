import java.io.*;
import java.net.*;

public class ClientDriver {
    private static Client client;
    public static void main(String[] args) {
        try {
            client = new Client();
            System.out.println("Clinet connected to server.");
            // Set up input and output streams for communication with the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
            PrintWriter writer = new PrintWriter(client.getClientSocket().getOutputStream(), true);

            // Read welcome message from the server
            String welcomeMessage = reader.readLine();
            System.out.println(welcomeMessage);
            String helpMessage = reader.readLine();
            System.out.println(helpMessage);

            // Read the user's choice and send it to the server
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(System.in));


            while (true) {
                String choice = clientReader.readLine();
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

            reader.close();
            writer.close();
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
