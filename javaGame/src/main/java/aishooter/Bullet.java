package aishooter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public final class Bullet {
    private static final double SPEED = 9.0;
    private static final int WIDTH = 6;
    private static final int HEIGHT = 18;

    private final double x;
    private double y;
    private boolean active = true;

    public Bullet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= SPEED;
        if (y + HEIGHT < 0) {
            active = false;
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x - WIDTH / 2.0, y - HEIGHT / 2.0, WIDTH, HEIGHT);
    }

    public void draw(Graphics2D g) {
        Rectangle2D bounds = getBounds();
        g.setColor(new Color(55, 194, 255));
        g.fill(new RoundRectangle2D.Double(bounds.getX(), bounds.getY(), WIDTH, HEIGHT, 6, 6));
        g.setColor(new Color(214, 250, 255, 150));
        g.fill(new RoundRectangle2D.Double(bounds.getX() - 2, bounds.getY() + 8, WIDTH + 4, HEIGHT, 8, 8));
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
