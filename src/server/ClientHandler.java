package server;
import utils.Player;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Server server;
    
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
            while ((msg = reader.readLine()) != null){
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
                switch (firstArg) {
                    case "help":
                        writer.println("Command\tArguments\tAction");
                        writer.println("pseudo\t[pseudonym]\tgenerate new ticket for player with pseudonym");
                        writer.println("ticket\t[ticket]\tvalidate received ticket and, if valid, welcome player with pseudonym");
                        writer.println("join\t[gameId]\tjoin game with gameId if it exists otherwise create a new game");
                        writer.println("ready\t[gameId]\tconfirm player readiness for a game");
                        writer.println("guess\t[gameId] [number]\tmake a guess for a game");
                        writer.println("exit\t\texit the game");
                        break;
                    case "pseudo":
                        if (secondArg.length() < 3) {
                            writer.println("Error: Pseudonym must be at least 3 characters long.");
                            break;
                        }
                        Player player = new Player(secondArg);
                        player.generateTicket();
                        server.addPlayer(player);
                        writer.println("Ticket generated for " + player.nickname + ": " + player.ticket);
                        break;
                    case "ticket":
                        boolean flag = false;
                        List<Player> players = server.getConnectedPlayers();
                        for (Player p : players) {
                            if (p.ticket.equals(secondArg)) {
                                writer.println("Welcome " + p.nickname + "!");
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            writer.println("Error: Invalid Ticket");
                        }
                        break;
                    default:
                        writer.println("Error: Invalid command. Enter `help` for a list of commands.");
                        break;
                }


            }

            reader.close();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BufferedReader getReader() {
        return this.reader;
    }

    public PrintWriter getWriter() {
        return this.writer;
    }


}
