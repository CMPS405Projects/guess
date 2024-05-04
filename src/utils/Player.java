package utils;

import java.lang.Math;

//v1.0
public class Player {
    private String ticket;
    private String nickname;
    // Status to check if the player is still in the game it will be either [gameId] or [gameId lost] or [none]
    private String status;
    // Players start with score of 5
    private int score;
    private int selection;
    private boolean isWinner;

    public Player(String nickname) {
        this.nickname = nickname;
        this.generateTicket();
        this.resetScore();
        this.status = "none";
        this.selection = 0;
        this.isWinner = false;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTicket() {
        return ticket;
    }

    public String getStatus() {
        return status;
    }

    public int getScore() {
        return score;
    }

    public int getSelection() {
        return selection;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void decrementScore() {
        this.score -= 1;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    //Generate ticket of 4 random numbers and name
    public void generateTicket() {
        int randomNum = (int) (1000 + Math.random() * 9000);
        this.ticket = randomNum + nickname;
    }

    // Reset score when the game ends
    public void resetScore() {
        this.score = 5;
    }
}
