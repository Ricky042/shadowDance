import bagel.*;

/**
 * Class for hold notes
 */
public class HoldNote extends Entity{
    private static final int HEIGHT_OFFSET = 82;
    private final Image image;
    private final int appearanceFrame;
    private final int speed = 2;
    private int y = 24;
    private boolean active = false;
    private boolean holdStarted = false;
    private boolean completed = false;

    public HoldNote(String dir, int appearanceFrame) {
        image = new Image("res/holdNote" + dir + ".png");
        this.appearanceFrame = appearanceFrame;
    }

    public boolean isActive() {
        return active;
    }
    public boolean isCompleted() {
        return completed;
    }

    public void deactivate() {
        active = false;
        completed = true;
    }

    public void startHold() {
        holdStarted = true;
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

    /**
     * scored twice, once at the start of the hold and once at the end
     */

    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        if (isActive() && !holdStarted) {
            int score = accuracy.evaluateScore(getBottomHeight(), targetHeight, input.wasPressed(relevantKey));

            if (score == Accuracy.MISS_SCORE) {
                deactivate();
                return score;
            } else if (score != Accuracy.NOT_SCORED) {
                startHold();
                return score;
            }
        } else if (isActive() && holdStarted) {

            int score = accuracy.evaluateScore(getTopHeight(), targetHeight, input.wasReleased(relevantKey));

            if (score != Accuracy.NOT_SCORED) {
                deactivate();
                return score;
            } else if (input.wasReleased(relevantKey)) {
                deactivate();
                accuracy.setAccuracy(Accuracy.MISS);
                return Accuracy.MISS_SCORE;
            }
        }

        return 0;
    }

    /**
     * gets the location of the start of the note
     */
    private int getBottomHeight() {
        return y + HEIGHT_OFFSET;
    }

    /**
     * gets the location of the end of the note
     */
    private int getTopHeight() {
        return y - HEIGHT_OFFSET;
    }
}
