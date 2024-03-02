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
    private boolean player1Turn = true;

    private ArrayList<Player> players = new ArrayList<>();

    public Server(int port, int numberOfBoxes, int winNumberOfBoxes) {
        this.port = port;
        try {
            if (winNumberOfBoxes > numberOfBoxes) {
                throw new Exception("Number of boxes should be bigger than the winNumberOfBoxes");
            }
            if (numberOfBoxes > 10) {
                throw new Exception("Max number of boxes allowed is 10");
            }
            this.numberOfBoxes = numberOfBoxes;
            this.winNumberOfBoxes = winNumberOfBoxes;
            this.board = new char[numberOfBoxes][numberOfBoxes];
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        start();
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
                PlayerHandler playerHandler = new PlayerHandler(socket,this,player,board);
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

    public boolean checkWinCondition(char symbol) {

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

    public boolean checkDrawCondition() {
        for (int i = 0; i < numberOfBoxes; i++) {
            for (int j = 0; j < numberOfBoxes; j++) {
                if (board[i][j] == '\u0000') {
                    // If any cell is empty, the game is not a draw
                    return false;
                }
            }
        }
        // If all cells are filled and no player has won, it's a draw
        return true;
    }

    public void switchTurns() {
        player1Turn = !player1Turn;
    }

    public boolean isPlayer1Turn() {
        return player1Turn;
    }

    public void updateGUI(String msg){
        for (Player player : players) {
            player.sendMessage(msg);
        }
    }

    public void gameOver(char playerSymbol, boolean isWinner) {
        String message;
        if (isWinner) {
            message = playerSymbol + "won";
        } else {
            message = playerSymbol + "lost";
        }
        updateGUI(message);
    }

    public static void main(String[] args) {
        new Server(9999, 3, 3);
    }
}
