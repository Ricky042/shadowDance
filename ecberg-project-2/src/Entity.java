import bagel.Input;
import bagel.Keys;
import bagel.util.Point;

public abstract class Entity {
    protected boolean active;
    protected boolean completed;

    public boolean isActive() {
        return active;
    }

    public abstract void update();

    public abstract int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey);

    public abstract boolean isCompleted();
    public abstract void draw(int x);

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
        completed = true;
    }
}

