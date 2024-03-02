package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private int port;
    public int numberOfBoxes;
    private int winNumberOfBoxes;
    private char[][] board;
    private boolean player1Turn = true; // Initially, it's player 1's turn

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
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Player player = new Player(players.isEmpty() ? 'X' : 'O',out);
                players.add(player);
                PlayerHandler playerHandler = new PlayerHandler(socket,this,player);
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

    // Method to check for a win condition
    private boolean checkWinCondition(char symbol) {
        // Check rows for win
        for (int i = 0; i < numberOfBoxes; i++) {
            for (int j = 0; j <= numberOfBoxes - winNumberOfBoxes; j++) {
                boolean win = true;
                for (int k = 0; k < winNumberOfBoxes; k++) {
                    if (board[i][j + k] != symbol) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }

        // Check columns for win
        for (int j = 0; j < numberOfBoxes; j++) {
            for (int i = 0; i <= numberOfBoxes - winNumberOfBoxes; i++) {
                boolean win = true;
                for (int k = 0; k < winNumberOfBoxes; k++) {
                    if (board[i + k][j] != symbol) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }

        // Check diagonals for win
        for (int i = 0; i <= numberOfBoxes - winNumberOfBoxes; i++) {
            for (int j = 0; j <= numberOfBoxes - winNumberOfBoxes; j++) {
                boolean win = true;
                for (int k = 0; k < winNumberOfBoxes; k++) {
                    if (board[i + k][j + k] != symbol) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }

        // Check reverse diagonals for win
        for (int i = 0; i <= numberOfBoxes - winNumberOfBoxes; i++) {
            for (int j = winNumberOfBoxes - 1; j < numberOfBoxes; j++) {
                boolean win = true;
                for (int k = 0; k < winNumberOfBoxes; k++) {
                    if (board[i + k][j - k] != symbol) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }

        return false;
    }

    // Method to switch turns between players
    public void switchTurns() {
        player1Turn = !player1Turn;
    }

    // Method to check if it's currently player 1's turn
    public boolean isPlayer1Turn() {
        return player1Turn;
    }

    public void updateGUI(String msg){
        for (Player player : players) {
            player.sendMessage(msg);
        }
    }

    public static void main(String[] args) {
        Server server = new Server(9999, 6, 3); // Change parameters as needed
        server.start();
    }
}
