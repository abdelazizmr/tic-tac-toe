package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private int port;
    private int numberOfBoxes;
    private int winNumberOfBoxes;
    private char[][] board;

    private ArrayList<Player> players = new ArrayList<>();

    public Server(int port, int numberOfBoxes, int winNumberOfBoxes) {
        this.port = port;
        this.numberOfBoxes = numberOfBoxes;
        this.winNumberOfBoxes = winNumberOfBoxes;
        this.board = new char[numberOfBoxes][numberOfBoxes];
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for players...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Player connected: " + socket);
                Player player = new Player(players.isEmpty() ? 'X' : 'O');
                players.add(player);
                PlayerHandler playerHandler = new PlayerHandler(socket,player);
                playerHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    public static void main(String[] args) {
        Server server = new Server(9999, 3, 3); // Change parameters as needed
        server.start();
    }
}
