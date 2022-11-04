package GFBilliard.Items;

import GFBilliard.Drawable;
import GFBilliard.Movable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Table implements Drawable, ConfigReader.ConfigItem {
    private Rectangle shape;

    public double[] bounds = {0.0, 0.0};

    public double friction = 1.0;

    public Table(double canvasDimX, double canvasDimY, String color, double friction) {
        this.shape = new Rectangle(0, 0, canvasDimX, canvasDimY);
        this.shape.setFill(Color.valueOf(color));
        this.friction = friction;
        bounds[0] = canvasDimX;
        bounds[1] = canvasDimY;
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
