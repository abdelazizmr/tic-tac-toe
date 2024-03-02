package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame implements ActionListener {
    private JButton[][] buttons;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int boardSize;
    private char playerSymbol;

    public Client(String serverAddress, int port, int boardSize) {
        this.boardSize = boardSize;
        buttons = new JButton[boardSize][boardSize];
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Receive player's symbol from the server
            playerSymbol = in.readLine().charAt(0);
            System.out.println("Player symbol: " + playerSymbol);

            // Create and start threads for receiving moves and updating GUI
            new Thread(new ServerListener()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].addActionListener(this);
                boardPanel.add(buttons[i][j]);
            }
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setVisible(true);

        getContentPane().add(boardPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (e.getSource() == buttons[i][j] && buttons[i][j].getText().isEmpty()) {
                    // Send player's move to the server
                    out.println(playerSymbol + "," + i + "," + j);
                    buttons[i][j].setText(String.valueOf(playerSymbol));
                }
            }
        }
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String message = in.readLine();
                    if (message != null) {
                        // Process the received message from the server
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                String[] parts = message.split(",");
                                char symbol = parts[0].charAt(0);
                                int row = Integer.parseInt(parts[1]);
                                int col = Integer.parseInt(parts[2]);
                                buttons[row][col].setText(String.valueOf(symbol));
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 9999, 3); // Change parameters as needed
        new Client("localhost", 9999, 3); // Change parameters as needed
    }
}
