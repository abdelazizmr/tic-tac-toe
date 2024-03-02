package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame implements ActionListener {
    private JButton[][] buttons;
    private JLabel playerLabel;
    private JTextArea messageArea;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int boardSize;
    private char playerSymbol;

    public Client(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String[] info = in.readLine().split(",");
            boardSize = Integer.parseInt(info[0]);
            playerSymbol = info[1].charAt(0);


            initializeGUI();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(400, 400);
            setVisible(true);

            new Thread(new ServerListener()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeGUI() {
        buttons = new JButton[boardSize][boardSize]; // Adjust button array size
        JPanel boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].addActionListener(this);
                boardPanel.add(buttons[i][j]);
            }
        }
        playerLabel = new JLabel("Player => ( "+playerSymbol+" )");
        messageArea = new JTextArea("Your turn \n", 10, 10);
        messageArea.setEditable(false);

        JPanel infoPanel = new JPanel(new GridLayout(1, 1));
        infoPanel.add(playerLabel);

        JScrollPane scrollPane = new JScrollPane(messageArea);
        JPanel consolePanel = new JPanel(new GridLayout(1, 1));
        consolePanel.add(scrollPane);

        getContentPane().add(infoPanel, BorderLayout.NORTH);
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(consolePanel,BorderLayout.SOUTH);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (e.getSource() == buttons[i][j] && buttons[i][j].getText().isEmpty()) {
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
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (message.contains("won")) {
                                    char winnerSymbol = message.charAt(0);
                                    if (winnerSymbol == playerSymbol) {
                                        JOptionPane.showMessageDialog(Client.this, "You won!");
                                        System.exit(0);
                                    } else {
                                        JOptionPane.showMessageDialog(Client.this, "You lost!");
                                        System.exit(0);
                                    }
                                } else if (message.contains("lost")) {
                                    char loserSymbol = message.charAt(0);
                                    if (loserSymbol == playerSymbol) {
                                        JOptionPane.showMessageDialog(Client.this, "You lost!");
                                        System.exit(0);
                                    } else {
                                        JOptionPane.showMessageDialog(Client.this, "You won!");
                                        System.exit(0);
                                    }
                                }else if(message.contains("draw")){
                                    JOptionPane.showMessageDialog(Client.this, "It is Draw !");
                                    System.exit(0);
                                } else {
                                    String[] parts = message.split(",");
                                    char symbol = parts[0].charAt(0);
                                    int row = Integer.parseInt(parts[1]);
                                    int col = Integer.parseInt(parts[2]);
                                    buttons[row][col].setText(String.valueOf(symbol));
                                }
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
        SwingUtilities.invokeLater(() -> new Client("localhost", 9999));
        SwingUtilities.invokeLater(() -> new Client("localhost", 9999));
    }
}
