package server;

import utils.Game;
import utils.Player;
import utils.PlayerStatus;

public class GameHandler implements Runnable {
    private final Game game;
    private static final int TIMEOUT_MS = 20000;
    private Server server;

    public GameHandler(Game game, Server server) {
        this.game = game;
        this.server = server;
    }


    @Override
    public void run() {
        boolean ready = false;
        boolean allSelectionsMade = false;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        synchronized (game) {
            while (elapsedTime < TIMEOUT_MS) {
                try {
                    long remainingTime = TIMEOUT_MS - elapsedTime;
                    broadcast("Waiting for players to join..." + game.playersCount() + "/" + game.getMaxPlayers()
                            + ". Remaining time: " + remainingTime + " ms");
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

            game.startGame();

            broadcast("Please enter `ready [gameName]` to start the game.");
            while (!ready) {
                try {
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
                broadcast("Round " + game.getRound() + "\nPlease enter `guess [gameName] [selection]` to run the round.");
                while (!allSelectionsMade) {
                    try {
                        game.wait();
                        allSelectionsMade = true;
                        for (ClientHandler clientHandler : game.getClientHandlers()) {
                            if (clientHandler.getPlayer().getStatus().equals(PlayerStatus.SPECTATING)) {
                                continue;
                            }
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
                    if (game.activePlayersCount() == game.getMinPlayers()
                            && game.getClientHandlers().get(i).getPlayer().getSelection() == 0) {
                        diff[i] = Double.MAX_VALUE;
                    } else {
                        diff[i] = Math
                                .abs((double) game.getClientHandlers().get(i).getPlayer().getSelection() - target);
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

                StringBuilder roundMsg = new StringBuilder();
                roundMsg.append("game " + game.getName() + " round " + game.getRound() + "\n");
                roundMsg.append("name,selection,score,won/lost/spectator\n");
                for (ClientHandler clientHandler : game.getClientHandlers()) {
                    Player player = clientHandler.getPlayer();
                    roundMsg.append(player.getNickname() + "," + player.getSelection() + "," + player.getScore() + "," + player.getStatus().toString().toLowerCase().toCharArray()[0] + "\n");
                }
                broadcast(roundMsg.toString());
                game.incrementRound();
                game.resetPlayersSelections();
                allSelectionsMade = false;
                game.notifyAll();
            }

            Player winner = null;
            for (ClientHandler clientHandler : game.getClientHandlers()) {
                if (!clientHandler.getPlayer().getStatus().equals(PlayerStatus.SPECTATING)) {
                    winner = clientHandler.getPlayer();
                    broadcast("Winner is: " + winner.getNickname());
                    winner.incrementWins();
                    server.updateLeaderboard();
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
            if (clientHandler.getPlayer().getStatus().equals(PlayerStatus.SPECTATING)) {
                continue;
            }
            clientHandler.getWriter().println(message);
        }
    }
}