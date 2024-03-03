package org.example;

import java.awt.*;

public class Ship {
    private int x;
    private int y;
    private int width;
    private int height;
    private int SHIP_SPEED = 5;

    public Ship(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(Color.green);
        g.fillRect(x, y, width, height);
    }

    public void moveLeft() {
        x -= SHIP_SPEED;
        if (x < 0) {
            x = 0;
        }
    }

    public void moveRight() {
        x += SHIP_SPEED;
        if (x + width > GamePanel.SCREEN_WIDTH) {
            x = GamePanel.SCREEN_WIDTH - width;
        }
    }
}
