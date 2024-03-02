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

    public PlayerHandler(Socket socket,Server server, Player p) {
        this.server = server;
        this.socket = socket;
        this.player = p;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            out.println(player.getSymbol()); // Send player symbol to client

            while (true) {
                // Handle client input and game logic
                String input = in.readLine();
                System.out.println(input);
                server.updateGUI(input);
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