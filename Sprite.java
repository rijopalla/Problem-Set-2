import java.awt.*;

public class Sprite {
    private double x, y;
    private double velocity = 2.0;
    private int diameter = 10;

    public Sprite(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double dx, double dy) {
        x += dx * velocity;
        y += dy * velocity;
    }

    public void draw(Graphics g, int canvasHeight) {
        int drawY = canvasHeight - (int)y - diameter;
        g.setColor(Color.RED);
        g.fillOval((int)x, drawY, diameter, diameter);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
