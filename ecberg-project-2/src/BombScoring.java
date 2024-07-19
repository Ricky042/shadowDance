import bagel.*;

public class BombScoring {

    private static final int ACTIVATION_DISTANCE = 50;
    private static final int MESSAGE_FRAMES = 30;
    private static final Font ACCURACY_FONT = new Font(ShadowDance.FONT_FILE, 40);
    private static int frameCount = 0;
    public static String displayMessage = "";
    public static int score = 0;
    public static boolean kaboom = false;


    public static int evaluateScore(int noteY, int targetHeight, boolean toggled, Keys relevantKey) {
        if (toggled) {
            if (Math.abs(noteY - targetHeight) <= ACTIVATION_DISTANCE) {
                if (relevantKey == Keys.SPACE) {
                    SpecialNoteScoring.SpecialNoteCount++;
                }
                kaboom = true;
                score = 0;
                displayMessage = "Lane Clear";
                frameCount = 0;
                return score;
            }
        }

        // Check if the Bomb goes out of bounds (off the screen)
        if (noteY >= Window.getHeight()) {
            // Deactivate the Bomb
            return 100;
        }

        return -100;
    }

    public static void update() {
        frameCount++;
        if (displayMessage != null && frameCount < MESSAGE_FRAMES) {
            ACCURACY_FONT.drawString(displayMessage,
                    Window.getWidth() / 2 - ACCURACY_FONT.getWidth(displayMessage) / 2,
                    Window.getHeight() / 2);
        }
    }
}
