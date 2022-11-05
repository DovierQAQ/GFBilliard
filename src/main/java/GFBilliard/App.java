package GFBilliard;

import GFBilliard.Items.Ball;
import GFBilliard.Items.Board;
import GFBilliard.Items.Table;
import GFBilliard.Items.ConfigReader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.util.Duration;

public class App extends Application {

    private static final String TITLE = "GFBilliard";
    // private static final double DIM_X = 800.0;
    // private static final double DIM_Y = 600.0;
    private static final double FRAMETIME = 1.0/60.0;

    @Override
    public void start(Stage stage) throws Exception {
        // ConfigReader.parse("src/main/resources/config.json");
        ConfigReader configReader = new ConfigReader();
        Table table = (Table)(configReader.getConfig(ConfigReader.ConfigContent.table)[0]);
        Ball[] balls = (Ball[])configReader.getConfig(ConfigReader.ConfigContent.balls);

        Group root = new Group();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.show();

        stage.setWidth(table.bounds[0] + 15);
        stage.setHeight(table.bounds[1] + 40);
        stage.setResizable(false);
        Canvas canvas = new Canvas(table.bounds[0], table.bounds[1]);
        root.getChildren().add(canvas);
        Game game = new Game(table, balls, canvas);
        game.addDrawables(root);

        // setup frames
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.seconds(FRAMETIME), (actionEvent) -> game.tick());
        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
