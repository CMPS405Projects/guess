package utils;

import java.util.ArrayList;
import java.util.Collections;

import server.Server;

import java.lang.Math;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private static int idCounter = 0;
    private Integer id;
//    private HashMap<Player, ArrayList<Boolean>> players;
    private List<Player> players = Collections.synchronizedList(new ArrayList<>());
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
        this.players = new ArrayList<>();
        this.round = 1;
        this.name = name;
    }

    public String addPlayer(Player player) {
        String message = "";
        if (players.size() < MAX_PLAYERS) {
            players.add(player);
            message = "Player " + player.getNickname() + " added to the game!";
        } else {
            message = "Game is full!";
        }
        return message;
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void startGame() {
        status = GameStatus.ONGOING;
    }

    public void endGame() {
        status = GameStatus.ENDED;
    }

    public void resetRound() {
        for (Player player : players) {
            player.resetScore();
        }
    }

    public int playersCount() {
        return players.size();
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<Player> getPlayers() {
        return this.players;
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
