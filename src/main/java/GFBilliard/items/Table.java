package GFBilliard.Items;

import GFBilliard.Drawable;
import GFBilliard.Movable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Table implements Drawable, ConfigReader.ConfigItem {
    private final Rectangle shape;

    public final double[] bounds = {0.0, 0.0};

    public Table(double canvasDimX, double canvasDimY, String color) {
        this.shape = new Rectangle(0, 0, canvasDimX, canvasDimY);
        this.shape.setFill(Color.valueOf(color));
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
}
