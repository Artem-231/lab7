// src/main/java/app/server/ServerMain.java
package app.server;

import app.managers.CommandManager;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        int port = 34567;
        CommandManager manager = new CommandManager();

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket client = server.accept();
                new Thread(new Session(client, manager)).start();
            }
        }
    }
}
