package GFBilliard.Items;

import GFBilliard.Drawable;
import GFBilliard.Movable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball implements Movable, Drawable, ConfigReader.ConfigItem {
    private static final double RASIUS = 15.0;
    private Circle shape;

    private double[] startingPosition = {0.0, 0.0};
    private double[] velocity = {0.0, 0.0};
    private double mass = 0.0;
    private int life = 1;

    private FallIntoHole fall;

    private Ball(Builder builder) {
        this.shape = builder.shape;
        this.startingPosition = builder.startingPosition;
        this.velocity = builder.velocity;
        this.mass = builder.mass;
        this.life = builder.life;
        this.fall = builder.fall;
    }

    public FallIntoHole.StrategyResult fallIntoHole() {
        FallIntoHole.StrategyResult res = fall.doFall();
        if (res == FallIntoHole.StrategyResult.decraseLife) {
            life--;
            if (life == 0) {
                res = FallIntoHole.StrategyResult.goal;
            } else {
                reset();
            }
        }
        return res;
    }

    private void reset() {
        setXPos(startingPosition[0]);
        setYPos(startingPosition[1]);
        setXVel(0.0);
        setYVel(0.0);
    }

    // 建造者模式
    public static class Builder {
        private Circle shape;
        private double[] startingPosition = {0.0, 0.0};
        private double[] velocity = {0.0, 0.0};
        private double mass = 1.0;
        private int life = 1;
        private FallIntoHole fall;

        public Builder(double posX, double posY, String color) {
            this.shape = new Circle(posX, posY, RASIUS);
            this.shape.setFill(Color.valueOf(color));
            startingPosition[0] = posX;
            startingPosition[1] = posY;

            switch (color) {
                case "white":
                life = 0;
                fall = new FallIntoHole(new WhiteBall());
                break;
                case "red":
                life = 1;
                fall = new FallIntoHole(new RedBall());
                break;
                case "blue":
                life = 2;
                fall = new FallIntoHole(new BlueBall());
                break;
            }
        }

        public Builder setVelocity(double[] velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder setMass(double mass) {
            this.mass = mass;
            return this;
        }

        public Ball build() {
            return new Ball(this);
        }
    }

    // 使用策略模式决定不球的行为
    private static class RedBall implements FallIntoHole.Strategy {
        @Override
        public FallIntoHole.StrategyResult doStrategy() {
            return FallIntoHole.StrategyResult.decraseLife;
        }
    }

    private static class BlueBall implements FallIntoHole.Strategy {
        @Override
        public FallIntoHole.StrategyResult doStrategy() {
            return FallIntoHole.StrategyResult.decraseLife;
        }
    }

    private static class WhiteBall implements FallIntoHole.Strategy {
        @Override
        public FallIntoHole.StrategyResult doStrategy() {
            return FallIntoHole.StrategyResult.gameOver;
        }
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
