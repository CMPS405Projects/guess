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

    public void addPlayer(Player player) {
        if (players.size() < MAX_PLAYERS) {
            players.add(player);
        } else {
            System.out.println("Game is full. Cannot add more players.");
        }
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
