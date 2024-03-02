package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Player player;
    private Server server;
    private char[][] board;

    public PlayerHandler(Socket socket, Server server, Player p, char[][] board) {
        this.board = board;
        this.socket = socket;
        this.player = p;
        this.server = server;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            out.println(server.numberOfBoxes+","+player.getSymbol());


            while (true) {
                if (in == null) {
                    socket.close();
                    return;
                }
                String input = in.readLine();

                System.out.println(input);

                server.updateGUI(input);

                char symbol = input.split(",")[0].charAt(0);
                int i = Integer.parseInt(input.split(",")[1]);
                int j = Integer.parseInt(input.split(",")[2]);
                board[i][j] = symbol;
                if(server.checkWinCondition(symbol)){
                    notifyGameOver(symbol,true);
                    return;
                }
                if(server.checkDrawCondition()){
                    server.updateGUI("draw");
                    return;
                }
                server.switchTurns();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyGameOver(char playerSymbol, boolean isWinner) {
        server.gameOver(playerSymbol, isWinner);
    }
}
