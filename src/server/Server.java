package server;

import java.io.*;
import java.net.*;
import java.util.*;

import utils.*;

public class Server {

    private final int PORT = 13337;

    private ServerSocket serverSocket;

    private List<Player> onlinePlayers = Collections.synchronizedList(new ArrayList<>());
    private List<Player> allPlayers = Collections.synchronizedList(new ArrayList<>());
    private List<GameHandler> liveGames = new ArrayList<>();
    private List<Game> allGames = new ArrayList<>();
    private List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    private List<String> tickets = Collections.synchronizedList(new ArrayList<>());
    private Map<Player, Integer> leaderboard = Collections.synchronizedMap(new HashMap<>());

    public Server() {
        try {
            this.serverSocket = new ServerSocket(this.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptClients() throws IOException {
        while (true) {
            Socket clientSocket = this.serverSocket.accept();

            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clientHandlers.add(clientHandler);

            Thread clientThread = new Thread(clientHandler);
            clientThread.start();

            System.out.println("Client connected.");
        }
    }

    public void ready(Player player, String gameName, ClientHandler clientHandler) {
        GameHandler gameHandler = getGameHandler(gameName);
        if (gameHandler == null) {
            clientHandler.getWriter().println("Game does not exist.");
            return;
        }
        Game game = gameHandler.getGame();
        synchronized (game) {
            if (player.getGame().getName().equals(gameName) && player.getStatus().equals(PlayerStatus.JOINED)) {
                player.setStatus(PlayerStatus.READY);
                clientHandler.getWriter().println("You are ready to start the game.\nWaiting for other players.");
                game.notify();
            } else if (!player.getGame().getName().equals(gameName)) {
                clientHandler.getWriter().println("You are not in this game.");
            }
        }
    }

    public void joinGame(Player player, String gameName, ClientHandler clientHandler) {
        GameHandler gameHandler = getGameHandler(gameName);

        // If the game does not exist, create a new game
        if (gameHandler == null) {
            Game game = new Game(gameName);
            player.setStatus(PlayerStatus.JOINED);
            player.setGame(game);
            game.addClientHandler(clientHandler);
            this.addGame(game);
//            clientHandler.getWriter().println("Please enter `ready [gameName]` to start the game.");
            this.createGame(game);
            return;
        }
        // if the game exists, add the player to the game
        Game game = gameHandler.getGame();
        synchronized (game) {
            if (game.playersCount() >= game.getMaxPlayers()) {
                clientHandler.getWriter().println("Game is full.");
                return;
            } else if (game.getStatus().equals(GameStatus.ONGOING)) {
                clientHandler.getWriter().println("Game is ongoing.");
                return;
            } else if (game.getStatus().equals(GameStatus.ENDED)) {
                clientHandler.getWriter().println("Game has ended.");
                return;
            }
            // Add the player to the game
            player.setStatus(PlayerStatus.JOINED);
            player.setGame(game);
            game.addClientHandler(clientHandler);
            this.addGame(game);
            game.notify();
        }
        // Ask the player to ready
        clientHandler.getWriter().println("Please enter `ready [gameName]` to start the game.");
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public List<ClientHandler> getClientHandlers() {
        return this.clientHandlers;
    }

    public synchronized List<Player> getAllPlayers() {
        return allPlayers;
    }

    public synchronized List<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public synchronized List<Game> getAllGames() {
        return allGames;
    }

    public synchronized List<GameHandler> getLiveGames() {
        return liveGames;
    }

    public GameHandler getGameHandler(String gameName) {
        for (GameHandler gameHandler : liveGames) {
            if (gameHandler.getGame().getName().equals(gameName)) {
                return gameHandler;
            }
        }
        return null;
    }

    public String getLeaderboard() {
        StringBuilder leaderboardString = new StringBuilder();
        for (Map.Entry<Player, Integer> entry : leaderboard.entrySet()) {
            leaderboardString.append(entry.getKey().getNickname()).append(": ").append(entry.getValue()).append("\n");
        }
        return leaderboardString.toString();
    }

    public synchronized void addPlayer(Player player) {
        onlinePlayers.add(player);
        if (!allPlayers.contains(player))
            allPlayers.add(player);
    }

    private synchronized void addGame(Game game) {
        if (allGames.contains(game))
            return;
        allGames.add(game);
    }

    private synchronized void addGameHandler(GameHandler gameHandler) {
        liveGames.add(gameHandler);
    }

    public void updateLeaderboard() {
        for (Player player : allPlayers) {
            leaderboard.put(player, player.getWins());
        }
    }

    private void createGame(Game game) {
        GameHandler gameHandler = new GameHandler(game, this);
        this.addGameHandler(gameHandler);
        Thread gameThread = new Thread(gameHandler);
        gameThread.start();
    }

    public synchronized void broadcast(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.getWriter().println(message);
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
}