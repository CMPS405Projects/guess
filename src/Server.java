import java.io.*;
import java.net.*;
import java.util.*;
import utils.*;

public class Server {
    private static final int PORT = 1300;
    private static List<Player> players = new ArrayList<>();
    private static Map<String, Integer> games = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for Clients...");

            while (true) {
                // Show when a client is connected to the server
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Show when a client is disconnected from the server
                if (clientSocket.isClosed()) {
                    System.out.println("Client disconnected: " + clientSocket);
                }


                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                // Send welcome message to the client
                writer.println("Welcome to the Guess 2/3 Game server!");
                writer.println("Enter `help` for a list of commands.");

                while (true) {

                    String choice = reader.readLine();

                    if (choice.equals("exit")) {
                        writer.println("Goodbye!");
                        break;
                    }
                    // Send login options to the client
                    writer.println("1. Login");
                    writer.println("2. Register");
                    writer.println("3. Exit");
                }



            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
