import java.io.*;
import java.net.*;
import java.util.*;
import utils.*;

public class Server {

    private final int PORT = 13337;

    private List<Socket> connectedClients = new ArrayList<>();
    private List<Player> connectedPlayers = new ArrayList<>();
    private List<Player> allPlayers = new ArrayList<>();

    private List<Game> liveGames = new ArrayList<>();
    private List<Game> allGames = new ArrayList<>();

    private ServerSocket serverSocket;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(this.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptClients() throws IOException {
        while (true){
            Socket clientSocket = this.serverSocket.accept();
            connectedClients.add(clientSocket);

            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
            
            System.out.println("Client connected.");
        }
    }

    public void endClient() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public synchronized void addPlayer(Player player) {
        allPlayers.add(player);
        connectedPlayers.add(player);
    }

    public synchronized List<Player> getConnectedPlayers() {
        return connectedPlayers;
    }

    public synchronized List<Game> getLiveGames() {
        return liveGames;
    }

    public synchronized List<Player> getAllPlayers() {
        return allPlayers;
    }

    public synchronized List<Game> getAllGames() {
        return allGames;
    }

    public synchronized void createGame() {
        Game game = new Game();
        this.addGame(game);
    }

    private synchronized void addGame(Game game) {
        allGames.add(game);
        liveGames.add(game);
    }

    public int getPort() {
        return this.PORT;
    }

}
