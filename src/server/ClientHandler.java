package server;

import utils.Game;
import utils.GameHandler;
import utils.Player;
import utils.PlayerStatus;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Server server;
    private Player player;
    private String ticket;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Send welcome message to the client
            writer.println("Welcome to the Guess 2/3 Game server!");
            writer.println("Enter `help` for a list of commands.");

            // a command line interface for the server
            String msg;
            while ((msg = reader.readLine()) != null) {
                // split msg into arguments if available
                String[] msgArray = msg.split(" ");
                String firstArg = "";
                String secondArg = "";
                String thirdArg = "";

                if (msgArray.length == 1) {
                    firstArg = msgArray[0];
                } else if (msgArray.length == 2) {
                    firstArg = msgArray[0];
                    secondArg = msgArray[1];
                } else if (msgArray.length == 3) {
                    firstArg = msgArray[0];
                    secondArg = msgArray[1];
                    thirdArg = msgArray[2];
                }

                if (firstArg.equals("exit")) {
                    break;
                }

                // switch statement to handle the different commands
                try {
                    switch (firstArg) {
                        case "help":
                            printHelpMenu();
                            // writer.println("end");
                            break;
                        case "pseudo":
                            if (secondArg.length() < 3) {
                                writer.println("Error: Pseudonym must be at least 3 characters long.");
                                // writer.println("end");
                                break;
                            }
                            player = new Player(secondArg, server);
                            this.player = player;
                            writer.println("Ticket: " + player.getTicket());
                            // writer.println("end");
                            break;
                        case "ticket":
                            boolean valid = false;
                            List<Player> players = server.getAllPlayers();
                            for (Player p : players) {
                                if (p.getTicket().equals(secondArg)) {
                                    writer.println("Welcome " + p.getNickname() + "!");
                                    // writer.println("end");
                                    valid = true;
                                    this.ticket = secondArg;
                                    break;
                                }
                            }
                            if (!valid)
                                writer.println("Error: Invalid Ticket");
                            // writer.println("end");
                            break;
                        case "join":
                            if (player == null) {
                                writer.println("Error: You must generate a ticket first.");
                                // writer.println("end");
                                break;
                            } else if (player.getGame() != null) {
                                writer.println("Error: You are already in a game.");
                                // writer.println("end");
                                break;
                            }
                            player.joinGame(secondArg, this);
                            // writer.println("end");
                            break;
                        case "ready":
                            if (player == null) {
                                writer.println("Error: You must generate a ticket first.");
                                // writer.println("end");
                                break;
                            } else if (this.ticket == null) {
                                writer.println("Error: You must validate your ticket first.");
                                // writer.println("end");
                                break;
                            }
                            if (player.getStatus().equals(PlayerStatus.READY)) {
                                writer.println("Error: You are already ready for this game.");
                                // writer.println("end");
                                break;
                            }
                            String gameName = secondArg;
                            player.ready(gameName, this);
                            // writer.println("end");
                            break;
                        case "menu":
                            if (secondArg.equals("players")) {
                                List<Player> playersList = server.getOnlinePlayers();
                                writer.println("Players: ");
                                for (Player p : playersList) {
                                    writer.println(p.getNickname());
                                }
                            } else if (secondArg.equals("games")) {
                                List<GameHandler> gamesList = server.getLiveGames();
                                writer.println("Games: ");
                                Game game;
                                for (GameHandler g : gamesList) {
                                    game = g.getGame();
                                    writer.println(game.getName());
                                }
                            } else {
                                writer.println("Error: Invalid argument. Enter `menu players` or `menu games`.");
                            }
                            // writer.println("end");
                            break;
                        case "guess":
                            if (player == null) {
                                writer.println("Error: You must generate a ticket first.");
                                // writer.println("end");
                                break;
                            } else if (this.ticket == null) {
                                writer.println("Error: You must validate your ticket first.");
                                // writer.println("end");
                                break;
                            }
                            if (!player.getGame().getName().equals(secondArg)) {

                            }
                            player.makeGuess(Integer.parseInt(thirdArg), this);
                            // writer.println("end");
                            break;
                        default:
                            writer.println("Error: Invalid command. Enter `help` for a list of commands.");
                            // writer.println("end");
                            break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.endClient(this.clientSocket);
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.close();
        }
    }

    public BufferedReader getReader() {
        return this.reader;
    }

    public PrintWriter getWriter() {
        return this.writer;
    }

    public Socket getClientSocket() {
        return this.clientSocket;
    }

    public void printHelpMenu() {
        writer.println("Command\tArguments\tAction");
        writer.println("pseudo\t[pseudonym]\tgenerate new ticket for player with pseudonym");
        writer.println("ticket\t[ticket]\tvalidate received ticket and, if valid, welcome player with pseudonym");
        writer.println("menu\tplayers/games\tlist all connected players or available games");
        writer.println("join\t[gameId]\tjoin game with gameId if it exists otherwise create a new game");
        writer.println("ready\t[gameId]\tconfirm player readiness for a game");
        writer.println("guess\t[gameId] [number]\tmake a guess for a game");
        writer.println("exit\t\texit the game");
    }

    public Player getPlayer() {
        return this.player;
    }

}