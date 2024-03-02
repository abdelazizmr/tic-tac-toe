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
    private String[][] board;

    public PlayerHandler(Socket socket, Server server, Player p) {

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
                // Handle client input and game logic
                String input = in.readLine();
                String symbol = input.split(",")[0];
                int i = Integer.parseInt(input.split(",")[1]);
                int j = Integer.parseInt(input.split(",")[2]);

                board[i][j] = symbol;


                System.out.println(input);
                server.updateGUI(input);
                server.switchTurns(); // Switch turns after receiving a move
                // Process client input and update game state accordingly
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
}
