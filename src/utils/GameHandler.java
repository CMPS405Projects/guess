package utils;

import server.ClientHandler;

public class GameHandler implements Runnable {
    private Game game;
    private boolean ready = false;

    public GameHandler(Game game) {
        this.game = game;
    }

    @Override
    public void run() {

        while (!ready || game.playersCount() < game.getMinPlayers()) {
            ready = false;
            while (game.playersCount() < game.getMinPlayers()) {
                broadcast("Waiting for players to join..." + game.playersCount() + "/" + game.getMaxPlayers());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Wait for all players to ready up
            while (!ready) {
                ready = true;
                for (ClientHandler clientHandler : game.getClientHandlers()) {
                    if (!clientHandler.getPlayer().getStatus().equals(PlayerStatus.READY)) {
                        ready = false;
                        break;
                    }
                }
            }
        }

        while (game.activePlayersCount() >= game.getMinPlayers()) {
            // calculate 2/3 of the avg
            double target = 0.0;
            for (ClientHandler clientHandler : game.getClientHandlers()) {
                target += (double) (2 * clientHandler.getPlayer().getSelection()) / (3 * game.activePlayersCount());
            }

            double minDiff = Double.MAX_VALUE;
            double[] diff = new double[game.activePlayersCount()];
            for (int i = 0; i < game.activePlayersCount(); i++) {
                diff[i] = Math.abs((double) game.getClientHandlers().get(i).getPlayer().getSelection() - target);
                if (diff[i] < minDiff) {
                    minDiff = diff[i];
                }
            }

            for (int i = 0; i < game.activePlayersCount(); i++) {
                if (diff[i] == minDiff) {
                    game.getClientHandlers().get(i).getPlayer().setStatus(PlayerStatus.WON);
                } else {
                    game.getClientHandlers().get(i).getPlayer().decrementScore();
                    game.getClientHandlers().get(i).getPlayer().setStatus(PlayerStatus.LOST);
                }
            }

        }
    }

    public Game getGame() {
        return this.game;
    }

    public void broadcast(String message) {
        for (ClientHandler clientHandler : game.getClientHandlers()) {
            clientHandler.getWriter().println(message);
        }
    }
}