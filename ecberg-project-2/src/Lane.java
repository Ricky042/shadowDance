import bagel.*;

/**
 * Class for the lanes which notes fall down
 */
public class Lane {
    private static final int HEIGHT = 384;
    private static final int TARGET_HEIGHT = 657;
    private final String type;
    private final Image image;
    private final Entity[] notes = new Entity[100];
    private int numNotes = 0;
    private final HoldNote[] holdNotes = new HoldNote[20]; // Changed the size to 20
    private int numHoldNotes = 0;
    private final Entity[] specialNotes = new Entity[20];
    private int numSpecialNotes = 0;
    private Keys relevantKey;
    public final int location;
    private int currNote = 0;
    private int currHoldNote = 0;
    private int currSpecialNote = 0;
    private int score = 0;

    public Lane(String dir, int location) {
        this.type = dir;
        this.location = location;
        image = new Image("res/lane" + dir + ".png");
        switch (dir) {
            case "Left":
                relevantKey = Keys.LEFT;
                break;
            case "Right":
                relevantKey = Keys.RIGHT;
                break;
            case "Up":
                relevantKey = Keys.UP;
                break;
            case "Down":
                relevantKey = Keys.DOWN;
                break;
            case "Special":
                relevantKey = Keys.SPACE;
                break;
        }
    }

    public String getType() {
        return type;
    }

    public void addNote(Entity note) {
        notes[numNotes++] = note;
    }

    public void addHoldNote(HoldNote holdNote) {
        if (numHoldNotes < holdNotes.length) {
            holdNotes[numHoldNotes++] = holdNote;
        } else {
            System.err.println("Cannot add more hold notes, limit reached.");
        }
    }

    public void addSpecialNote(Entity specialNote) {
        if (numSpecialNotes < specialNotes.length) {
            specialNotes[numSpecialNotes++] = specialNote;
        } else {
            System.err.println("Cannot add more special notes, limit reached.");
        }
    }

    public int update(Input input, Accuracy accuracy) {
        draw();

        for (int i = currNote; i < numNotes; i++) {
            notes[i].update();
        }

        for (int j = currHoldNote; j < numHoldNotes; j++) {
            holdNotes[j].update();
        }

        for (int k = currSpecialNote; k < numSpecialNotes; k++) {
            specialNotes[k].update();
        }

        int laneScore = Accuracy.NOT_SCORED; // Initialize lane score

        // Update regular notes
        if (currNote < numNotes) {
            Entity currentNote = notes[currNote];
            if (currentNote instanceof Bomb) {
                Bomb bomb = (Bomb) currentNote;
                laneScore = bomb.checkScore(input, accuracy, TARGET_HEIGHT, relevantKey);
            } else {
                laneScore = currentNote.checkScore(input, accuracy, TARGET_HEIGHT, relevantKey);
            }

            if (currentNote.isCompleted() && currentNote instanceof Bomb) {
                if (BombScoring.kaboom) {
                    clearNotes();
                    BombScoring.kaboom = false;
                }
                currNote++;
                return laneScore;
            }

            if (currentNote.isCompleted()) {
                currNote++;
                return laneScore;
            }
        }

        // Update hold notes
        if (currHoldNote < numHoldNotes) {
            HoldNote currentHoldNote = holdNotes[currHoldNote];
            laneScore = currentHoldNote.checkScore(input, accuracy, TARGET_HEIGHT, relevantKey);

            if (currentHoldNote.isCompleted()) {
                currHoldNote++;
                return laneScore;
            }
        }

        // Update special notes
        if (currSpecialNote < numSpecialNotes) {
            Entity currentSpecialNote = specialNotes[currSpecialNote];
            if (currentSpecialNote instanceof Bomb) {
                Bomb bomb = (Bomb) currentSpecialNote;
                laneScore = bomb.checkScore(input, accuracy, TARGET_HEIGHT, relevantKey);
            } else {
                laneScore = currentSpecialNote.checkScore(input, accuracy, TARGET_HEIGHT, relevantKey);
            }

            if (currentSpecialNote.isCompleted() && currentSpecialNote instanceof Bomb) {
                if (BombScoring.kaboom) {
                    clearNotes();
                    BombScoring.kaboom = false;
                }
                currSpecialNote++;
                return laneScore;
            }

            if (currentSpecialNote.isCompleted()) {
                currSpecialNote++;
                return laneScore;
            }

        }

        return laneScore;
    }

    public boolean isFinished() {
        for (int i = 0; i < numNotes; i++) {
            if (!notes[i].isCompleted()) {
                return false;
            }
        }

        for (int j = 0; j < numHoldNotes; j++) {
            if (!holdNotes[j].isCompleted()) {
                return false;
            }
        }

        for (int k = 0; k < numSpecialNotes; k++) {
            if (!specialNotes[k].isCompleted()) {
                return false;
            }
        }

        return true;
    }


    public void draw() {
        image.draw(location, HEIGHT);

        for (int i = currNote; i < numNotes; i++) {
            notes[i].draw(location);
        }

        for (int j = currHoldNote; j < numHoldNotes; j++) {
            if (holdNotes[j] != null) {
                holdNotes[j].draw(location);
            }
        }

        for (int k = currSpecialNote; k < numSpecialNotes; k++) {
            specialNotes[k].draw(location);
        }
    }

    public void clearNotes() {
        // Clear regular notes if they exist
        for (int i = 0; i < numNotes; i++) {
            if (notes[i] != null && notes[i].isActive()) {
                notes[i].deactivate();
            }
        }

        // Clear hold notes if they exist
        for (int j = 0; j < numHoldNotes; j++) {
            if (holdNotes[j] != null && holdNotes[j].isActive()) {
                holdNotes[j].deactivate();
            }
        }

        // Clear special notes if they exist
        for (int k = 0; k < numSpecialNotes; k++) {
            if (specialNotes[k] != null && specialNotes[k].isActive()) {
                specialNotes[k].deactivate();
            }
        }
    }

    public Entity[] getNotes() {
        return notes;
    }

    public int getNumNotes() {
        return numNotes;
    }

    public int getLocation() {
        return location;
    }


}
