import bagel.*;
import java.io.*;
import java.util.*;
import bagel.util.*;

/**
 * Full code for SWEN20003 Project 2, Semester 2, 2023
 *
 * This class represents the main game logic and execution.
 *
 * @author Eric Berg 1353093
 */
public class ShadowDance extends AbstractGame {
    // Constants for window dimensions and game title
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DANCE";

    // Background image and CSV file paths
    private final Image BACKGROUND_IMAGE = new Image("res/background.png");
    private String CSV_FILE = "res/level1.csv";

    // Font file path and fonts for the game
    public final static String FONT_FILE = "res/FSO8BITR.TTF";
    private final Font TITLE_FONT = new Font(FONT_FILE, 64);
    private final Font INSTRUCTION_FONT = new Font(FONT_FILE, 24);
    private final Font SCORE_FONT = new Font(FONT_FILE, 30);

    // Position and message constants for the title and instructions
    private final static int TITLE_X = 220;
    private final static int TITLE_Y = 250;
    private final static int INS_X_OFFSET = 100;
    private final static int INS_Y_OFFSET = 190;

    // Position for the score display
    private final static int SCORE_LOCATION = 35;

    // Instructions displayed in the main menu
    private static final String INSTRUCTIONS = "SELECT LEVELS WITH\nNUMBER KEYS";

    // Messages displayed on level completion
    private static final String CLEAR_MESSAGE = "CLEAR!";
    private static final String TRY_AGAIN_MESSAGE = "TRY AGAIN";

    // Accuracy object to track player's accuracy
    private final Accuracy accuracy = new Accuracy();

    // Array of game lanes
    public static final Lane[] lanes = new Lane[4];

    private int numLanes = 0;
    private int score = 0;

    // Frame counter
    private static int currFrame = 0;

    private boolean started = false;
    private boolean finished = false;
    private boolean paused = false;

    // Flag for returning to the main menu
    public static boolean returningToMainMenu = false;

    // Array to store special notes
    public static String[] Special = new String[20];
    public int currCount = 0;

    public static int doubleScoreCounter = 0;

    // Constants for enemy spawning
    private static final int ENEMY_SPAWN_INTERVAL = 600;
    private int enemySpawnFrame = ENEMY_SPAWN_INTERVAL;

    public static ArrayList<Enemy> enemies = new ArrayList<>();

    // Constants for random enemy spawning
    private static final int SPAWN_X_MIN = 100;
    private static final int SPAWN_X_MAX = 900;
    private static final int SPAWN_Y_MIN = 100;
    private static final int SPAWN_Y_MAX = 500;

    // Image for the guardian
    private final Image guardian = new Image("res/guardian.png");

    // List to store projectiles
    private List<Projectile> projectiles = new ArrayList<>();

    private boolean leftShiftDown = false;

