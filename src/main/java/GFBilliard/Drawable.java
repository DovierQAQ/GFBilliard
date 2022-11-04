package GFBilliard;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface Drawable {
    Node getNode();

    // Implementations show recursively add their child drawables to group
    void addToGroup(ObservableList<Node> group);

    void removeFromGroup(ObservableList<Node> group);
}
