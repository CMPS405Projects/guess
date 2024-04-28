import java.io.*;
import java.net.*;
import java.util.*;
import utils.*;

public class Server {
    private static final int PORT = 1300;
    private static List<Player> players = new ArrayList<>();
    private static List<Game> games = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for Clients...");

            while (true) {
                // Show when a client is connected to the server
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);


                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void addPlayer(Player player) {
        players.add(player);
    }

    public static synchronized List<Player> getPlayers() {
        return players;
    }

    public static synchronized void addGame(Game game) {
        games.add(game);
    }


}
