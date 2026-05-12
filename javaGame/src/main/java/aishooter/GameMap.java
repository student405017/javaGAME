package aishooter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public final class GameMap {
    private static final int[][] DIRECTIONS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    private final int rows;
    private final int columns;
    private final int tileSize;
    private final boolean[][] obstacles;

    public GameMap(int rows, int columns, int tileSize) {
        this.rows = rows;
        this.columns = columns;
        this.tileSize = tileSize;
        this.obstacles = new boolean[rows][columns];
    }

    public void rebuild(int level) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                obstacles[row][column] = false;
            }
        }

        int[][] base = {
                {2, 2}, {2, 7}, {2, 13}, {2, 17},
                {4, 4}, {4, 10}, {4, 15},
                {6, 2}, {6, 7}, {6, 13}, {6, 18},
                {8, 5}, {8, 10}, {8, 16},
                {10, 3}, {10, 8}, {10, 14}, {11, 18},
                {12, 6}, {12, 12}, {13, 16}
        };
        for (int[] cell : base) {
            placeObstacle(cell[0], cell[1]);
        }

        Random random = new Random(2400L + level * 131L);
        int extra = Math.min(12, level + 2);
        for (int i = 0; i < extra; i++) {
            int row = 1 + random.nextInt(rows - 4);
            int column = random.nextInt(columns);
            placeObstacle(row, column);
        }
    }

    public List<Cell> findPath(Cell start, Cell goal) {
        if (isBlocked(start) || isBlocked(goal)) {
            return List.of();
        }

        boolean[][] visited = new boolean[rows][columns];
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(new Node(start, null));
        visited[start.row()][start.column()] = true;

        while (!queue.isEmpty()) {
            Node current = queue.remove();
            if (current.cell().equals(goal)) {
                return buildPath(current);
            }

            for (int[] direction : DIRECTIONS) {
                int nextRow = current.cell().row() + direction[0];
                int nextColumn = current.cell().column() + direction[1];
                Cell next = new Cell(nextRow, nextColumn);
                if (isInside(next) && !visited[nextRow][nextColumn] && !isBlocked(next)) {
                    visited[nextRow][nextColumn] = true;
                    queue.add(new Node(next, current));
                }
            }
        }

        return List.of();
    }

    private List<Cell> buildPath(Node end) {
        List<Cell> path = new ArrayList<>();
        Node cursor = end;
        while (cursor != null) {
            path.add(cursor.cell());
            cursor = cursor.parent();
        }
        Collections.reverse(path);
        return path;
    }

    public boolean overlapsObstacle(Rectangle2D bounds) {
        int minColumn = clamp((int) (bounds.getMinX() / tileSize), 0, columns - 1);
        int maxColumn = clamp((int) (bounds.getMaxX() / tileSize), 0, columns - 1);
        int minRow = clamp((int) (bounds.getMinY() / tileSize), 0, rows - 1);
        int maxRow = clamp((int) (bounds.getMaxY() / tileSize), 0, rows - 1);

        for (int row = minRow; row <= maxRow; row++) {
            for (int column = minColumn; column <= maxColumn; column++) {
                if (obstacles[row][column]) {
                    Rectangle cell = new Rectangle(column * tileSize, row * tileSize, tileSize, tileSize);
                    if (bounds.intersects(cell)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Cell worldToCell(double x, double y) {
        int column = clamp((int) (x / tileSize), 0, columns - 1);
        int row = clamp((int) (y / tileSize), 0, rows - 1);
        return new Cell(row, column);
    }

    public double cellCenterX(int column) {
        return column * tileSize + tileSize / 2.0;
    }

    public double cellCenterY(int row) {
        return row * tileSize + tileSize / 2.0;
    }

    public boolean isBlocked(Cell cell) {
        return !isInside(cell) || obstacles[cell.row()][cell.column()];
    }

    public int playWidth() {
        return columns * tileSize;
    }

    public int playHeight() {
        return rows * tileSize;
    }

    public void draw(Graphics2D g) {
        drawGrid(g);
        drawObstacles(g);
    }

    private void drawGrid(Graphics2D g) {
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(44, 92, 116, 75));
        for (int column = 0; column <= columns; column++) {
            int x = column * tileSize;
            g.drawLine(x, 0, x, rows * tileSize);
        }
        for (int row = 0; row <= rows; row++) {
            int y = row * tileSize;
            g.drawLine(0, y, columns * tileSize, y);
        }
    }

    private void drawObstacles(Graphics2D g) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (obstacles[row][column]) {
                    drawAsteroid(g, column * tileSize, row * tileSize, row, column);
                }
            }
        }
    }

    private void drawAsteroid(Graphics2D g, int x, int y, int row, int column) {
        int padding = 5;
        int size = tileSize - padding * 2;
        Random shape = new Random(row * 97L + column * 53L);
        Polygon polygon = new Polygon();
        int centerX = x + tileSize / 2;
        int centerY = y + tileSize / 2;
        for (int i = 0; i < 9; i++) {
            double angle = Math.PI * 2.0 * i / 9.0;
            double radius = size * (0.36 + shape.nextDouble() * 0.16);
            polygon.addPoint(
                    centerX + (int) (Math.cos(angle) * radius),
                    centerY + (int) (Math.sin(angle) * radius));
        }
        g.setColor(new Color(88, 93, 98));
        g.fillPolygon(polygon);
        g.setColor(new Color(169, 177, 182));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(polygon);
        g.setColor(new Color(255, 255, 255, 35));
        g.fill(new Ellipse2D.Double(x + 11, y + 9, 10, 7));
    }

    private boolean isInside(Cell cell) {
        return cell.row() >= 0 && cell.row() < rows
                && cell.column() >= 0 && cell.column() < columns;
    }

    private void placeObstacle(int row, int column) {
        if (row < 1 || row >= rows - 1 || column < 0 || column >= columns) {
            return;
        }
        if (row >= rows - 4 && column >= columns / 2 - 2 && column <= columns / 2 + 2) {
            return;
        }
        obstacles[row][column] = true;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