    /**
     * Constructor for the ShadowDance class.
     */
    public ShadowDance() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
    }

    /**
     * Main entry point for the game.
     *
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        ShadowDance game = new ShadowDance();
        game.run();
    }

    /**
     * Read a CSV file to populate lanes and notes.
     *
     * @param csvFileName The path to the CSV file.
     */
    private void readCsv(String csvFileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
            // Clear existing lanes and notes
            numLanes = 0;

            String textRead;
            while ((textRead = br.readLine()) != null) {
                String[] splitText = textRead.split(",");

                if (splitText[0].equals("Lane")) {
                    String laneType = splitText[1];
                    int pos = Integer.parseInt(splitText[2]);

                    if (laneType.equals("Special")) {
                        SpecialLane specialLane = new SpecialLane(pos);
                        lanes[numLanes++] = specialLane;
                    } else {
                        Lane lane = new Lane(laneType, pos);
                        lanes[numLanes++] = lane;
                    }
                } else {
                    // Reading notes
                    String dir = splitText[0];
                    Lane lane = null;
                    for (int i = 0; i < numLanes; i++) {
                        if (lanes[i].getType().equals(dir)) {
                            lane = lanes[i];
                        }
                    }

                    if (lane != null) {
                        switch (splitText[1]) {
                            case "Normal":
                                Note note = new Note(dir, Integer.parseInt(splitText[2]), lane.location);
                                lane.addNote(note);
                                break;
                            case "Hold":
                                HoldNote holdNote = new HoldNote(dir, Integer.parseInt(splitText[2]));
                                lane.addHoldNote(holdNote);
                                break;
                            case "DoubleScore":
                                DoubleScoreNote doubleScoreNote = new DoubleScoreNote(dir, Integer.parseInt(splitText[2]));
                                lane.addSpecialNote(doubleScoreNote);
                                Special[currCount] = splitText[1];
                                currCount++;
                                break;
                            case "SpeedUp":
                                SpeedUpNote speedUpNote = new SpeedUpNote(dir, Integer.parseInt(splitText[2]));
                                lane.addSpecialNote(speedUpNote);
                                Special[currCount] = splitText[1];
                                currCount++;
                                break;
                            case "SlowDown":
                                SlowDownNote slowDownNote = new SlowDownNote(dir, Integer.parseInt(splitText[2]));
                                lane.addSpecialNote(slowDownNote);
                                Special[currCount] = splitText[1];
                                currCount++;
                                break;
                            case "Bomb":
                                // Check if it's a special lane or not
                                Bomb bomb = new Bomb(dir, Integer.parseInt(splitText[2]));
                                if (lane instanceof SpecialLane) {
                                    lane.addSpecialNote(bomb);
                                    Special[currCount] = splitText[1];
                                    currCount++;
                                } else {
                                    lane.addNote(bomb);
                                }
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * The main game update loop.
     *
     * @param input The input provided by the user.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        if (returningToMainMenu) {
            resetGame();
            returningToMainMenu = false;
        }

        BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

        if (!started) {
            // Main menu screen
            TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
            INSTRUCTION_FONT.drawString(INSTRUCTIONS, TITLE_X + INS_X_OFFSET, TITLE_Y + INS_Y_OFFSET);
            INSTRUCTION_FONT.drawString("1", TITLE_X + 150, TITLE_Y + INS_Y_OFFSET + 75);
            INSTRUCTION_FONT.drawString("2", TITLE_X + 250, TITLE_Y + INS_Y_OFFSET + 75);
            INSTRUCTION_FONT.drawString("3", TITLE_X + 350, TITLE_Y + INS_Y_OFFSET + 75);

            if (input.wasPressed(Keys.NUM_1)) {
                started = true;
                CSV_FILE = "res/level1.csv";
                readCsv(CSV_FILE);
            } else if (input.wasPressed(Keys.NUM_2)) {
                started = true;
                CSV_FILE = "res/level2.csv"; // Change this to the path of your second level CSV file
                readCsv(CSV_FILE);
            } else if (input.wasPressed(Keys.NUM_3)) {
                started = true;
                CSV_FILE = "res/level3.csv"; // Change this to the path of your third level CSV file
                readCsv(CSV_FILE);
            }
        } else if (finished) {
            // Level completion screen
            String endMessage;
            if (score >= getTargetScore(CSV_FILE)) {
                endMessage = CLEAR_MESSAGE;
            } else {
                endMessage = TRY_AGAIN_MESSAGE;
            }

            int endMessageX = (int) (Window.getWidth() / 2.0 - TITLE_FONT.getWidth(endMessage) / 2);
            TITLE_FONT.drawString(endMessage, endMessageX, 300);

            String instructionMessage = "PRESS SPACE TO RETURN TO LEVEL SELECTION";
            int instructionX = (int) (Window.getWidth() / 2.0 - INSTRUCTION_FONT.getWidth(instructionMessage) / 2);
            INSTRUCTION_FONT.drawString(instructionMessage, instructionX, 500);

            if (input.wasPressed(Keys.SPACE)) {
                finished = false;
                started = false;
                returningToMainMenu = true;
            }
        } else {
            // In-game screen
            SCORE_FONT.drawString("Score " + score, SCORE_LOCATION, SCORE_LOCATION);

            if (paused) {
                if (input.wasPressed(Keys.TAB)) {
                    paused = false;
                }

                for (int i = 0; i < numLanes; i++) {
                    lanes[i].draw();
                }
            } else {
                currFrame++;
                for (int i = 0; i < numLanes; i++) {
                    int laneScore = lanes[i].update(input, accuracy);
                    if (SpecialNoteScoring.doubleScoring && doubleScoreCounter < 480) {
                        score += 2 * laneScore;
                    } else {
                        score += laneScore;
                    }
                }

                if (SpecialNoteScoring.doubleScoring && doubleScoreCounter < 480) {
                    doubleScoreCounter++;
                }

                if (doubleScoreCounter == 480) {
                    doubleScoreCounter = 0;
                    SpecialNoteScoring.doubleScoring = false;
                }

                BombScoring.update();
                SpecialNoteScoring.update();
                accuracy.update();
                finished = checkFinished();
                if (input.wasPressed(Keys.TAB)) {
                    paused = true;
                }

                if (CSV_FILE.equals("res/level3.csv")) {
                    // Render the guardian at (800, 600)
                    guardian.draw(800, 600);
                    enemySpawnFrame--;

                    if (enemySpawnFrame <= 0) {
                        spawnEnemy();
                        enemySpawnFrame = ENEMY_SPAWN_INTERVAL;
                    }

                    for (Enemy enemy : enemies) {
                        enemy.update();
                    }
                }

                // Check if there are active enemies
                boolean hasActiveEnemies = false;
                for (Enemy enemy : enemies) {
                    if (enemy.isActive()) {
                        hasActiveEnemies = true;
                        break;
                    }
                }

                // Check if left shift is pressed and fire projectiles if there are active enemies
                if (input.isDown(Keys.LEFT_SHIFT) && !leftShiftDown && hasActiveEnemies) {
                    double angleToNearestEnemy = findNearestEnemy();
                    projectiles.add(new Projectile(800, 600, angleToNearestEnemy));
                }

                // Update the flag for left shift key state
                leftShiftDown = input.isDown(Keys.LEFT_SHIFT);

                // Update the projectiles
                Iterator<Projectile> iterator = projectiles.iterator();
                while (iterator.hasNext()) {
                    Projectile projectile = iterator.next();
                    projectile.update();
                    projectile.draw();

                    // Check for collisions with enemies
                    for (Enemy enemy : enemies) {
                        if (projectile.checkCollision(enemy)) {
                            iterator.remove(); // Remove the projectile
                            enemy.deactivate(); // Deactivate the enemy
                        }
                    }

                    // Remove projectiles that are out of the screen
                    if (projectile.isOutOfScreen()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    // Define a method to get the target score based on the CSV file
    private int getTargetScore(String csvFileName) {
        if (csvFileName.equals("res/level1.csv")) {
            return 150;
        } else if (csvFileName.equals("res/level2.csv")) {
            return 400;
        } else if (csvFileName.equals("res/level3.csv")) {
            return 350;
        } else {
            return 0; // Default target score if the CSV file is not recognized
        }
    }

    /**
     * Get the current frame count.
     *
     * @return The current frame count.
     */
    public static int getCurrFrame() {
        return currFrame;
    }

    /**
     * Check if the game is finished.
     *
     * @return True if the game is finished, false otherwise.
     */
    private boolean checkFinished() {
        for (int i = 0; i < numLanes; i++) {
            if (!lanes[i].isFinished()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reset the game to its initial state.
     */
    private void resetGame() {
        // Reset game-related variables
        Accuracy.frameCount = 30;
        score = 0;
        currFrame = 0;
        started = false;
        finished = false;
        paused = false;
        SpecialNoteScoring.doubleScoring = false;
        SpecialNoteScoring.speedUpEffectActive = false;
        doubleScoreCounter = 0;
        enemySpawnFrame = 600;

        // Clear lanes and stop reading CSV file
        numLanes = 0;
        for (int i = 0; i < lanes.length; i++) {
            lanes[i] = null;
        }
        CSV_FILE = ""; // Empty CSV file path

        // Clear the enemy list
        enemies.clear();
    }

    /**
     * Spawn an enemy at a random position.
     */
    private void spawnEnemy() {
        Random rand = new Random();
        double x = rand.nextInt(SPAWN_X_MAX - SPAWN_X_MIN + 1) + SPAWN_X_MIN;
        double y = rand.nextInt(SPAWN_Y_MAX - SPAWN_Y_MIN + 1) + SPAWN_Y_MIN;
        Point enemyPosition = new Point(x, y);
        Enemy enemy = new Enemy(enemyPosition);
        enemies.add(enemy);
    }

    /**
     * Find the angle to the nearest enemy from the guardian.
     *
     * @return The angle to the nearest enemy.
     */
    public static double findNearestEnemy() {
        double minDistance = Double.MAX_VALUE;
        double nearestEnemyAngle = 0.0;

        for (Enemy enemy : enemies) {
            // Check if the enemy is active
            if (!enemy.isActive()) {
                continue; // Skip inactive enemies
            }

            double enemyX = enemy.getX();
            double enemyY = enemy.getY();
            double distance = Math.sqrt(Math.pow(enemyX - 800, 2) + Math.pow(enemyY - 600, 2));

            if (distance < minDistance) {
                minDistance = distance;
                double deltaX = enemyX - 800;
                double deltaY = enemyY - 600;
                nearestEnemyAngle = Math.atan2(deltaY, deltaX);
            }
        }

        return nearestEnemyAngle;
    }
}
