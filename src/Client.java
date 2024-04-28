import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "localhost"; // Server IP address
    private static final int SERVER_PORT = 1300; // Server port number

    public static void main(String[] args) {
        try {
            // Connect to the server
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server.");

            // Set up input and output streams for communication with the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

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
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
