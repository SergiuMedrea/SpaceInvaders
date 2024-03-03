package org.example;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 1200;
    static final int SCREEN_HEIGHT = 800;
    static final int SHIP_WIDTH = 50;
    static final int SHIP_HEIGHT = 20;
    static final int ALIEN_WIDTH = 20;
    static final int ALIEN_HEIGHT = 20;
    static final int BULLET_WIDTH = 5;
    static final int BULLET_HEIGHT = 10;
    static int bulletCooldown = 0;
    static final int BULLET_COOLDOWN_TIME = 200;
    int score = 0;
    int round = 1;
    int enemies = 4;
    int alienNumber;
    boolean running = false;
    static final int DELAY = 10;
    Ship ship;
    List<Alien> aliens;
    List<Bullet> bullets;
    List<Bullet> alienBullets;
    Timer timer;
    Image backgroundImage;
    Clip destructionClip;

    GamePanel() {
        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        alienBullets = new ArrayList<>();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/6hpsdx4.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ship = new Ship(SCREEN_WIDTH / 2 - SHIP_WIDTH / 2, SCREEN_HEIGHT - 50, SHIP_WIDTH, SHIP_HEIGHT);
        running = true;
        addAliens();
        loadSoundEffect();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        draw(g);
    }

    public void loadSoundEffect() {
        try {
            AudioInputStream destructionStream = AudioSystem.getAudioInputStream(new File("src/main/resources/shoot02wav-14562.wav"));
            destructionClip = AudioSystem.getClip();
            destructionClip.open(destructionStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void playDestructionSound() {
        if (destructionClip != null) {
            destructionClip.setFramePosition(0); // Reset the sound to the beginning
            destructionClip.start();
        }
    }

    public void draw(Graphics g) {
        if(running) {
            ship.draw(g);

            g.setColor(Color.red);
            g.drawLine(0, ship.getY(), SCREEN_WIDTH, ship.getY());

            for(Alien alien : aliens) {
                alien.draw(g);
            }
            for(Bullet bullet : bullets) {
                bullet.draw(g);
            }
            for(Bullet bullet : alienBullets) {
                bullet.draw(g);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Public Pixel", Font.BOLD, 25));
            g.drawString("Score: " + score, 0, g.getFont().getSize());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Public Pixel", Font.BOLD, 25));
            g.drawString("" + aliens.size(), 0, 100);

            g.setFont(new Font("Ink Free", Font.BOLD, 35));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Round: " +  round, (SCREEN_WIDTH - metrics.stringWidth("Round: " + round)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void shootShipBullet() {
        if(bulletCooldown > 0) {
            return;
        }
        bullets.add(new Bullet(ship.getX() + SHIP_WIDTH / 2 - BULLET_WIDTH/ 2, ship.getY(), BULLET_WIDTH, BULLET_HEIGHT));
        bulletCooldown = BULLET_COOLDOWN_TIME;
    }

    public void moveBullets() {
        for(int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.moveShip();
            if(bullet.getY() < 0) {
                bullets.remove(i);
            }
        }
    }

    public void moveAlienBullets() {
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = alienBullets.get(i);
            bullet.moveAlien();
            if (bullet.getY() > SCREEN_HEIGHT) {
                alienBullets.remove(i);
            }
        }
    }

    public void addAliens() {
        for(int i = 0; i < enemies - 1; i++) {
            for(int j = 0; j < enemies; j++) {
                int x = 50 + j * 100;
                int y = 50 + i * 40;
                aliens.add(new Alien(x, y, ALIEN_WIDTH, ALIEN_HEIGHT));
            }
        }
        alienNumber = aliens.size();
    }

    public void moveAliens() {
        for(Alien alien : aliens) {
            alien.move();
        }
    }

    public void checkCollisions() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            Rectangle bulletRect = bullet.getBounds();
            for (int j = aliens.size() - 1; j >= 0; j--) {
                Alien alien = aliens.get(j);
                Rectangle alienRect = alien.getBounds();
                if (bulletRect.intersects(alienRect)) {
                    bullets.remove(i);
                    aliens.remove(j);
                    score += 1;
                    playDestructionSound();
                    if(aliens.size() <= alienNumber / 2) {
                        Alien.ALIEN_SPEED++;
                        alienNumber = aliens.size();
                    }
                    break;
                }
            }
        }
    }

    public void checkAlienBullets() {
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = alienBullets.get(i);
            Rectangle bulletRect = bullet.getBounds();
            Rectangle shipRect = ship.getBounds();
            if (bulletRect.intersects(shipRect)) {
                running = false;
                break;
            }
        }
    }

    public void shootAlienBullet() {
        for (Alien alien : aliens) {
            List<Bullet> bullets = alien.shoot();
            alienBullets.addAll(bullets);
        }
    }

    public void newRound() {
        if(aliens.isEmpty()) {
            round++;
            enemies++;
            Alien.ALIEN_SPEED = 1;
            addAliens();
        }
    }

    public void resetGame() {
        score = 0;
        round = 1;
        enemies = 4;
        Alien.ALIEN_SPEED = 1;
        aliens.clear();
        bullets.clear();
        alienBullets.clear();
        startGame();
    }

    public void checkDefeat() {
        for(Alien alien : aliens) {
            if (alien.getY() >= ship.getY()) {
                running = false;
                playDestructionSound();
                break;
            }
            if(!running) {
                timer.stop();
            }
        }
    }

    public void gameOver(Graphics g) {
        // Score
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + score, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + score)) / 2, g.getFont().getSize());

        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        // Restart text
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Press R to Restart", (SCREEN_WIDTH - metrics2.stringWidth("Press R to Restart")) / 2, SCREEN_HEIGHT / 2 + 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            moveBullets();
            moveAliens();
            shootAlienBullet();
            moveAlienBullets();
            checkCollisions();
            checkAlienBullets();
            checkDefeat();
            newRound();
            if(bulletCooldown > 0) {
                bulletCooldown -= DELAY;
            }
        }
        repaint();
         }


    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    ship.moveLeft();
                    break;
                case KeyEvent.VK_D:
                    ship.moveRight();
                    break;
                case KeyEvent.VK_SPACE:
                    shootShipBullet();
                    break;
                case KeyEvent.VK_R:
                    if(!running) {
                        resetGame();
                    }
                    break;
            }
        }
    }
}
