import utils.Game;
import utils.Player;
import java.util.List;

public class Main implements Runnable {
    private Game game;
    private List<Player> players;

    public Main(Game game, List<Player> players) {
        this.game = game;
        this.players = players;
    }

    @Override
    public void run() {
        while (game.playersCount() >= game.getMinPlayers()) {
            // calculate 2/3 of the avg
            double target = 0.0;
            for (Player player : players) {
                target += (double) (2 * player.getSelection()) / (3 * players.size());
            }

            double minDiff = Double.MAX_VALUE;
            double[] diff = new double[players.size()];
            for (int i = 0; i < players.size(); i++) {
                diff[i] = Math.abs((double) players.get(i).getSelection() - target);
                if (diff[i] < minDiff) {
                    minDiff = diff[i];
                }
            }

            for (int i = 0; i < players.size(); i++) {
                if (diff[i] == minDiff) {
                    players.get(i).setWinner(true);
                }else {
                    players.get(i).decrementScore();
                }
            }

        }
    }
}