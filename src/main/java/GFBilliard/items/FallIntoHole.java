package GFBilliard.Items;

import GFBilliard.Items.Ball;

// 不同球不同入洞行为的策略模式
public class FallIntoHole {
    private Strategy strategy;

    public FallIntoHole(Strategy strategy) {
        this.strategy = strategy;
    }

    public StrategyResult doFall() {
        return strategy.doStrategy();
    }

    public enum StrategyResult {
        gameOver, decraseLife, goal
    }

    public interface Strategy {
        public StrategyResult doStrategy();
    }
}
