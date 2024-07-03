import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JPanel;
import java.util.Timer;

import java.util.TimerTask;

public class ExplorerCanvas extends JPanel {
    private List<Particle> particles;
    private Sprite sprite;
    private int fps;
    private int frames;
    private long lastTime;
    private boolean explorerMode = false;
    private double spriteX, spriteY;


    public ExplorerCanvas(List<Particle> particles, double spriteX, double spriteY) {
        this.particles = particles;
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        sprite = new Sprite(640, 360); // initially center sprite at middle of the screen

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_W: // W
                    case KeyEvent.VK_UP: // up arrow key:
                        sprite.move(0, -10);
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN: // down arrow key
                        sprite.move(0, 10);
                        break;
                    case KeyEvent.VK_A: // A
                    case KeyEvent.VK_LEFT: // left arrow key
                        sprite.move(-10, 0);
                        break;
                    case KeyEvent.VK_D: // D
                    case KeyEvent.VK_RIGHT: // right arrow key
                        sprite.move(10, 0);
                        break;
                }
                repaint();
            }
        });

        // Schedule regular updates for particles
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateParticles();
                repaint();
            }
        }, 0, 16); // approximately 60 FPS
    }

    private void updateParticles() {
        for (Particle particle : particles) {
            particle.update(this);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int canvasWidth = getWidth();
        int canvasHeight = getHeight();

        int leftBoundary = (int) (sprite.getX() - 16 * 10); // 10 = particle diameter
        int rightBoundary = (int) (sprite.getX() + 16 * 10);
        int topBoundary = (int) (sprite.getY() - 9 * 10);
        int bottomBoundary = (int) (sprite.getY() + 9 * 10);

        // draw particles
        for (Particle particle : particles) {
            double particleX = particle.getX();
            double particleY = particle.getY();
            if (particleX >= leftBoundary && particleX <= rightBoundary &&
                    particleY >= topBoundary && particleY <= bottomBoundary) {
                particle.draw(g, getHeight());
            }
        }

        // draw sprite
        sprite.draw(g, canvasWidth, canvasHeight);

        frames++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= 500) { //update every 0.5 seconds
            fps = (int) (frames / ((currentTime - lastTime) / 500.0)); //calculate FPS
            frames = 0;
            lastTime = currentTime;
        }

        g.setColor(Color.BLACK);
        g.drawString("FPS: " + fps, 10, 10);

        int positionX = (int) (spriteX - getWidth() / 2);
        int positionY = (int) (spriteY - getHeight() / 2);

        g.drawString("Sprite Position: (" + positionX + ", " + positionY + ")", 10, 40);

    }

    public void toggleExplorerMode() {
        explorerMode = !explorerMode;
        repaint();
    }

    public boolean isExplorerMode() {
        return this.explorerMode;
    }
}
