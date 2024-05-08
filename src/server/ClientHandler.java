package server;

import utils.Game;
import utils.GameStatus;
import utils.Player;
import utils.PlayerStatus;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private BufferedReader reader;
    private PrintWriter writer;
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
                            if (this.ticket != null) {
                                writer.println("You are already authenticated.");
                                break;
                            }
                            if (secondArg.length() < 3) {
                                writer.println("Error: Pseudonym must be at least 3 characters long.");
                                // writer.println("end");
                                break;
                            }
                            this.player = new Player(secondArg, server);
                            writer.println("Ticket: " + player.getTicket());
                            // writer.println("end");
                            break;
                        case "ticket":
                            if (this.ticket != null) {
                                writer.println("You are already authenticated.");
                                break;
                            }
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
                            if (!valid) {
                                writer.println("Error: Invalid Ticket");
                            }
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
                            }
                            if (this.ticket == null) {
                                writer.println("Error: You must validate your ticket first.");
                                // writer.println("end");
                                break;
                            }
                            if (player.getStatus().equals(PlayerStatus.NONE)) {
                                writer.println("Error: you must join a game first.");
                                break;
                            }
                            if (player.getGame().getStatus().equals(GameStatus.WAITING)) {
                                writer.println("Waiting for others to join to start the game. Retry later");
                                break;
                            }
                            if (player.getStatus().equals(PlayerStatus.READY)) {
                                writer.println("Error: You are already ready for this game.");
                                // writer.println("end");
                                break;
                            }
                            player.ready(secondArg, this);
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

                                // If there is no games validation
                                if (gamesList.isEmpty()) {
                                    writer.println("No games available.");
                                    continue;
                                }

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
                            }
                            if (this.ticket == null) {
                                writer.println("Error: You must validate your ticket first.");
                                // writer.println("end");
                                break;
                            }
                            if (player.getStatus().equals(PlayerStatus.NONE)) {
                                writer.println("Error: you must join a game first.");
                                break;
                            }
                            if (!player.getGame().getStatus().equals(GameStatus.ONGOING)) {
                                writer.println("Error: The game didn't start yet.");
                                break;
                            }
                            if (!player.getGame().getName().equals(secondArg)) {
                                writer.println("Invalid game. Try Again");
                            }
                            player.makeGuess(Integer.parseInt(thirdArg), this);
                            // writer.println("end");
                            break;
                        case "leaderboard":
                            writer.println(server.getLeaderboard());
                            break;
                        default:
                            writer.println("Error: Invalid command. Enter `help` for a list of commands.");
                            // writer.println("end");
                            break;
                    }
                } catch (Exception e) {
                    writer.println("An error occurred. " + e.getMessage());
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

    public Socket getClientSocket() {
        return this.clientSocket;
    }

    public BufferedReader getReader() {
        return this.reader;
    }

    public PrintWriter getWriter() {
        return this.writer;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void printHelpMenu() {
        String[][] commands = {
                {"Command", "Arguments", "Action"},
                {"pseudo", "[pseudonym]", "Generate new ticket for player with pseudonym"},
                {"ticket", "[ticket]", "Validate received ticket and, if valid, welcome player with pseudonym"},
                {"menu", "players/games", "List all connected players or available games"},
                {"join", "[gameId]", "Join game with gameId if it exists otherwise create a new game"},
                {"ready", "[gameId]", "Confirm player readiness for a game"},
                {"guess", "[gameId][number]", "Make a guess for a game"},
                {"leaderboard", "", "Show the leaderboard"},
                {"exit", "", "Exit the game"}
        };

        int[] maxLengths = new int[3];
        for (String[] command : commands) {
            for (int i = 0; i < command.length; i++) {
                maxLengths[i] = Math.max(maxLengths[i], command[i].length());
            }
        }

        String format = "| %-" + maxLengths[0] + "s | %-" + maxLengths[1] + "s | %-" + maxLengths[2] + "s |%n";

        writer.printf(format, (Object[]) commands[0]);
        writer.println("+-" + "-".repeat(maxLengths[0]) + "-+-" + "-".repeat(maxLengths[1]) + "-+-" + "-".repeat(maxLengths[2]) + "-+");
        for (int i = 1; i < commands.length; i++) {
            writer.printf(format, (Object[]) commands[i]);
        }
    }

}