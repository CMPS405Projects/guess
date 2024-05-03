package server;
import java.io.*;
import java.net.*;
import java.util.*;
import utils.*;

public class ServerDriver {
  private static Server server;
    public static void main(String[] args) {
        try {
          server = new Server();
          System.out.println("Server started.");
          server.acceptClients();
          System.out.println("Server is listening for clients...");  
        } catch (Exception e) {
          System.out.println("Server could not be started.");
            e.printStackTrace();
        } finally {
            System.out.println("Server shutting down...");
            server.shutdown();
        }
    }
}
