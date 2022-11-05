package GFBilliard.Items;

import GFBilliard.Drawable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Hole implements Drawable {
    private final double RASIUS = 20.0;
    private Circle shape;

    public Hole(double posX, double posY) {
        this.shape = new Circle(posX, posY, RASIUS);
        this.shape.setFill(Color.valueOf("black"));
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
    public void removeFromGroup(ObservableList<Node> group) {
        group.remove(this.shape);
    }
}
