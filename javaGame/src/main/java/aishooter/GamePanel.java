package aishooter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class GamePanel extends JPanel implements ActionListener {
    public static final int TILE_SIZE = 40;
    public static final int PLAY_COLUMNS = 20;
    public static final int PLAY_ROWS = 16;
    public static final int PLAY_WIDTH = PLAY_COLUMNS * TILE_SIZE;
    public static final int PLAY_HEIGHT = PLAY_ROWS * TILE_SIZE;
    public static final int HUD_WIDTH = 220;
    public static final int WIDTH = PLAY_WIDTH + HUD_WIDTH;
    public static final int HEIGHT = PLAY_HEIGHT;

    private static final int TIMER_DELAY_MS = 16;
    private static final int STARTING_HP = 5;
    private static final int MAX_ENEMIES = 12;

    private final Timer timer = new Timer(TIMER_DELAY_MS, this);
    private final boolean[] keys = new boolean[256];
    private final Random random = new Random();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Star> stars = new ArrayList<>();
    private final GameMap map = new GameMap(PLAY_ROWS, PLAY_COLUMNS, TILE_SIZE);
    private final ScoreBoard scoreBoard = new ScoreBoard();

    private Player player;
    private int score;
    private int hp;
    private int level;
    private int ticks;
    private int shootCooldown;
    private int spawnCooldown;
    private boolean paused;
    private boolean gameOver;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        setDoubleBuffered(true);
        addKeyListener(new GameKeyHandler());
        createStars();
        resetGame();
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (!paused && !gameOver) {
            updateGame();
        } else {
            updateStars();
        }
        repaint();
    }

    private void resetGame() {
        score = 0;
        hp = STARTING_HP;
        level = 1;
        ticks = 0;
        shootCooldown = 0;
        spawnCooldown = 45;
        paused = false;
        gameOver = false;
        bullets.clear();
        enemies.clear();
        map.rebuild(level);
        player = new Player(PLAY_WIDTH / 2.0, PLAY_HEIGHT - 72);
    }

    private void updateGame() {
        ticks++;
        updateStars();

        player.update(keys, map);
        updateShooting();
        updateBullets();
        updateEnemies();

        score += CollisionManager.resolveBulletHits(bullets, enemies);
        hp -= CollisionManager.resolvePlayerHits(player, enemies);
        removeEnemiesThatPassedPlayer();

        if (ticks % 120 == 0) {
            score += 5;
        }

        int nextLevel = Math.min(9, 1 + score / 700);
        if (nextLevel > level) {
            level = nextLevel;
            spawnCooldown = Math.min(spawnCooldown, 30);
        }

        if (hp <= 0) {
            hp = 0;
            gameOver = true;
            bullets.clear();
        }
    }

    private void updateShooting() {
        if (shootCooldown > 0) {
            shootCooldown--;
        }
        if (keys[KeyEvent.VK_SPACE] && shootCooldown == 0) {
            bullets.add(new Bullet(player.getX(), player.getY() - player.getHeight() / 2.0));
            shootCooldown = 10;
        }
    }

    private void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();
            if (!bullet.isActive() || map.overlapsObstacle(bullet.getBounds())) {
                iterator.remove();
            }
        }
    }

    private void updateEnemies() {
        spawnCooldown--;
        int spawnRate = Math.max(28, 92 - level * 7);
        if (spawnCooldown <= 0 && enemies.size() < MAX_ENEMIES) {
            spawnEnemy();
            spawnCooldown = spawnRate;
        }

        for (Enemy enemy : enemies) {
            enemy.update(map, player, level);
        }
    }

    private void spawnEnemy() {
        for (int attempt = 0; attempt < 30; attempt++) {
            int column = random.nextInt(PLAY_COLUMNS);
            int row = random.nextInt(3);
            Cell cell = new Cell(row, column);
            if (!map.isBlocked(cell)) {
                double x = map.cellCenterX(column);
                double y = map.cellCenterY(row);
                enemies.add(new Enemy(x, y));
                return;
            }
        }
    }

    private void removeEnemiesThatPassedPlayer() {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.getY() > PLAY_HEIGHT + enemy.getRadius()) {
                iterator.remove();
                hp--;
            }
        }
    }

    private void createStars() {
        stars.clear();
        Random starRandom = new Random(42);
        for (int i = 0; i < 150; i++) {
            stars.add(new Star(
                    starRandom.nextInt(PLAY_WIDTH),
                    starRandom.nextInt(PLAY_HEIGHT),
                    1 + starRandom.nextInt(3),
                    0.35 + starRandom.nextDouble() * 1.4));
        }
    }

    private void updateStars() {
        for (Star star : stars) {
            star.update(PLAY_HEIGHT);
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawPlayArea(g);
        drawEntities(g);
        scoreBoard.draw(g, PLAY_WIDTH, HUD_WIDTH, HEIGHT, score, hp, level, paused, gameOver);

        if (paused || gameOver) {
            drawCenterOverlay(g);
        }

        g.dispose();
    }

    private void drawPlayArea(Graphics2D g) {
        g.setColor(new Color(3, 8, 20));
        g.fillRect(0, 0, PLAY_WIDTH, PLAY_HEIGHT);
        for (Star star : stars) {
            star.draw(g);
        }
        map.draw(g);

        g.setColor(new Color(42, 173, 220, 120));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(1, 1, PLAY_WIDTH - 2, PLAY_HEIGHT - 2);
    }

    private void drawEntities(Graphics2D g) {
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        player.draw(g);
    }

    private void drawCenterOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, PLAY_WIDTH, PLAY_HEIGHT);

        String title = gameOver ? "GAME OVER" : "PAUSED";
        String subtitle = gameOver ? "Press R to restart" : "Press P to resume";

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 44));
        FontMetrics titleMetrics = g.getFontMetrics();
        int titleX = (PLAY_WIDTH - titleMetrics.stringWidth(title)) / 2;
        int titleY = PLAY_HEIGHT / 2 - 20;
        g.setColor(new Color(255, 213, 84));
        g.drawString(title, titleX, titleY);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        FontMetrics subtitleMetrics = g.getFontMetrics();
        int subtitleX = (PLAY_WIDTH - subtitleMetrics.stringWidth(subtitle)) / 2;
        g.setColor(Color.WHITE);
        g.drawString(subtitle, subtitleX, titleY + 40);
    }

    private final class GameKeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            int code = event.getKeyCode();
            if (code < keys.length) {
                keys[code] = true;
            }
            if (code == KeyEvent.VK_R) {
                resetGame();
            } else if (code == KeyEvent.VK_P && !gameOver) {
                paused = !paused;
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
            int code = event.getKeyCode();
            if (code < keys.length) {
                keys[code] = false;
            }
        }
    }
}
