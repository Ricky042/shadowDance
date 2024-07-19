import bagel.*;
import java.util.*;
import java.io.*;

public class Bomb extends Entity {
    private Image image;
    public final int appearanceFrame;
    private final int speed = 2;
    private int y = 100;
    private boolean active = false;
    private boolean completed = false;
    public static String noteType;
    public Bomb(String dir, int appearanceFrame) {
        super();
        noteType = "noteBomb";
        image = new Image("res/noteBomb.png");
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
            // Evaluate accuracy of the key press
            int score = BombScoring.evaluateScore(getY(), targetHeight, input.wasPressed(relevantKey), relevantKey);

            // Check if the score is 100, which indicates a Bomb dropping off-screen
            if (score == 100) {
                deactivate();
                return 0;
            }

            // Check if the score is not -100 (deactivation of a Bomb)
            if (score != -100) {
                deactivate();
                return score;
            }
        }

        return 0;
    }


}




