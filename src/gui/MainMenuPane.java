package gui;


import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.GameStage;

public class MainMenuPane extends AnchorPane {

    public MainMenuPane(Stage primaryStage) {

        GameButton start = new GameButton("Start");
        start.setPadding(new Insets(10, 100, 10, 100));
        this.setId("mainmenu");
        this.setBottomAnchor(start, 50.0);
        this.setLeftAnchor(start, 400.0);


        Group g = new Group();
        Rectangle rekt = new Rectangle(GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
        rekt.setFill(Color.BLACK);
        FadeTransition ft = new FadeTransition(Duration.millis(2000), g);
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.setAutoReverse(true);
        g.getChildren().addAll(rekt);
        this.getChildren().add(g);
        ft.play();
        this.getChildren().addAll(start);


        start.setOnAction(event -> {
            GamePane gp = new GamePane(primaryStage);
            GameScene scene = new GameScene(gp, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
            gp.requestFocus();
            scene.getStylesheets().add(GameStage.STYLESHEET);
            primaryStage.setScene(scene);
        });
    }
}
