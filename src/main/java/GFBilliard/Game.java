package GFBilliard;

import GFBilliard.Items.Ball;
import GFBilliard.Items.Board;
import GFBilliard.Items.Table;
import GFBilliard.Items.Hole;
import GFBilliard.Items.FallIntoHole.StrategyResult;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font; 
import javafx.scene.text.FontPosture; 
import javafx.scene.text.FontWeight; 
import javafx.scene.text.Text;
import javafx.util.Pair;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Game {
    private final double GRAVITY = 10.0 / 60.0;
    private final ArrayList<Ball> balls;
    private final Table table;
    private final Canvas canvas;
    private double[] canvasDim = {0.0, 0.0};
    private Line line;
    private Text text;
    private Ball whiteBall;
    private Hole[] holes;
    private Group root;

    private boolean aming = false;

    private GameState state;

    private enum GameState {
        initializing, running, over
    }

    public Game(Table table, Ball[] balls, Canvas canvas) {
        state = GameState.initializing;

        this.table = table;
        
        this.balls = new ArrayList<Ball>(Arrays.asList(balls));

        this.canvas = canvas;
        canvasDim = table.bounds;
        line = new Line();

        text = new Text(table.bounds[0]/2 - 130, table.bounds[1]/2, "running");
        text.setFill(Color.valueOf("yellow"));
        text.setStrokeWidth(2.0);
        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 50));
        text.setVisible(false);

        line.setVisible(false);
        for (Ball ball : this.balls) {
            if (ball.life == 0) {
                whiteBall = ball;
                break;
            }
        }
        whiteBall.getNode().setOnMousePressed(e -> {
            Point2D whiteBallVelocity = new Point2D(whiteBall.getXVel(), whiteBall.getYVel());
            if (whiteBallVelocity.magnitude() == 0) {
                aming = true;
                line.setStartX(whiteBall.getXPos());
                line.setStartY(whiteBall.getYPos());
                line.setEndX(e.getSceneX());
                line.setEndY(e.getSceneY());
                line.setVisible(true);
            }
        });
        whiteBall.getNode().setOnMouseDragged(e -> {
            line.setStartX(whiteBall.getXPos());
            line.setStartY(whiteBall.getYPos());
            line.setEndX(e.getSceneX());
            line.setEndY(e.getSceneY());
        });
        whiteBall.getNode().setOnMouseReleased(e -> {
            line.setVisible(false);
            if (aming) {
                Point2D startPos = new Point2D(e.getSceneX(), e.getSceneY());
                Point2D endPos = new Point2D(whiteBall.getXPos(), whiteBall.getYPos());
                Point2D velocity = endPos.subtract(startPos).multiply(0.1);
                whiteBall.setXVel(velocity.getX());
                whiteBall.setYVel(velocity.getY());
            }
            aming = false;
        });

        holes = new Hole[6];
        double holeOffset = 8.0;
        holes[0] = new Hole(holeOffset, holeOffset);
        holes[1] = new Hole(table.bounds[0]/2.0, holeOffset);
        holes[2] = new Hole(table.bounds[0]-holeOffset, holeOffset);
        holes[3] = new Hole(holeOffset, table.bounds[1]-holeOffset);
        holes[4] = new Hole(table.bounds[0]/2.0, table.bounds[1]-holeOffset);
        holes[5] = new Hole(table.bounds[0]-holeOffset, table.bounds[1]-holeOffset);

        state = GameState.running;
    }

    public void addDrawables(Group root) {
        state = GameState.initializing;

        this.root = root;
        ObservableList<Node> groupChildren = root.getChildren();
        table.addToGroup(groupChildren);
        for (Hole hole : holes) {
            hole.addToGroup(groupChildren);
        }
        for (Ball ball : balls) {
            ball.addToGroup(groupChildren);
        }
        groupChildren.add(line);
        groupChildren.add(text);

        state = GameState.running;
    }

    // tick() is called every frame, handle main game logic here
    public void tick() {
        if (state == GameState.running) {
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
        }
    }

    private void handleCollision() {
        Bounds canvasBounds = canvas.getBoundsInLocal();
        ArrayList<Ball> ballsToRemove = new ArrayList<>();
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

            for (Hole hole : holes) {
                Bounds holeBounds = hole.getNode().getBoundsInLocal();
                if (holeBounds.intersects(ballBounds)) {
                    ObservableList<Node> groupChildren = root.getChildren();
                    StrategyResult fallRes = ball.fallIntoHole();
                    switch (fallRes) {
                        case goal:
                        System.out.println("goal");
                        // todo delete ball
                        ball.removeFromGroup(groupChildren);
                        ballsToRemove.add(ball);
                        break;
                        case gameOver:
                        text.setText("YOU LOSE...");
                        text.setVisible(true);
                        ball.removeFromGroup(groupChildren);
                        ballsToRemove.add(ball);
                        state = GameState.over;
                        break;
                    }
                }
            }
        }
        for (Ball ball : ballsToRemove) {
            balls.remove(ball);
        }
        if (balls.size() == 1 && balls.get(0) == whiteBall) {
            text.setText("YOU WIN!!!");
            text.setVisible(true);
            state = GameState.over;
        }
    }
}

