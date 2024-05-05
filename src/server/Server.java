package server;
import java.io.*;
import java.net.*;
import java.util.*;
import utils.*;

public class Server {

    private final int PORT = 13337;

    private List<Player> onlinePlayers = Collections.synchronizedList(new ArrayList<>());
    private List<Player> allPlayers = Collections.synchronizedList(new ArrayList<>());

    private List<Game> liveGames = new ArrayList<>();
    private List<Game> allGames = new ArrayList<>();

    private List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());

    private List<String> tickets = Collections.synchronizedList(new ArrayList<>());

    private ServerSocket serverSocket;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(this.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinGame(Player player, String gameName) {
        for (Game game : liveGames) {
            if (game.getName().equals(gameName) && game.getPlayers().size() < 6){
                player.setStatus((game.getId()).toString());
                game.addPlayer(player);
                return;
            }
        }
        Game game = new Game(gameName);
        player.setStatus((game.getId()).toString());
        game.addPlayer(player);
        this.addGame(game);
    }

    public void acceptClients() throws IOException {
        while (true){
            Socket clientSocket = this.serverSocket.accept();

            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clientHandlers.add(clientHandler);
            
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
            
            System.out.println("Client connected.");
        }
    }

    public synchronized void endClient(Socket clientSocket) {
        try {
            clientSocket.close();
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.getClientSocket().equals(clientSocket)) {
                    clientHandlers.remove(clientHandler);
                    break;
                }
            }

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
        onlinePlayers.add(player);
        if (! allPlayers.contains(player)) allPlayers.add(player);
    }

    public synchronized List<Player> getOnlinePlayers() {
        return onlinePlayers;
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

    public synchronized void createGame(String gameName) {
        Game game = new Game(gameName);
        this.addGame(game);
    }

    private synchronized void addGame(Game game) {
        liveGames.add(game);
        if (allGames.contains(game)) return ;
        allGames.add(game);
    }

    public int getPort() {
        return this.PORT;
    }

    public synchronized void broadcast(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.getWriter().println(message);
        }
    }

}
