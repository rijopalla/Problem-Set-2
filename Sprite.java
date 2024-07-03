import java.awt.*;

public class Sprite {
    private double x, y;
    private double initialX, initialY;
    private double velocity = 2.0;
    private int diameter = 10;

    public Sprite(double x, double y) {
        this.x = x;
        this.y = y;
        this.initialX = x;
        this.initialY = y;
    }

    public void move(double dx, double dy) {
        x += dx * velocity;
        y += dy * velocity;
    }
    public int getDiameter() {
        return this.diameter;
    }

    public void draw(Graphics g, int canvasWidth, int canvasHeight) {
        int centerX = canvasWidth / 2;
        int centerY = canvasHeight / 2;
        int drawX = centerX - (int)(initialX - x);
        int drawY = centerY - (int)(initialY - y);
        drawY = canvasHeight - drawY - diameter; //invert y-coordinates
        g.setColor(Color.RED);
        g.fillOval(drawX, drawY, diameter, diameter);
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
