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
    private Timer particleUpdateTimer;

    private static final int PERIPHERY_COLUMNS = 16;
    private static final int PERIPHERY_ROWS = 9;

    public ExplorerCanvas(List<Particle> particles, double spriteX, double spriteY) {
        this.particles = particles;
        this.sprite = new Sprite(spriteX, spriteY);

        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_W: // W
                    case KeyEvent.VK_UP: // up arrow key:
                        sprite.move(0, 10);
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN: // down arrow key
                        sprite.move(0, -10);
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

        startParticleUpdates();
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

        int leftBoundary = (int) (640 - PERIPHERY_COLUMNS * 10);
        int rightBoundary = (int) (640 + PERIPHERY_COLUMNS * 10);
        int topBoundary = (int) (360 - PERIPHERY_ROWS * 10);
        int bottomBoundary = (int) (360 + PERIPHERY_ROWS * 10);

        leftBoundary = Math.max(leftBoundary, 0);
        rightBoundary = Math.min(rightBoundary, canvasWidth);
        topBoundary = Math.max(topBoundary, 0);
        bottomBoundary = Math.min(bottomBoundary, canvasHeight);


        g.fillRect(0, 0, canvasWidth, topBoundary); // Top
        g.fillRect(0, bottomBoundary, canvasWidth, canvasHeight - bottomBoundary); // Bottom
        g.fillRect(0, topBoundary, leftBoundary, bottomBoundary - topBoundary); // Left
        g.fillRect(rightBoundary, topBoundary, canvasWidth - rightBoundary, bottomBoundary - topBoundary); // Right

        g.setColor(Color.GRAY);

        //draw particles within the periphery
        for (Particle particle : particles) {
            double particleX = particle.getX();
            double particleY = particle.getY();
            if (particleX >= leftBoundary && particleX <= rightBoundary &&
                    particleY >= topBoundary && particleY <= bottomBoundary) {
                particle.draw(g, getHeight());
            }
        }

        //draw sprite 
        sprite.draw(g, canvasWidth, canvasHeight);

        //calculate FPS
        frames++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= 500) { // Update every 0.5 seconds
            fps = (int) (frames / ((currentTime - lastTime) / 1000.0)); // Calculate FPS
            frames = 0;
            lastTime = currentTime;
        }

        //display FPS
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + fps, 10, 10);
    }

    public void toggleExplorerMode() {
        explorerMode = !explorerMode;
        repaint();
    }

    public boolean isExplorerMode() {
        return this.explorerMode;
    }

    public void startParticleUpdates() {
        if (particleUpdateTimer == null) {
            particleUpdateTimer = new Timer();
            particleUpdateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateParticles();
                    repaint();
                }
            }, 0, 16); //approximately 60 FPS
        }
    }

    public void stopParticleUpdates() {
        if (particleUpdateTimer != null) {
            particleUpdateTimer.cancel();
            particleUpdateTimer = null;
        }
    }
}
