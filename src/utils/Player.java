package utils;

import java.lang.Math;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import server.ClientHandler;
import server.Server;

//v1.0
public class Player {
    private String ticket;
    // pseudonym
    private String nickname;
    // Status to check if the player is still in the game it will be either [gameId]
    // or [gameId lost] or [none]
    private PlayerStatus status;
    // Players start with score of 5
    private int score;
    private Server server;
    private Integer selection;
    private Game game;
    private Integer wins;

    public Player(String nickname, Server server) {
        this.nickname = nickname;
        this.server = server;
        this.generateTicket(nickname);
        this.server.addPlayer(this);
        this.resetScore();
        this.status = PlayerStatus.NONE;
        this.selection = null;
    }

    public void ready(String gameName, ClientHandler clientHandler) {
        this.server.ready(this, gameName, clientHandler);
    }

    public void joinGame(String gameName, ClientHandler clientHandler) {
        this.server.joinGame(this, gameName, clientHandler);
    }

    public void makeGuess(int selection, ClientHandler clientHandler) {
        synchronized (this.getGame()) {
            if (this.status.equals(PlayerStatus.JOINED)) {
                clientHandler.getWriter().println("You have not readied up yet.");
                return;
            } else if (this.status.equals(PlayerStatus.SPECTATING)) {
                clientHandler.getWriter().println("You are spectating the game. You cannot make a guess.");
                return;
            }
            while (this.getSelection() != null) {
                try {
                    game.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            this.selection = selection;
            clientHandler.getWriter().println("You have selected " + selection + ".");
            this.getGame().notify();
        }
    }

    private void generateTicket(String nickname) {
        byte[] hash = String.format("%32s", nickname).getBytes();
        try {
            for (int i = 0; i < Math.random() * 64 + 1; ++i) {
                hash = MessageDigest.getInstance("SHA-256").digest(hash);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.ticket = HexFormat.ofDelimiter(":").formatHex(hash).toString().substring(78);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public void decrementScore() {
        this.score -= 1;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    // Reset score when the game ends
    public void resetScore() {
        this.score = 5;
    }

    public String getTicket() {
        return this.ticket;
    }

    public String getNickname() {
        return this.nickname;
    }

    public PlayerStatus getStatus() {
        return this.status;
    }

    public int getScore() {
        return this.score;
    }

    public Integer getSelection() {
        return selection;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void resetSelection() {
        this.selection = null;
    }

    public Integer getWins() {
        return wins;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void reset() {
        this.status = PlayerStatus.NONE;
        this.selection = null;
        this.game = null;
        this.score = 5;
    }
}
