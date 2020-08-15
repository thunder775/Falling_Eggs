package com.falling_egg;

import javax.swing.*;


public class HomePanel extends JPanel {
    static JButton playButton = new JButton("Play Now");
    static JTextField userNameField = new JTextField();
    static JLabel userNameLabel = new JLabel("Enter Your Name");

    HomePanel() {
        playButton.setBounds(400, 10, 50, 50);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        userNameLabel.setBounds(100, 10, 100, 20);
        userNameField.setBounds(200, 10, 100, 20);
        add(userNameField);
        add(userNameField);
        add(playButton);
        playButton.addActionListener((event) -> {
            System.out.print(userNameField.getText());
            Game.panels.add(new GamePanel(userNameField.getText()), "GamePanel");
            Game.cl.show(Game.panels, "GamePanel");
        });
    }

}
