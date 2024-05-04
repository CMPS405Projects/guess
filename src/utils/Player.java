package utils;
import java.lang.Math;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import server.Server;

//v1.0
public class Player implements Runnable {
    private String ticket;
    // pseudonym
    private String nickname;
    // Status to check if the player is still in the game it will be either [gameId] or [gameId lost] or [none]
    private String status;
    // Players start with score of 5
    private int score = 5;
    private Server server;

    public Player(String nickname, Server server){
        this.nickname = nickname;
        this.server = server;
        this.generateTicket(nickname);
        this.server.addPlayer(this);
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
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

    // Update the score when a player loses a round
    public void lost(){
        this.score -= 1;
    }

    // Reset score when the game ends
    public void resetScore(){
        this.score = 5;
    }



    public String getTicket() {
        return this.ticket;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getStatus() {
        return this.status;
    }

    public int getScore() {
        return this.score;
    }
}
