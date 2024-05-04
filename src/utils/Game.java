package utils;

import java.util.ArrayList;

import server.Server;

import java.lang.Math;

public class Game {
    private int id;
    private ArrayList<Player> players = new ArrayList<>();
    private GameStatus status;
    private final int MIN_PLAYERS = 2;
    private final int MAX_PLAYERS = 6;
    private Server server;
    
    public Game() {
       this.id = 1000 + (int)(Math.random() * 9000);
       this.status = GameStatus.WAITING;
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

}
