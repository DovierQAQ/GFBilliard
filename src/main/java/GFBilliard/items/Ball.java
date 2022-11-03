package GFBilliard.Items;

import GFBilliard.Drawable;
import GFBilliard.Movable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball implements Movable, Drawable, ConfigReader.ConfigItem {
    private static final double RASIUS = 15.0;
    private final Circle shape;

    private final double[] velocity = {0.0, 0.0};

    public Ball(double xPos, double yPos) {
        this.shape = new Circle(xPos, yPos, RASIUS);
        this.shape.setFill(Color.valueOf("blue"));
    }

    @Override
    public Node getNode() {
        return this.shape;
    }

    @Override
    public void addToGroup(ObservableList<Node> group) {
        group.add(this.shape);
    }

    @Override
    public double getXPos() {
        return this.shape.getCenterX();
    }

    @Override
    public double getYPos() {
        return this.shape.getCenterY();
    }

    @Override
    public double getXVel() {
        return this.velocity[0];
    }

    @Override
    public double getYVel() {
        return this.velocity[1];
    }

    @Override
    public void setXPos(double xPos) {
        this.shape.setCenterX(xPos);
    }

    @Override
    public void setYPos(double yPos) {
        this.shape.setCenterY(yPos);
    }

    @Override
    public void setXVel(double xVel) {
        this.velocity[0] = xVel;
    }

    @Override
    public void setYVel(double yVel) {
        this.velocity[1] = yVel;
    }

    @Override
    public void move() {
        double xPos = getXPos() + getXVel();
        double yPos = getYPos() + getYVel();
        setXPos(xPos);
        setYPos(yPos);
    }
}
