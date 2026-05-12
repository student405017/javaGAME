package aishooter;

import java.util.Iterator;
import java.util.List;

public final class CollisionManager {
    private static final int SCORE_PER_ENEMY = 100;

    private CollisionManager() {
    }

    public static int resolveBulletHits(List<Bullet> bullets, List<Enemy> enemies) {
        int points = 0;
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            boolean hit = false;
            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    enemyIterator.remove();
                    bullet.deactivate();
                    points += SCORE_PER_ENEMY;
                    hit = true;
                    break;
                }
            }
            if (hit || !bullet.isActive()) {
                bulletIterator.remove();
            }
        }
        return points;
    }

    public static int resolvePlayerHits(Player player, List<Enemy> enemies) {
        int damage = 0;
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (player.getBounds().intersects(enemy.getBounds())) {
                iterator.remove();
                damage++;
            }
        }
        return damage;
    }
}
