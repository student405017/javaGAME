package aishooter;

import java.awt.Color;
import java.awt.Graphics2D;

public final class Star {
    private final int size;
    private final double speed;
    private final int alpha;
    private double x;
    private double y;

    public Star(double x, double y, int size, double speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.alpha = Math.min(255, 90 + size * 45);
    }

    public void update(int maxY) {
        y += speed;
        if (y > maxY) {
            y = -size;
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(210, 236, 255, alpha));
        g.fillRect((int) x, (int) y, size, size);
    }
}
