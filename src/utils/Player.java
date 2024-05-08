package utils;

import java.lang.Math;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import server.ClientHandler;
import server.Server;

//v1.0
public class Player {
    private String nickname;
    private String ticket;
    private int score;
    private Integer wins;
    private PlayerStatus status;
    private Integer selection;
    private Server server;
    private Game game;

    public Player(String nickname, Server server) {
        this.nickname = nickname;
        this.generateTicket(nickname);
        this.resetScore();
        this.wins = 0;
        this.status = PlayerStatus.NONE;
        this.selection = null;
        this.server = server;
        this.server.addPlayer(this);
        this.game = null;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getTicket() {
        return this.ticket;
    }

    public int getScore() {
        return this.score;
    }

    public Integer getWins() {
        return wins;
    }

    public PlayerStatus getStatus() {
        return this.status;
    }

    public Integer getSelection() {
        return selection;
    }

    public Game getGame() {
        return game;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public void decrementScore() {
        this.score -= 1;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void ready(String gameName, ClientHandler clientHandler) {
        this.server.ready(this, gameName, clientHandler);
    }

    public void joinGame(String gameName, ClientHandler clientHandler) {
        this.server.joinGame(this, gameName, clientHandler);
    }

    public void makeGuess(int selection, ClientHandler clientHandler) throws Exception {
        if (selection < 0 || selection > 100) {
            throw new Exception("Selection must be between 0 and 100 inclusive.");
        }
        if (this.status.equals(PlayerStatus.JOINED)) {
            throw new Exception("You have not readied up yet.");
        } else if (this.status.equals(PlayerStatus.SPECTATING)) {
            throw new Exception("You are spectating the game. You cannot make a guess.");
        }

        synchronized (this.getGame()) {
            while (this.getSelection() != null) {
                try {
                    game.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            this.selection = selection;
            clientHandler.getWriter().println("You have selected " + selection + ".\n" + "Waiting for other to select.");
            this.getGame().notify();
        }
    }

    public void resetScore() {
        this.score = 5;
    }

    public void resetSelection() {
        this.selection = null;
    }

    public void reset() {
        this.status = PlayerStatus.NONE;
        this.selection = null;
        this.game = null;
        this.score = 5;
    }
}
