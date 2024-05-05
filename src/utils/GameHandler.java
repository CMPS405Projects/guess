package utils;

import server.ClientHandler;

import java.util.Formatter;
import java.util.Timer;

public class GameHandler implements Runnable {
    private Game game;
    private boolean ready = false;
    private boolean allSelectionsMade = false;
    private static final int TIMEOUT_MS = 20000;

    public GameHandler(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        synchronized (game) {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;

            while (elapsedTime < TIMEOUT_MS) {
                try {
                    long remainingTime = TIMEOUT_MS - elapsedTime;
                    broadcast("Waiting for players to join..." + game.playersCount() + "/" + game.getMaxPlayers() + ". Remaining time: " + remainingTime + " ms");
                    game.wait(remainingTime);
                    elapsedTime = System.currentTimeMillis() - startTime;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (game.playersCount() < game.getMinPlayers()) {
                broadcast("Minimum players have not joined within the timeout. Game closed.");
                game.endGame();
                return;
            }

            while (!ready) {
                try {
                    broadcast("Waiting for players to be ready...");
                    game.wait();
                    ready = true;
                    for (ClientHandler clientHandler : game.getClientHandlers()) {
                        if (!clientHandler.getPlayer().getStatus().equals(PlayerStatus.READY)) {
                            ready = false;
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


            while (game.activePlayersCount() >= game.getMinPlayers()) {
                while (!allSelectionsMade) {
                    try {
                        broadcast("Waiting for players selections...");
                        game.wait();
                        allSelectionsMade = true;
                        for (ClientHandler clientHandler : game.getClientHandlers()) {
                            if (clientHandler.getPlayer().getSelection() == null) {
                                allSelectionsMade = false;
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                // calculate 2/3 of the avg
                double target = 0.0;
                for (ClientHandler clientHandler : game.getClientHandlers()) {
                    target += (double) (2 * clientHandler.getPlayer().getSelection()) / (3 * game.activePlayersCount());
                }

                double minDiff = Double.MAX_VALUE - 1;
                double[] diff = new double[game.activePlayersCount()];
                for (int i = 0; i < game.activePlayersCount(); i++) {
                    if (game.activePlayersCount() == game.getMinPlayers() && game.getClientHandlers().get(i).getPlayer().getSelection() == 0) {
                        diff[i] = Double.MAX_VALUE;
                    } else {
                        diff[i] = Math.abs((double) game.getClientHandlers().get(i).getPlayer().getSelection() - target);
                    }
                    if (diff[i] < minDiff) {
                        minDiff = diff[i];
                    }
                }

                for (int i = 0; i < game.activePlayersCount(); i++) {
                    if (diff[i] == minDiff) {
                        game.getClientHandlers().get(i).getPlayer().setStatus(PlayerStatus.WON);
                    } else {
                        game.getClientHandlers().get(i).getPlayer().decrementScore();
                        if (game.getClientHandlers().get(i).getPlayer().getScore() > 0) {
                            game.getClientHandlers().get(i).getPlayer().setStatus(PlayerStatus.LOST);
                        } else {
                            game.getClientHandlers().get(i).getPlayer().setStatus(PlayerStatus.SPECTATING);
                        }
                    }
                }

                Formatter roundMsg = new Formatter();
                roundMsg.format("game %s round %d", game.getName(), game.getRound());
                // #TODO format output
                broadcast(roundMsg.toString());
                game.resetRound();
                allSelectionsMade = false;
                game.notifyAll();
            }

            Player winner = null;
            for (ClientHandler clientHandler : game.getClientHandlers()) {
                if (!clientHandler.getPlayer().getStatus().equals(PlayerStatus.SPECTATING)) {
                    winner = clientHandler.getPlayer();
                    broadcast("Winner is: " + winner.getNickname());
                    // #TODO update leaderboard
                }
            }

            game.endGame();
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