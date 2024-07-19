import bagel.*;
import bagel.util.Point;
import java.util.Random;

public class Enemy extends Entity {

    private static final String IMAGE_PATH = "res/enemy.png";
    private static final int SPEED = 1;
    private static final int SPAWN_INTERVAL = 600;
    private static final int SPAWN_X_MIN = 100;
    private static final int SPAWN_X_MAX = 900;
    private int direction; // 1 for right, -1 for left
    private int frameCount;
    private Point position;
    private Image demon;
    private boolean active; // Added flag to track enemy's activity

    public Enemy(Point position) {
        super();
        demon = new Image(IMAGE_PATH);
        this.position = position;
        active = true; // Initially, the enemy is active
        spawn();
    }

    private void spawn() {
        Random rand = new Random();
        direction = rand.nextBoolean() ? 1 : -1; // Randomly choose initial direction
        frameCount = 0;
    }

    @Override
    public void update() {
        if (active) { // Only update if the enemy is active
            frameCount++;

            // Calculate the new position
            double newX = position.x + direction * SPEED;
            double newY = position.y;

            // Reverse direction when reaching the screen edges
            if (newX <= SPAWN_X_MIN || newX >= SPAWN_X_MAX) {
                direction *= -1;
            }

            // Reset frame count and respawn if necessary
            if (frameCount >= SPAWN_INTERVAL) {
                frameCount = 0;
            } else {
                position = new Point(newX, newY);
            }
            draw((int)newX);
        }
    }

    @Override
    public void draw(int x) {
        if (active) { // Only draw if the enemy is active
            demon.draw(position.x, position.y);
        }
    }

    public void deactivate() {
        active = false; // Deactivate the enemy
    }

    public boolean isActive() {
        return active;
    }

    public double getX() {
        // Assuming you want to return the x-coordinate of the left edge of the image
        return position.x;
    }

    public double getY() {
        return position.y;
    }

    @Override
    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        return 0;
    }

    @Override
    public boolean isCompleted() {
        return !active; // Mark as completed if the enemy is inactive
    }
}
