import bagel.*;
import bagel.util.*;

/**
 * Class for normal notes
 */
public class Note extends Entity {
    private Image image;
    private final int appearanceFrame;
    private final int speed = 2;
    private int y = 100;
    private double x; // Added x-coordinate field
    private boolean active = false;
    private boolean completed = false;

    public Note(String dir, int appearanceFrame, double x) {
        super();
        image = new Image("res/note" + dir + ".png");
        this.appearanceFrame = appearanceFrame;
        this.x = x; // Initialize x-coordinate
    }

    public boolean isActive() {
        return active;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getY() {
        return y;
    }

    public void deactivate() {
        active = false;
        completed = true;
    }

    public void update() {
        if (active) {
            if (SpecialNoteScoring.isSpeedUpEffectActive()) {
                // Increase speed
                y += speed + 1;
            } else if (SpecialNoteScoring.isSlowDownEffectActive()) {
                // Decrease speed
                y += speed - 1;
            } else {
                y += speed;
            }

            // Iterate over a collection of enemies and check for collision
            for (Enemy enemy : ShadowDance.enemies) {
                if (checkCollision(enemy)) {
                    deactivate(); // Deactivate the Note if there's a collision
                    break; // No need to check other enemies
                }
            }
        }

        if (ShadowDance.getCurrFrame() >= appearanceFrame && !completed) {
            active = true;
        }
    }

    public boolean checkCollision(Enemy enemy) {
        // Check if the enemy is active
        if (!enemy.isActive()) {
            return false; // Don't consider collisions with inactive enemies
        }

        // Calculate the distance between the Note and the Enemy
        double distance = Math.sqrt(Math.pow(getX() - enemy.getX(), 2) + Math.pow(getY() - enemy.getY(), 2));

        // Check if the distance is less than or equal to 104 (or some other desired value)
        return distance <= 104;
    }


    public void draw(int x) {
        if (active) {
            image.draw(x, y);
        }
    }

    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        if (isActive()) {
            // Evaluate accuracy of the key press
            int score = accuracy.evaluateScore(getY(), targetHeight, input.wasPressed(relevantKey));

            if (score != Accuracy.NOT_SCORED) {
                deactivate();
                return score;
            }
        }

        return 0;
    }

    public double getX() {
        return x;
    }
}
