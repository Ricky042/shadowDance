import bagel.*;

/**
 * Class for special notes (SpeedUp, SlowDown, DoubleScore)
 */

public class SpecialNote extends Entity {
    private Image image;
    private final int appearanceFrame;
    private final int speed = 2;
    private int y = 100;
    private boolean active = false;
    private boolean completed = false;
    public static String noteType;

    public SpecialNote(String dir, String type, int appearanceFrame) {
        super();
        noteType = type;
        image = new Image("res/" + type + ".png");
        this.appearanceFrame = appearanceFrame;
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
        }

        if (ShadowDance.getCurrFrame() >= appearanceFrame && !completed) {
            active = true;
        }
    }

    public void draw(int x) {
        if (active) {
            image.draw(x, y);
        }
    }

    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        if (isActive()) {
            // evaluate accuracy of the key press
            int score = SpecialNoteScoring.evaluateScore(getY(), targetHeight, input.wasPressed(relevantKey));

            if (score == 100) {
                deactivate();
                return 0;
            }

            if (score != -100) {
                deactivate();
                return score;
            }
        }

        return 0;
    }
}

