package GFBilliard;

import GFBilliard.Items.Ball;
import GFBilliard.Items.Board;
import GFBilliard.Items.Table;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

import java.util.List;

public class Game {
    private final Ball[] balls;
    private final Table table;
    private final Canvas canvas;
    private double[] canvasDim = {0.0, 0.0};

    public Game(Table table, Ball[] balls, Canvas canvas) {
        this.table = table;
        this.balls = balls;
        this.canvas = canvas;
        canvasDim = table.bounds;
        reset();
    }

    public void addDrawables(Group root) {
        ObservableList<Node> groupChildren = root.getChildren();
        table.addToGroup(groupChildren);
        for (Ball ball : balls) {
            ball.addToGroup(groupChildren);
        }
        // board.addToGroup(groupChildren);
        // board.registerMouseAction();
    }

    // tick() is called every frame, handle main game logic here
    public void tick() {
        // ball.setYVel(ball.getYVel() + GRAVITY);
        handleCollision();
        // ball.move();
    }

    private void handleCollision() {
        Bounds canvasBounds = canvas.getBoundsInLocal();
        // Bounds ballBounds = ball.getNode().getBoundsInLocal();
        // Bounds boardBounds = board.getNode().getBoundsInLocal();
        // if (ballBounds.intersects(boardBounds)) {
        //     ball.setYVel(-ball.getYVel());
        // }
        // if (ballBounds.getMinX() <= canvasBounds.getMinX() ||
        //         ballBounds.getMaxX() >= canvasBounds.getMaxX()) {
        //     ball.setXVel(-ball.getXVel());
        // }
        // if (ballBounds.getMinY() <= canvasBounds.getMinY() ||
        //         ballBounds.getMaxY() >= canvasBounds.getMaxY()) {
        //     ball.setYVel(-ball.getYVel());
        // }
        // if (ballBounds.getMaxY() >= canvasBounds.getMaxY()) {
        //     reset();
        // }
    }

    public void reset() {
        // ball.setXPos(100);
        // ball.setYPos(100);
        // ball.setXVel(100.0 / 60);
        // ball.setYVel(100.0 / 60);
    }
}

