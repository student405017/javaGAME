package aishooter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

public final class Player {
    private static final double SPEED = 4.2;
    private static final int WIDTH = 34;
    private static final int HEIGHT = 44;

    private double x;
    private double y;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(boolean[] keys, GameMap map) {
        double dx = 0;
        double dy = 0;
        if (pressed(keys, KeyEvent.VK_LEFT) || pressed(keys, KeyEvent.VK_A)) {
            dx -= 1;
        }
        if (pressed(keys, KeyEvent.VK_RIGHT) || pressed(keys, KeyEvent.VK_D)) {
            dx += 1;
        }
        if (pressed(keys, KeyEvent.VK_UP) || pressed(keys, KeyEvent.VK_W)) {
            dy -= 1;
        }
        if (pressed(keys, KeyEvent.VK_DOWN) || pressed(keys, KeyEvent.VK_S)) {
            dy += 1;
        }

        if (dx != 0 || dy != 0) {
            double length = Math.sqrt(dx * dx + dy * dy);
            dx = dx / length * SPEED;
            dy = dy / length * SPEED;
            move(dx, 0, map);
            move(0, dy, map);
        }
    }

    private void move(double dx, double dy, GameMap map) {
        double nextX = clamp(x + dx, WIDTH / 2.0, map.playWidth() - WIDTH / 2.0);
        double nextY = clamp(y + dy, HEIGHT / 2.0, map.playHeight() - HEIGHT / 2.0);
        Rectangle2D bounds = boundsAt(nextX, nextY);
        if (!map.overlapsObstacle(bounds)) {
            x = nextX;
            y = nextY;
        }
    }

    public Rectangle2D getBounds() {
        return boundsAt(x, y);
    }

    private Rectangle2D boundsAt(double centerX, double centerY) {
        return new Rectangle2D.Double(centerX - WIDTH / 2.0, centerY - HEIGHT / 2.0, WIDTH, HEIGHT);
    }

    public void draw(Graphics2D g) {
        int px = (int) Math.round(x);
        int py = (int) Math.round(y);

        Polygon body = new Polygon();
        body.addPoint(px, py - HEIGHT / 2);
        body.addPoint(px - WIDTH / 2, py + HEIGHT / 2);
        body.addPoint(px, py + HEIGHT / 3);
        body.addPoint(px + WIDTH / 2, py + HEIGHT / 2);

        g.setColor(new Color(215, 232, 248));
        g.fillPolygon(body);
        g.setColor(new Color(38, 111, 185));
        g.setStroke(new BasicStroke(2f));
        g.drawPolygon(body);

        g.setColor(new Color(81, 190, 255));
        g.fillRoundRect(px - 5, py - 7, 10, 18, 8, 8);

        g.setColor(new Color(255, 125, 40, 190));
        g.fillOval(px - 9, py + HEIGHT / 2 - 3, 6, 14);
        g.fillOval(px + 3, py + HEIGHT / 2 - 3, 6, 14);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    private boolean pressed(boolean[] keys, int code) {
        return code < keys.length && keys[code];
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
