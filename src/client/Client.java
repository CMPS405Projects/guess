package client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "localhost"; // Server IP address
    private static final int SERVER_PORT = 13337; // Server port number
    private Socket serverSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private BufferedReader clientConsole;
    private Scanner scanner;

    public Client() {
        System.out.println("Setting up the client...");
        System.out.println("Setting up the scanner...");
        this.setUpScanner();
        System.out.println("Connecting to the server...");
        this.connectToServer();
        System.out.println("Setting up server streams...");
        this.setUpServerStreams();
        System.out.println("Client set up successfully.");
    }  


    public BufferedReader getReader() {
        return this.reader;
    }

    public PrintWriter getWriter() {
        return this.writer;
    }

    public BufferedReader getClientConsole() {
        return this.clientConsole;
    }  

    public Scanner getScanner() {
        return this.scanner;
    }



    public void connectToServer() {
        while (this.serverSocket == null) {
        try {
            // Connect to the server
            this.serverSocket = new Socket(SERVER_IP, SERVER_PORT);
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
            this.reader.close();
            this.writer.close();
            this.clientConsole.close();
            this.serverSocket.close();
            System.exit(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpServerStreams() {
        try {
            // Set up input and output streams for communication with the server
            this.reader = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
            this.writer = new PrintWriter(this.serverSocket.getOutputStream(), true);
            this.clientConsole = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.println("Server streams set up successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpScanner() {
        try {
            this.scanner = new Scanner(System.in);
            System.out.println("Scanner set up successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Socket getServerSocket() {
        return this.serverSocket;
    }

    public void disconnect() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
