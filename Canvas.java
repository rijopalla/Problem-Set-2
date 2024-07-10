import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.awt.*;
import java.util.Timer;
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
    private Timer fpsCounterTimer;
    private int totalFrames;
    private long lastFpsUpdateTime;

    public Canvas() {
        particles = new ArrayList<>();
        lastTime = System.currentTimeMillis();
        frames = 0;
        fps = 0;
        es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        fpsCounterTimer = new Timer();
        fpsCounterTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastFpsUpdateTime >= 500) {
                    fps = (int) (totalFrames / ((currentTime - lastFpsUpdateTime) / 500.0));
                    totalFrames = 0;
                    lastFpsUpdateTime = currentTime;
                    repaint(); //trigger repaint to update the FPS display
                }
            }
        }, 0, 500); //update FPS every 0.5 seconds
    }

    public Canvas(List<Particle> particles) {
        this.particles = particles;
        lastTime = System.currentTimeMillis();
        frames = 0;
        fps = 0;
        es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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

    public void clearParticles() {
        particles.clear();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        totalFrames++;
        
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
            fps = (int) (frames / ((currentTime - lastTime) / 1000.0)); //calculate FPS
            frames = 0;
            lastTime = currentTime;
        }

        //display FPS
        g.setColor(Color.BLACK);
        g.drawString("FPS: " + fps, 10, 10);
        
        
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
