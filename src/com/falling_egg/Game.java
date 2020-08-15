package com.falling_egg;

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {
    static CardLayout cl = new CardLayout();
    static JPanel panels = new JPanel();

    Game() {
        panels.setLayout(cl);
        panels.add(new HomePanel(), "HomePanel");
        cl.show(panels, "HomePanel");
        add(panels);
        setTitle("Falling Eggs Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 700);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Game();
    }
}
