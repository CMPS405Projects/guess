import utils.Player;

import java.io.*;
import java.net.*;
import java.util.List;


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;


    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Send welcome message to the client
            writer.println("Welcome to the Guess 2/3 Game server!");
            writer.println("Enter `help` for a list of commands.");

            // a command line interface for the server
            while (true) {

                String choice = reader.readLine();

                // split choice into arguments if available
                String[] choiceArray = choice.split(" ");
                String firstArg = "";
                String secondArg = "";
                String thirdArg = "";

                if (choiceArray.length == 1) {
                    firstArg = choiceArray[0];
                } else if (choiceArray.length == 2) {
                    firstArg = choiceArray[0];
                    secondArg = choiceArray[1];
                } else if (choiceArray.length == 3) {
                    firstArg = choiceArray[0];
                    secondArg = choiceArray[1];
                    thirdArg = choiceArray[2];
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
                        Server.addPlayer(player);
                        writer.println("Ticket generated for " + player.name + ": " + player.ticket);
                        break;
                    case "ticket":
                        boolean flag = false;
                        List<Player> players = Server.getPlayers();
                        for (Player p : players) {
                            if (p.ticket.equals(secondArg)) {
                                writer.println("Welcome " + p.name);
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
}
