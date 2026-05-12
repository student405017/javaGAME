package aishooter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class ScoreBoard {
    private static final Color PANEL = new Color(5, 14, 29);
    private static final Color BORDER = new Color(51, 151, 198);
    private static final Color TEXT = new Color(222, 238, 247);
    private static final Color MUTED = new Color(120, 152, 170);
    private static final Color ACCENT = new Color(255, 214, 86);
    private static final Color DANGER = new Color(255, 77, 96);

    public void draw(Graphics2D g, int x, int width, int height, int score, int hp, int level,
            boolean paused, boolean gameOver) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(PANEL);
        g.fillRect(x, 0, width, height);
        g.setColor(new Color(20, 57, 78));
        g.fillRect(x, 0, 4, height);

        int cardX = x + 18;
        int cardW = width - 36;
        drawCard(g, cardX, 24, cardW, 154);
        drawLabel(g, "SCORE", cardX + 18, 58);
        drawValue(g, String.valueOf(score), cardX + 18, 91, 26, ACCENT);
        drawDivider(g, cardX + 18, 112, cardW - 36);
        drawLabel(g, "HP", cardX + 18, 139);
        drawHearts(g, cardX + 18, 157, hp);

        drawCard(g, cardX, 202, cardW, 112);
        drawLabel(g, "LEVEL", cardX + 18, 236);
        drawValue(g, String.valueOf(level), cardX + cardW - 54, 280, 36, TEXT);

        drawCard(g, cardX, 338, cardW, 188);
        drawLabel(g, "CONTROLS", cardX + 18, 372);
        drawControl(g, "WASD / ARROWS", "Move", cardX + 18, 410);
        drawControl(g, "SPACE", "Shoot", cardX + 18, 444);
        drawControl(g, "P", "Pause", cardX + 18, 478);
        drawControl(g, "R", "Restart", cardX + 18, 512);

        drawStatus(g, x, width, height, paused, gameOver);
    }

    private void drawCard(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(new Color(8, 25, 43));
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setColor(BORDER);
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(x, y, width, height, 8, 8);
    }

    private void drawLabel(Graphics2D g, String text, int x, int y) {
        g.setColor(MUTED);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        g.drawString(text, x, y);
    }

    private void drawValue(Graphics2D g, String text, int x, int y, int size, Color color) {
        g.setColor(color);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size));
        g.drawString(text, x, y);
    }

    private void drawDivider(Graphics2D g, int x, int y, int width) {
        g.setColor(new Color(74, 104, 123, 120));
        g.drawLine(x, y, x + width, y);
    }

    private void drawHearts(Graphics2D g, int x, int y, int hp) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
        for (int i = 0; i < 5; i++) {
            g.setColor(i < hp ? DANGER : new Color(65, 74, 86));
            g.drawString("\u2665", x + i * 25, y);
        }
    }

    private void drawControl(Graphics2D g, String key, String action, int x, int y) {
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        int badgeW = Math.max(26, g.getFontMetrics().stringWidth(key) + 14);
        g.setColor(new Color(12, 36, 58));
        g.fillRoundRect(x, y - 18, badgeW, 24, 6, 6);
        g.setColor(new Color(88, 175, 215));
        g.drawRoundRect(x, y - 18, badgeW, 24, 6, 6);

        g.setColor(TEXT);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(key, x + (badgeW - metrics.stringWidth(key)) / 2, y - 2);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g.setColor(TEXT);
        g.drawString(action, x + badgeW + 12, y - 2);
    }

    private void drawStatus(Graphics2D g, int x, int width, int height, boolean paused, boolean gameOver) {
        String status = gameOver ? "Press R to fly again" : paused ? "Paused" : "BFS enemies active";
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(status)) / 2;
        g.setColor(gameOver ? DANGER : paused ? ACCENT : new Color(89, 220, 170));
        g.drawString(status, textX, height - 28);
    }
}
