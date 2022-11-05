package GFBilliard;

import GFBilliard.Items.Ball;
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
        // 把游戏置为初始化状态
        state = GameState.initializing;

        this.table = table;
        
        this.balls = new ArrayList<Ball>(Arrays.asList(balls));

        this.canvas = canvas;
        canvasDim = table.bounds;
        
        // 创建游戏结束文本
        text = new Text(table.bounds[0]/2 - 130, table.bounds[1]/2, "running");
        text.setFill(Color.valueOf("yellow"));
        text.setStrokeWidth(2.0);
        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 50));
        text.setVisible(false);

        // 创建瞄准线条
        line = new Line();
        line.setVisible(false);

        for (Ball ball : this.balls) {
            // 白球生命值为0
            if (ball.life == 0) {
                whiteBall = ball;
                break;
            }
        }

        // 注册白球的鼠标控制
        whiteBall.getNode().setOnMousePressed(e -> {
            Point2D whiteBallVelocity = new Point2D(whiteBall.getXVel(), whiteBall.getYVel());
            // 只有白球静止的时候才能操作
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
            // 只有在瞄准状态下才给白球施加力的作用
            if (aming) {
                Point2D startPos = new Point2D(e.getSceneX(), e.getSceneY());
                Point2D endPos = new Point2D(whiteBall.getXPos(), whiteBall.getYPos());
                Point2D velocity = endPos.subtract(startPos).multiply(0.1);
                whiteBall.setXVel(velocity.getX());
                whiteBall.setYVel(velocity.getY());
            }
            aming = false;
        });

        // 创建六个洞口
        holes = new Hole[6];
        double holeOffset = 8.0;
        holes[0] = new Hole(holeOffset, holeOffset);
        holes[1] = new Hole(table.bounds[0]/2.0, holeOffset);
        holes[2] = new Hole(table.bounds[0]-holeOffset, holeOffset);
        holes[3] = new Hole(holeOffset, table.bounds[1]-holeOffset);
        holes[4] = new Hole(table.bounds[0]/2.0, table.bounds[1]-holeOffset);
        holes[5] = new Hole(table.bounds[0]-holeOffset, table.bounds[1]-holeOffset);

        // 把游戏置为运行状态
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
        // 只有在运行状态才计算帧动画和物理
        if (state == GameState.running) {
            handleCollision();
            for (Ball ball : balls) {
                // 使用摩擦力将速度递减
                // ma=umg，a=dv/dt
                Point2D velocity = new Point2D(ball.getXVel(), ball.getYVel());
                Point2D velocityDirection = velocity.normalize();
                double frictionAcc = table.friction * GRAVITY;
                if (velocity.magnitude() < frictionAcc) {
                    // 如果速度低于一定值直接置0，防止反复震荡
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
        // 创建一个列表，用来临时存放需要删除的球
        ArrayList<Ball> ballsToRemove = new ArrayList<>();
        // 遍历所有球，求其碰撞
        for (Ball ball : balls) {
            Bounds ballBounds = ball.getNode().getBoundsInLocal();

            // 每两个球均需检测一遍碰撞
            for (Ball collisionBall : balls) {
                // 不和自己求
                if (collisionBall != ball) {
                    Bounds collisionBallBounds = collisionBall.getNode().getBoundsInLocal();
                    if (collisionBallBounds.intersects(ballBounds)) {
                        // 如果包围盒有碰撞，则调用碰撞处理函数，改变两球的速度值
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

            // 碰到边界反弹
            if (ballBounds.getMinX() <= canvasBounds.getMinX() ||
                    ballBounds.getMaxX() >= canvasBounds.getMaxX()) {
                ball.setXVel(-ball.getXVel());
            }
            if (ballBounds.getMinY() <= canvasBounds.getMinY() ||
                    ballBounds.getMaxY() >= canvasBounds.getMaxY()) {
                ball.setYVel(-ball.getYVel());
            }

            // 判断是否入洞
            for (Hole hole : holes) {
                Bounds holeBounds = hole.getNode().getBoundsInLocal();
                if (holeBounds.intersects(ballBounds)) {
                    // 使用策略模式
                    StrategyResult fallRes = ball.fallIntoHole();
                    switch (fallRes) {
                        case goal:
                        // 红球入一次洞、蓝球入两次洞，可以扩展积分系统，当前需删除入洞的球
                        System.out.println("goal");
                        ballsToRemove.add(ball);
                        break;
                        case gameOver:
                        // 白球入洞
                        text.setText("YOU LOSE...");
                        text.setVisible(true);
                        ballsToRemove.add(ball);
                        state = GameState.over;
                        break;
                    }
                }
            }
        }

        // 将所有暂存的球删去
        for (Ball ball : ballsToRemove) {
            ObservableList<Node> groupChildren = root.getChildren();
            ball.removeFromGroup(groupChildren);
            balls.remove(ball);
        }

        // 判断场上是否只剩白球，如果成立则游戏胜利
        if (balls.size() == 1 && balls.get(0) == whiteBall) {
            text.setText("YOU WIN!!!");
            text.setVisible(true);
            state = GameState.over;
        }
    }
}

