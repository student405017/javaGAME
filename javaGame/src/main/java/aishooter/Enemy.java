package aishooter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public final class Enemy {
    private static final int[][] DIRECTIONS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };
    private static final int RADIUS = 18;
    private static final int IMAGE_SIZE = 44;
    private static final BufferedImage ENEMY_IMAGE = loadEnemyImage();

    private double x;
    private double y;
    private Cell targetCell;
    private int pathCooldown;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        this.pathCooldown = 0;
    }

    public void update(GameMap map, Player player, int level) {
        pathCooldown--;
        Cell currentCell = map.worldToCell(x, y);
        Cell playerCell = map.worldToCell(player.getX(), player.getY());
        if (targetCell == null || reachedTarget(map) || pathCooldown <= 0) {
            targetCell = chooseNextCell(map, currentCell, playerCell);
            pathCooldown = Math.max(6, 18 - level);
        }

        double targetX = targetCell == null ? player.getX() : map.cellCenterX(targetCell.column());
        double targetY = targetCell == null ? player.getY() : map.cellCenterY(targetCell.row());
        moveToward(targetX, targetY, 1.15 + level * 0.18);
    }

    private Cell chooseNextCell(GameMap map, Cell currentCell, Cell playerCell) {
        List<Cell> path = map.findPath(currentCell, playerCell);
        if (path.size() > 1) {
            return path.get(1);
        }
        if (path.size() == 1) {
            return path.get(0);
        }
        return chooseFallbackNeighbor(map, currentCell, playerCell);
    }

    private Cell chooseFallbackNeighbor(GameMap map, Cell currentCell, Cell playerCell) {
        Cell best = currentCell;
        double bestDistance = distanceBetweenCells(map, currentCell, playerCell);
        for (int[] direction : DIRECTIONS) {
            Cell next = new Cell(currentCell.row() + direction[0], currentCell.column() + direction[1]);
            if (!map.isBlocked(next)) {
                double distance = distanceBetweenCells(map, next, playerCell);
                if (distance < bestDistance) {
                    best = next;
                    bestDistance = distance;
                }
            }
        }
        return best;
    }

    private double distanceBetweenCells(GameMap map, Cell first, Cell second) {
        double dx = map.cellCenterX(first.column()) - map.cellCenterX(second.column());
        double dy = map.cellCenterY(first.row()) - map.cellCenterY(second.row());
        return Math.sqrt(dx * dx + dy * dy);
    }

    private boolean reachedTarget(GameMap map) {
        if (targetCell == null) {
            return true;
        }
        double targetX = map.cellCenterX(targetCell.column());
        double targetY = map.cellCenterY(targetCell.row());
        return distanceTo(targetX, targetY) < 4;
    }

    private void moveToward(double targetX, double targetY, double speed) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) {
            return;
        }
        double step = Math.min(speed, distance);
        x += dx / distance * step;
        y += dy / distance * step;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x - RADIUS, y - RADIUS, RADIUS * 2.0, RADIUS * 2.0);
    }

    public void draw(Graphics2D g) {
        int ex = (int) Math.round(x);
        int ey = (int) Math.round(y);
        if (ENEMY_IMAGE != null) {
            drawImageEnemy(g, ex, ey);
            return;
        }

        Polygon body = new Polygon();
        body.addPoint(ex, ey + RADIUS);
        body.addPoint(ex - RADIUS, ey - RADIUS / 2);
        body.addPoint(ex - 7, ey - RADIUS);
        body.addPoint(ex, ey - 6);
        body.addPoint(ex + 7, ey - RADIUS);
        body.addPoint(ex + RADIUS, ey - RADIUS / 2);

        g.setColor(new Color(190, 42, 66));
        g.fillPolygon(body);
        g.setColor(new Color(255, 123, 85));
        g.setStroke(new BasicStroke(2f));
        g.drawPolygon(body);

        g.setColor(new Color(255, 230, 80));
        g.fill(new Ellipse2D.Double(ex - 4, ey - 4, 8, 8));

        g.setColor(new Color(255, 67, 67, 115));
        g.drawOval(ex - RADIUS - 2, ey - RADIUS - 2, RADIUS * 2 + 4, RADIUS * 2 + 4);
    }

    private void drawImageEnemy(Graphics2D g, int ex, int ey) {
        g.setColor(new Color(255, 67, 67, 115));
        g.setStroke(new BasicStroke(2f));
        g.drawOval(ex - RADIUS - 5, ey - RADIUS - 5, RADIUS * 2 + 10, RADIUS * 2 + 10);

        int imageX = ex - IMAGE_SIZE / 2;
        int imageY = ey - IMAGE_SIZE / 2;
        g.drawImage(ENEMY_IMAGE, imageX, imageY, IMAGE_SIZE, IMAGE_SIZE, null);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getRadius() {
        return RADIUS;
    }

    private double distanceTo(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static BufferedImage loadEnemyImage() {
        String[] candidates = {
                "image0.png",
                "image0.jpg",
                "image0.jpeg",
                "src/main/resources/image0.png",
                "src/main/resources/image0.jpg",
                "src/main/resources/image0.jpeg"
        };
        for (String candidate : candidates) {
            File file = new File(candidate);
            if (file.isFile()) {
                try {
                    return makeWhiteTransparent(ImageIO.read(file));
                } catch (IOException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private static BufferedImage makeWhiteTransparent(BufferedImage source) {
        if (source == null) {
            return null;
        }
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int rgb = source.getRGB(x, y);
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;
                if (red > 238 && green > 238 && blue > 238) {
                    result.setRGB(x, y, 0x00000000);
                } else {
                    result.setRGB(x, y, 0xff000000 | (rgb & 0x00ffffff));
                }
            }
        }
        return result;
    }
}
