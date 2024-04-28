package utils;

import java.util.ArrayList;
import java.lang.Math;

public class Game {
    // a 4-digit number to identify the game
    public int id = 1000 + (int)(Math.random() * 9000);
    public String name;
    public int MAX_PLAYERS;
    public ArrayList<Player> players = new ArrayList<>();

    // Game Status
    public GameStatus status = GameStatus.WAITING;

    public Game(String name, int MAX_PLAYERS) {
        this.name = name;
        this.MAX_PLAYERS = MAX_PLAYERS;
    }

    public String  addPlayer(Player player) {
        String message = "";
        if (players.size() < MAX_PLAYERS) {
            players.add(player);
            message = "Player " + player.name + " added to the game!";
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
