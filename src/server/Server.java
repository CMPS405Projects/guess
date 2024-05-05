package server;
import java.io.*;
import java.net.*;
import java.util.*;
import utils.*;
public class Server {

    private final int PORT = 13337;

    private List<Player> onlinePlayers = Collections.synchronizedList(new ArrayList<>());
    private List<Player> allPlayers = Collections.synchronizedList(new ArrayList<>());

    private List<GameHandler> liveGames = new ArrayList<>();
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

    public void ready(Player player, String gameName, ClientHandler clientHandler) {
        Game game;
        for (GameHandler gameHandler : liveGames) {
            game = gameHandler.getGame();
            if (game.getName().equals(gameName) && game.getPlayers().contains(player)){
                player.setStatus((game.getName()).toString() + " ready");
                clientHandler.getWriter().println("You are ready to start the game.");
                return;
            } else if (game.getName().equals(gameName) && ! game.getPlayers().contains(player)){
                clientHandler.getWriter().println("You are not in the game.");
                return;
            } else if (game.getName().equals(gameName) && game.getPlayers().size() >= 6){
                clientHandler.getWriter().println("Game is full.");
                return;
            } else if (game.getName().equals(gameName) && game.getStatus().equals(GameStatus.ONGOING)){
                clientHandler.getWriter().println("Game is ongoing.");
                return;
            } else if (game.getName().equals(gameName) && game.getStatus().equals(GameStatus.ENDED)){
                clientHandler.getWriter().println("Game has ended.");
                return;
            }
        }
        clientHandler.getWriter().println("Game does not exist.");
    }
    public GameHandler getGameHandler(String gameName) {
        for (GameHandler gameHandler : liveGames) {
            if (gameHandler.getGame().getName().equals(gameName)) {
                return gameHandler;
            }
        }
        return null;
    }

    public void joinGame(Player player, String gameName, ClientHandler clientHandler) {
        GameHandler gameHandler = getGameHandler(gameName);

        // If the game does not exist, create a new game
        if (gameHandler == null) {
            Game game = new Game(gameName);
            player.setStatus(PlayerStatus.JOINED);
            game.addPlayer(player);
            this.addGame(game);
            // Ask the player to ready
            clientHandler.getWriter().println("Please enter `ready [gameName]` to start the game.");
            this.createGame(game);
            return;
        }
        
        // if the game exists, add the player to the game
        Game game = gameHandler.getGame();
        if (game.getPlayers().contains(player)){
            clientHandler.getWriter().println("You are already in the game.");
            return;
        } else if (game.getPlayers().size() >= game.getMaxPlayers()){
            clientHandler.getWriter().println("Game is full.");
            return;
        } else if (game.getStatus().equals(GameStatus.ONGOING)){
            clientHandler.getWriter().println("Game is ongoing.");
            return;
        } else if (game.getStatus().equals(GameStatus.ENDED)){
            clientHandler.getWriter().println("Game has ended.");
            return;
        }
        // Add the player to the game
        player.setStatus(PlayerStatus.JOINED);
        game.addPlayer(player);
        this.addGame(game);

        // Ask the player to ready
        clientHandler.getWriter().println("Please enter `ready [gameName]` to start the game.");
    }

    private void createGame(Game game) {
        GameHandler gameHandler = new GameHandler(game);
        this.addGameHandler(gameHandler);
        Thread gameThread = new Thread(gameHandler);
        gameThread.start();
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

    public synchronized List<GameHandler> getLiveGames() {
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
        if (allGames.contains(game)) return ;
        allGames.add(game);
    }

    private synchronized void addGameHandler(GameHandler gameHandler) {
        liveGames.add(gameHandler);
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
