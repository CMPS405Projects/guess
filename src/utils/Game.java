package utils;

import java.util.ArrayList;
import java.util.Collections;

import server.ClientHandler;
import server.Server;

import java.util.List;

public class Game {
    private String name;
    private int round;
    private Server server;
    private GameStatus status;
    private List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    private final int MIN_PLAYERS = 2;
    private final int MAX_PLAYERS = 6;

    public Game(String name) {
        this.name = name;
        this.round = 1;
        this.status = GameStatus.WAITING;
    }

    public String getName() {
        return this.name;
    }

    public int getRound() {
        return round;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public List<ClientHandler> getClientHandlers() {
        return this.clientHandlers;
    }

    public int getMinPlayers() {
        return this.MIN_PLAYERS;
    }

    public int getMaxPlayers() {
        return this.MAX_PLAYERS;
    }

    public void startGame() {
        status = GameStatus.ONGOING;
    }

    public void incrementRound() {
        this.round += 1;
    }

    public void addClientHandler(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }

    public void removePlayer(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public int playersCount() {
        return clientHandlers.size();
    }

    public int activePlayersCount() {
        int count = 0;
        for (ClientHandler clientHandler : clientHandlers) {
            if (!clientHandler.getPlayer().getStatus().equals(PlayerStatus.SPECTATING)) {
                count++;
            }
        }
        return count;
    }

    public void resetPlayersSelections() {
        for (ClientHandler clientHandler : this.clientHandlers) {
            clientHandler.getPlayer().resetSelection();
        }
    }

    public void endGame() {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.getPlayer().reset();
        }
        status = GameStatus.ENDED;
    }
}
