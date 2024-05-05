package utils;

public class GameHandler implements Runnable {
    private Game game;
    private boolean ready = false;

    public GameHandler(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        // Wait for all players to ready up
        while (! ready) {
            ready = true;
            for (Player player : game.getPlayers()) {
                if (player.getStatus() != PlayerStatus.READY) {
                    ready = false;
                    break;
                }
            }
        }

        while (game.playersCount() >= game.getMinPlayers()) {
            // calculate 2/3 of the avg
            double target = 0.0;
            for (Player player : game.getPlayers()) {
                target += (double) (2 * player.getSelection()) / (3 * game.getPlayers().size());
            }

            double minDiff = Double.MAX_VALUE;
            double[] diff = new double[game.getPlayers().size()];
            for (int i = 0; i < game.getPlayers().size(); i++) {
                diff[i] = Math.abs((double) game.getPlayers().get(i).getSelection() - target);
                if (diff[i] < minDiff) {
                    minDiff = diff[i];
                }
            }

            for (int i = 0; i < game.getPlayers().size(); i++) {
                if (diff[i] == minDiff) {
                    game.getPlayers().get(i).setWinner(true);
                }else {
                    game.getPlayers().get(i).decrementScore();
                }
            }

        }
    }

    public Game getGame() {
        return this.game;
    }
}