package org.example;

import javax.swing.*;

public class SpaceInvaders extends JFrame {
    SpaceInvaders() {
        this.add(new GamePanel());
        this.setTitle("Space Invaders");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
