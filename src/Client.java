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

            // Read login options from the server
            String loginOption1 = reader.readLine();
            String loginOption2 = reader.readLine();
            System.out.println(loginOption1);
            System.out.println(loginOption2);


            // Read the user's choice and send it to the server
            BufferedReader userChoice = new BufferedReader(new InputStreamReader(System.in));
            String choice = userChoice.readLine();
            writer.println(choice);

            String inputMessage = reader.readLine();
            System.out.println(inputMessage);


            // Read the user's ticket or name and send it to the server
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String input = userInput.readLine();
            writer.println(input);

            String status = reader.readLine();
            System.out.println(status);


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
