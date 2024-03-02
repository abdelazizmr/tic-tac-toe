package server;

import java.io.PrintWriter;

public class Player {
    private char symbol;
    private PrintWriter out;

    public Player(char symbol, PrintWriter out) {
        this.symbol = symbol;
        this.out = out;
    }


    public char getSymbol() {
        return symbol;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }
}
