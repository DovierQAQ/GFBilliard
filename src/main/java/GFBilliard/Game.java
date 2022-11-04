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
import javafx.util.Pair;
import javafx.geometry.Point2D;

import java.util.List;

public class Game {
    private final double GRAVITY = 10.0 / 60.0;
    private final Ball[] balls;
    private final Table table;
    private final Canvas canvas;
    private double[] canvasDim = {0.0, 0.0};

    public Game(Table table, Ball[] balls, Canvas canvas) {
        this.table = table;
        this.balls = balls;
        this.canvas = canvas;
        canvasDim = table.bounds;
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
        for (Ball ball : balls) {
            Point2D velocity = new Point2D(ball.getXVel(), ball.getYVel());
            Point2D velocityDirection = velocity.normalize();
            double frictionAcc = table.friction * GRAVITY;
            if (velocity.magnitude() < frictionAcc) {
                ball.setXVel(0.0);
                ball.setYVel(0.0);
            } else {
                velocity = velocity.subtract(velocityDirection.multiply(frictionAcc));
                ball.setXVel(velocity.getX());
                ball.setYVel(velocity.getY());
            }
            ball.move();
        }
        // ball.move();
    }

    private void handleCollision() {
        Bounds canvasBounds = canvas.getBoundsInLocal();
        for (Ball ball : balls) {
            Bounds ballBounds = ball.getNode().getBoundsInLocal();

            for (Ball collisionBall : balls) {
                if (collisionBall != ball) {
                    Bounds collisionBallBounds = collisionBall.getNode().getBoundsInLocal();
                    if (collisionBallBounds.intersects(ballBounds)) {
                        Point2D posA = new Point2D(collisionBall.getXPos(), collisionBall.getYPos());
                        Point2D velocityA = new Point2D(collisionBall.getXVel(), collisionBall.getYVel());
                        Point2D posB = new Point2D(ball.getXPos(), ball.getYPos());
                        Point2D velocityB = new Point2D(ball.getXVel(), ball.getYVel());
                        Pair<Point2D, Point2D> collisionResult = Physics.calculateCollision(posA, velocityA, collisionBall.mass, posB, velocityB, ball.mass);
                        collisionBall.setXVel(collisionResult.getKey().getX());
                        collisionBall.setYVel(collisionResult.getKey().getY());
                        ball.setXVel(collisionResult.getValue().getX());
                        ball.setYVel(collisionResult.getValue().getY());
                    }
                }
            }

            if (ballBounds.getMinX() <= canvasBounds.getMinX() ||
                    ballBounds.getMaxX() >= canvasBounds.getMaxX()) {
                ball.setXVel(-ball.getXVel());
            }
            if (ballBounds.getMinY() <= canvasBounds.getMinY() ||
                    ballBounds.getMaxY() >= canvasBounds.getMaxY()) {
                ball.setYVel(-ball.getYVel());
            }
        }
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
}

