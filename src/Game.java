import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    public Game() {
        JFrame frame = new JFrame("Simple Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        KeyInput keyInput = new KeyInput();
        frame.addKeyListener(keyInput);
    }

    private int playerX = WIDTH / 2;
    private int playerY = HEIGHT / 2;
    private int playerSpeed = 5;

    public class KeyInput implements KeyListener {
        private boolean[] keys = new boolean[256];
        public boolean up, down, left, right;

        public void tick() {
            playerX += playerSpeed; // Move player to the right
            if (playerX > WIDTH) {
                playerX = 0; // Wrap player around to the left side of the screen
            }
            if(up){
                playerY -= playerSpeed;
            }
            if(down){
                playerY += playerSpeed;
            }
            if(left){
                playerX -= playerSpeed;
            }
            if(right){
                playerX += playerSpeed;
            }
            up = keys[KeyEvent.VK_W];
            down = keys[KeyEvent.VK_S];
            left = keys[KeyEvent.VK_A];
            right = keys[KeyEvent.VK_D];
            // Game logic goes here
        }

        public void keyPressed(KeyEvent e) {
            keys[e.getKeyCode()] = true;
        }

        public void keyReleased(KeyEvent e) {
            keys[e.getKeyCode()] = false;
        }

        public void keyTyped(KeyEvent e) {}
    }

    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(playerX, playerY, 32, 32);
    }

    public void start() {
        // Main game loop goes here
        while(true){
            //keyInput.tick();
            //render();
        }
    }

    public static void main(String[] args) {
        new Game().start();
    }
}
