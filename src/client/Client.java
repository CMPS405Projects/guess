package client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "localhost"; // Server IP address
    private static final int SERVER_PORT = 13337; // Server port number
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private BufferedReader clientConsole;
    private Scanner scanner;
    private String lastMessage;

    public Client() {
        System.out.println("Setting up the client...");
        System.out.println("Setting up the scanner...");
        // this.setUpScanner();
        System.out.println("Connecting to the server...");
        this.connectToServer();
        System.out.println("Setting up server streams...");
        // this.setUpServerStreams();
        System.out.println("Client set up successfully.");
    }

    public String getLastMessage() {
        return this.lastMessage;
    }

    public String readConsole() {
        try {
            String message = this.clientConsole.readLine();
            System.out.println("Message read from console: " + message);
            return message;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String readMessage() {
        try {
            String msg = reader.readLine();
            this.lastMessage = msg;
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BufferedReader getClientConsole() {
        return this.clientConsole;
    }  

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void connectToServer() {
        while (this.clientSocket == null) {
        try {
            // Connect to the server
            this.clientSocket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to the server.");
        } catch (IOException e) {
            System.out.println("An error occurred while connecting to the server.");
            System.out.println("Press any key to try again. Enter 'exit' to quit.");
            // Pause the program until the user presses a key
            String choice = this.scanner.nextLine();
            if (choice.equals("exit")) {
                this.exit(1);
            }
        }
        }
    }

    public void exit(int code) {
        try {
            this.clientSocket.close();
            this.reader.close();
            this.writer.close();
            this.clientConsole.close();
            System.exit(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpServerStreams() {
        try {
            // Set up input and output streams for communication with the server
            this.reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.writer = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.clientConsole = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.println("Server streams set up successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpScanner() {
        try {
            this.scanner = new Scanner(System.in);
            System.out.println("Scanner set up successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scanner getScanner() {
        return this.scanner;
    }

    public Socket getClientSocket() {
        return this.clientSocket;
    }

    public void disconnect() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
