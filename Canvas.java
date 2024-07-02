import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Canvas extends JPanel {
    private List<Particle> particles;
    private int fps;
    private int frames;
    private long lastTime;
    private final ExecutorService es;
    private boolean explorerMode = false;
    private Sprite sprite;
    private double spriteX = 640;
    private double spriteY = 360;

    public Canvas() {
        particles = new ArrayList<>();
        lastTime = System.currentTimeMillis();
        frames = 0;
        fps = 0;
        es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        sprite = new Sprite(spriteX, spriteY); //initialize sprite

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch(keyCode) {
                    case KeyEvent.VK_W: //W
                    case KeyEvent.VK_UP: //up arrow key:
                        if (sprite.getY() > 0)    
                            sprite.move(0, -10);
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        sprite.move(0, 10);
                        break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        sprite.move(-10, 0);
                        break;
                    case KeyEvent.VK_D:
                        sprite.move(10, 0);
                        break;
                }
                repaint();
            }
        });
    }

    public void addParticle(Particle p) {
        synchronized (particles) {
            particles.add(p);
        }
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void removeParticle(Particle p) {
        synchronized (particles) {
            particles.remove(p);
        }
        repaint();
    }

    public void setSpritePosition(double x, double y) {
        this.sprite.setX(x);
        this.sprite.setY(y);
    }

    public void toggleExplorerMode() {
        explorerMode = !explorerMode;
        repaint();
    }

    public boolean isExplorerMode() {
        return this.explorerMode;
    }

    public void drawExplorerMode(Graphics g) {
        //get periphery boundaries
        double spriteX = sprite.getX();
        double spriteY = sprite.getY();
        int leftBoundary = (int) (spriteX - 16 * 10); //10 = particle diameter
        int rightBoundary = (int) (spriteX + 16 * 10);
        int topBoundary = (int) (spriteY - 9 * 10);
        int bottomBoundary = (int) (spriteY + 9 * 10);

        //update and draw particles within the periphery
        for (Particle particle : particles) {
            particle.update(this);
            double particleX = particle.getX();
            double particleY = particle.getY();
            if (particleX >= leftBoundary && particleX <= rightBoundary &&
                particleY >= topBoundary && particleY <= bottomBoundary) {
                particle.draw(g, getHeight());
            }
        }

        //draw sprite
        sprite.draw(g, getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (explorerMode) {
            drawExplorerMode(g);
        } else {
            //update and draw particles
            List<Particle> particlesToRemove = new ArrayList<>();
            for (Particle particle : particles) {
                particle.update(this); // update the particle's position
                if (particle.checkTarget()) { //if particle has reached target
                    particlesToRemove.add(particle); //mark particle to be removed
                } else if (particle.checkTargetTheta()) { //check if target angle is reached
                    particlesToRemove.add(particle); 
                } else if (particle.checkTargetVelocity()) { //check if target velocity is reached
                    particlesToRemove.add(particle);
                } else {
                    particle.draw(g, getHeight()); // keep particle
                }
            }

            //remove particles that have reached the target
            synchronized (particles) {
                particles.removeAll(particlesToRemove);
            }

            //get FPS
            frames++;
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= 500) { //update every 0.5 seconds
                fps = (int) (frames / ((currentTime - lastTime) / 500.0)); //calculate FPS
                frames = 0;
                lastTime = currentTime;
            }

            //display FPS
            g.setColor(Color.BLACK);
            g.drawString("FPS: " + fps, 10, 10);
        }
    }

    //shutdown ExecutorService when the application exits
    public void shutdown() {
        es.shutdown();
        try {
            if (!es.awaitTermination(5, TimeUnit.SECONDS)) {
                es.shutdownNow();
            }
        } catch (InterruptedException e) {
            es.shutdownNow();
        }
    }
}