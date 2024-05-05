package utils;

import server.ClientHandler;

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

                double minDiff = Double.MAX_VALUE;
                double[] diff = new double[game.activePlayersCount()];
                for (int i = 0; i < game.activePlayersCount(); i++) {
                    diff[i] = Math.abs((double) game.getClientHandlers().get(i).getPlayer().getSelection() - target);
                    if (diff[i] < minDiff) {
                        minDiff = diff[i];
                    }
                }

//                if (game.activePlayersCount() == game.getMinPlayers()) {
//                    for (ClientHandler clientHandler : game.getClientHandlers()) {
//                        if (clientHandler.getPlayer().getSelection() == 0) {
//                            clientHandler.getPlayer().decrementScore();
//                            if (clientHandler.getPlayer().getScore() > 0) {
//                                clientHandler.getPlayer().setStatus(PlayerStatus.LOST);
//                            } else {
//                                clientHandler.getPlayer().setStatus(PlayerStatus.SPECTATING);
//                            }
//                        } else {
//                            clientHandler.getPlayer().setStatus(PlayerStatus.WON);
//                        }
//                    }
//                } else {
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
//                }
                StringBuilder roundMsg = new StringBuilder();
                roundMsg.append("game ").append(game.getName()).append("round ").append(game.getRound()).append(" ");
                roundMsg.append("target ").append(target);
                for (ClientHandler clientHandler : game.getClientHandlers()) {
                    roundMsg.append("player: ").append(clientHandler.getPlayer().getNickname())
                            .append(" ").append("score: ").append(clientHandler.getPlayer().getScore()).append("\n");
                }
                broadcast(roundMsg.toString());
                game.resetRound();
                allSelectionsMade = false;
                game.notifyAll();
            }
            broadcast("Winner is: " + game.getClientHandlers().getFirst().getPlayer().getNickname());
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