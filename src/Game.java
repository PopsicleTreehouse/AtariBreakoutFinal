import java.util.*;
import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class Game {
    private Paddle paddle;
    ArrayList<GameComponent> bricks;
    ArrayList<Integer> actions;
    private Ball ball;
    private int score;
    public int lives;
    // private NewSoundPlayer player;
    private SoundPlayer player;
    private Image gameOver;
    public static final int BRICK_WIDTH = 70;
    public static final int BRICK_HEIGHT = 40;
    public static final int PADDLE_WIDTH = 100;
    public static final int PADDLE_HEIGHT = 20;
    public static final int BALL_WIDTH = 10;
    public static final int BALL_HEIGHT = 10;
    public static final int SPEED_CAP = 25;
    public static final int SPEED_INCREMENT = 5;

    // ball hitting the side of the brick causes errors

    public Game() {
        // player = new NewSoundPlayer();
        bricks = new ArrayList<>();
        actions = new ArrayList<>();
        paddle = new Paddle(300, 500, PADDLE_WIDTH, PADDLE_HEIGHT);
        // ball = new Ball(300, 450, BALL_WIDTH, BALL_HEIGHT, 10, -10);
        ball = new Ball(150, 350, BALL_WIDTH, BALL_HEIGHT, 10, -10);
        lives = 1;
        score = 0;
        player = new SoundPlayer();
        player.play(3, 0, true);
        loadImages();
        respawnBricks();

    }

    public void loadImages() {
        try {
            gameOver = ImageIO.read(new File("./images/gameOver.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawGame(Graphics g) {
        drawStuff(g);
    }

    public void drawStuff(Graphics g) {
        paddle.draw(g, Color.GREEN);
        ball.draw(g, Color.WHITE);
        for (GameComponent gc : bricks) {
            gc.draw(g);
        }
        g.setColor(Color.WHITE);
        g.drawString("Lives: " + lives, 550, 550);
        g.drawString("Score: " + score, 650, 550);
        if (lives == 0) {
            // g.setColor(new Color(0, 0, 0, 0.75f)); // 50% darker (change to 0.25f for 25%
            // darker)
            // g.fillRect(0, 0, Board.WIDTH, Board.HEIGHT);
            try {
                BufferedImage bimg = ImageIO.read(new File("./images/gameOver.jpg"));
                int width = bimg.getWidth();
                int height = bimg.getHeight();
                int x = Board.WIDTH - width;
                int y = Board.HEIGHT - height;
                g.drawImage(gameOver, x / 2, y / 2, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void moveObjects() {
        paddle.mouseMove();
        ball.move();
    }

    public void update() {
        moveObjects();
        checkCollisions();
        respawnBricks();
    }

    public void respawnBricks() {
        if (bricks.size() == 0) {
            Color color = null;
            Color[] colors = new Color[] { Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE };
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 10; c++) {
                    color = colors[r];
                    bricks.add(new GameComponent(c * (BRICK_WIDTH + 5), r * (BRICK_HEIGHT + 30),
                            BRICK_WIDTH, BRICK_HEIGHT, color));
                }
            }
        }
    }

    public void checkCollisions() {
        if (checkWalls()) {
            player.play(2, 0);
            return;
        }
        if (ball.touching(paddle)) {
            ball.bounced(paddle.getRect());
            player.play(2, 0);
            return;
        }
        if (ball.getRect().y + ball.getRect().height >= Board.HEIGHT) {
            player.play(1, 0);
            try {
                lives--;
                int randX = ((int) (Math.random() * Board.WIDTH - 99)) + 100;
                double rand = Math.random();
                int xv = (rand > 0.5) ? -10 : 10;
                ball = new Ball(randX, 300, BALL_WIDTH, BALL_HEIGHT, xv, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int idx = 0;
        while (idx < bricks.size()) {
            GameComponent brick = bricks.get(idx);
            Rectangle brickRect = brick.getRect();
            Rectangle ballRect = ball.getRect();
            if (!brick.touching(ball)) {
                idx++;
                continue;
            }
            // first one is checking the bottom, the second one is checking the top
            boolean ballIntersectingVert = ballRect.y == brickRect.y + brickRect.height
                    || ballRect.y + ballRect.height == brickRect.y;
            ball.changeDir(ballIntersectingVert);
            player.play(2, 0);
            bricks.remove(idx);
            score += 100;
            if (Math.abs(ball.getDx()) < SPEED_CAP || Math.abs(ball.getDy()) < SPEED_CAP) {
                if (ball.getDx() > 0 || ball.getDy() > 0) {
                    ball.setDx(ball.getDx() + SPEED_INCREMENT);
                    return;
                }
                if (ball.getDx() < 0 || ball.getDy() < 0) {
                    ball.setDx(ball.getDx() - SPEED_INCREMENT);
                    return;
                }
            }
        }
    }

    public boolean checkWalls() {
        if (ball.getRect().x + BALL_WIDTH >= Board.WIDTH || ball.getRect().x <= 0) {
            ball.changeDir(false);
            return true;
        } else if (ball.getRect().y + BALL_HEIGHT <= 0 || ball.getRect().y >= Board.HEIGHT) {
            ball.changeDir(true);
            return true;
        }
        return false;
    }

    public void ltHit(ActionEvent e) {
        actions.add(0);
    }

    public void leftReleased(ActionEvent e) {
        System.out.println("Released Left!!");
    }

    public void rtHit(ActionEvent e) {
        actions.add(1);
    }

    public void rightReleased(ActionEvent e) {
        System.out.println("Released Right!!");
    }

}
