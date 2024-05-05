package utils;

import java.util.ArrayList;
import java.util.Collections;

import server.ClientHandler;
import server.Server;

import java.lang.Math;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private static int idCounter = 0;
    private Integer id;
    private List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    private GameStatus status;
    private int round;
    private final int MIN_PLAYERS = 2;
    private final int MAX_PLAYERS = 6;
    private Server server;
    private String name;

    public Game(String name) {
        idCounter++;
        this.id = idCounter + (int) (Math.random() * 9000);
        this.status = GameStatus.WAITING;
        this.round = 1;
        this.name = name;
    }

    public void addClientHandler(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }

    public List<ClientHandler> getClientHandlers() {
        return this.clientHandlers;
    }

    public void removePlayer(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public void startGame() {
        status = GameStatus.ONGOING;
    }

    public void endGame() {
        status = GameStatus.ENDED;
    }

    public void resetRound() {
        for (ClientHandler clientHandler : this.clientHandlers) {
            clientHandler.getPlayer().resetSelection();
        }
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

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getMinPlayers() {
        return this.MIN_PLAYERS;
    }

    public int getMaxPlayers() {
        return this.MAX_PLAYERS;
    }

    public GameStatus getStatus() {
        return this.status;
    }

}
