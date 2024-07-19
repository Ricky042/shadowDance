import bagel.*;
import java.util.HashMap;
import java.util.Map;

public class SpecialNoteScoring {

    private static final int ACTIVATION_DISTANCE = 50;
    private static final int MESSAGE_FRAMES = 30;
    private static final Font ACCURACY_FONT = new Font(ShadowDance.FONT_FILE, 40);
    private static int frameCount = 0;
    public static String displayMessage = "";
    public static int SpecialNoteCount = 0;
    public static boolean speedUpEffectActive = false;
    public static boolean slowDownEffectActive = false;
    public static int score = 0;
    public static boolean doubleScoring = false;

    public static int evaluateScore(int noteY, int targetHeight, boolean toggled) {
        if (toggled) {
            if (Math.abs(noteY - targetHeight) <= ACTIVATION_DISTANCE) {
                if (ShadowDance.Special[SpecialNoteCount].equals("SpeedUp")) {
                    displayMessage = "Speed Up";
                    speedUpEffectActive = true;
                    score = 15;
                } else if (ShadowDance.Special[SpecialNoteCount].equals("SlowDown")) {
                    displayMessage = "Slow Down";
                    speedUpEffectActive = false;
                    score = 15;
                } else if (ShadowDance.Special[SpecialNoteCount].equals("DoubleScore")) {
                    displayMessage = "Double Score";
                    doubleScoring = true;
                    ShadowDance.doubleScoreCounter = 0;
                    score = 0;
                }
                frameCount = 0;
                SpecialNoteCount++;
                return score;
            }
        }
        if (noteY >= Window.getHeight()) {
            // Deactivate the Special Note
            SpecialNoteCount++;
            return 100;
        }
        return -100;
    }

    public static void update() {
        frameCount++;
        if (displayMessage != null && frameCount < MESSAGE_FRAMES) {
            ACCURACY_FONT.drawString(displayMessage,
                    Window.getWidth()/2 - ACCURACY_FONT.getWidth(displayMessage)/2,
                    Window.getHeight()/2);
        }
    }

    public static boolean isSpeedUpEffectActive() {
        return speedUpEffectActive;
    }

    public static boolean isSlowDownEffectActive() {
        return slowDownEffectActive;
    }
}
