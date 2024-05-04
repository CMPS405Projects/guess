package client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket serverSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private BufferedReader clientConsole;
    private Scanner scanner;

    public void connectToServer(String serverIp, int serverPort) {
        while (this.serverSocket == null) {
        try {
            // Connect to the server
            this.serverSocket = new Socket(serverIp, serverPort);
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

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public void setClientConsole(BufferedReader clientConsole) {
        this.clientConsole = clientConsole;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
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

}
