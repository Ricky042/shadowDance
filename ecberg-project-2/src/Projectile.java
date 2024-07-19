import bagel.*;
import bagel.util.*;

public class Projectile {
    private static final Image IMAGE = new Image("res/arrow.png");
    private static final double SPEED = 6.0;
    private double x, y;
    private double velocityX, velocityY;
    private double angle;


    public Projectile(double startX, double startY, double angle) {
        x = startX;
        y = startY;
        velocityX = SPEED * Math.cos(angle);
        velocityY = SPEED * Math.sin(angle);
        this.angle = angle;
    }

    public void update() {
        x += velocityX;
        y += velocityY;
    }

    public void draw() {
        // Calculate the angle in degrees for rotation (convert radians to degrees)
        double angleDegrees = Math.toDegrees(angle);

        // Draw the image with rotation
        IMAGE.draw(x, y, new DrawOptions().setRotation(angle));
    }



    public boolean checkCollision(Enemy enemy) {
        // Check if the enemy is active before calculating the distance
        if (!enemy.isActive()) {
            return false; // Skip collision check with inactive enemies
        }

        // Calculate the distance between the centre-coordinates of the enemy and the projectile
        double distance = Math.sqrt(Math.pow(x - enemy.getX(), 2) + Math.pow(y - enemy.getY(), 2));
        return distance <= 62; // Considered as a collision if distance is less than or equal to 62
    }


    public boolean isOutOfScreen() {
        return x < 0 || x > Window.getWidth() || y < 0 || y > Window.getHeight();
    }
}

