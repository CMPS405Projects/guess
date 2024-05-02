package utils;
import java.lang.Math;

//v1.0
public class Player {
    public String ticket;
    public String nickname;

    // Status to check if the player is still in the game it will be either [gameId] or [gameId lost] or [none]
    public String status;

    // Players start with score of 5
    public int score = 5;

    public Player(String nickname){
        this.nickname = nickname;
    }
    
    //Generate ticket of 4 random numbers and name
    public void generateTicket(){
        int randomNum = (int) (1000 + Math.random() * 9000);
        this.ticket = randomNum + nickname;
    }

    // Update the score when a player loses a round
    public void lost(){
        this.score -= 1;
    }

    // Reset score when the game ends
    public void resetScore(){
        this.score = 5;
    }
}
