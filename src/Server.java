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
                writer.println("Welcome to the server!");

                // Send the login options
                writer.println("1. Login Using Ticket ");
                writer.println("2. Register New Player");

                // Read the user's choice and handle it
                String choice = "";
                choice = reader.readLine();

                System.out.println("Choice: " + choice);


                while (choice.compareTo("1") != 0 && choice.compareTo("2") != 0){
                    writer.println("Invalid choice. Please try again.");
                    choice = reader.readLine();
                }

                if (choice.equals("1")){
                    writer.println("Enter your ticket: ");
                    String ticket = reader.readLine();
                    boolean found = false;
                    for (Player player : players){
                        if (player.ticket.equals(ticket)){
                            writer.println("Welcome back, " + player.name);
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        writer.println("Invalid ticket. Please try again.");
                    }
                } else if (choice.equals("2")){
                    writer.println("Enter your name: ");
                    String name = reader.readLine();
                    Player player = new Player(name);
                    player.generateTicket();
                    players.add(player);
                    writer.println("Your ticket is: " + player.ticket + ". Please keep it safe.");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
