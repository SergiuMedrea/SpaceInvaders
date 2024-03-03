package org.example;

import java.awt.*;

public class Bullet {
    private int x;
    private int y;
    private int width;
    private int height;
    private final int BULLET_SPEED = 7;

    public Bullet(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getY() {
        return y;
    }

    public void moveShip() {
        y -= BULLET_SPEED;
    }

    public void moveAlien() {
        y += BULLET_SPEED;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }
}
