package org.example;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Alien {
    private int x;
    private int y;
    private int width;
    private int height;
    static int ALIEN_SPEED = 1;
    private boolean movingRight = true;


    public Alien(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move() {
        if (movingRight) {
            x += ALIEN_SPEED;
            if (x + width >= GamePanel.SCREEN_WIDTH) {
                movingRight = false;
                y += height;
            }
        } else {
            x -= ALIEN_SPEED;
            if (x <= 0) {
                movingRight = true;
                y += height;
            }
        }
    }

    public int getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect(x, y, width, height);
    }

    public List<Bullet> shoot() {
        List<Bullet> bullets = new ArrayList<>();
        Random random = new Random();
        if(random.nextDouble() < 0.001) {
            bullets.add(new Bullet(x + width / 2 - GamePanel.BULLET_WIDTH / 2, y + height, GamePanel.BULLET_WIDTH, GamePanel.BULLET_HEIGHT));
        }
        return bullets;
    }
}
