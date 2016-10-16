package gui.menus;


import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import gui.GameButton;
import gui.GamePane;
import gui.GameScene;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.GameStage;
import mapping.MapContainer;
import main.SaveManager;
import sprites.PlayerSprite;

import java.util.ArrayList;

public class MainMenuPane extends AnchorPane {

    public MainMenuPane(Stage primaryStage) {

        VBox pane = new VBox(5);
        pane.setAlignment(Pos.CENTER);
        GameButton start = new GameButton("New Game");
        GameButton load = new GameButton("Continue");
        GameButton quit = new GameButton("Quit");

        start.setMinWidth(200);
        load.setMinWidth(200);
        quit.setMinWidth(200);
        this.setId("mainmenu");

        pane.getChildren().addAll(start, load, quit);
        pane.setAlignment(Pos.CENTER);

        this.setBottomAnchor(pane, 50.0);
        this.setLeftAnchor(pane, 425.0);

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
        this.getChildren().addAll(pane);


        start.setOnAction(event -> {
            GamePane gp = new GamePane(primaryStage);
            GameStage.setGamePane(gp);
            GameScene scene = new GameScene(gp, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
            gp.requestFocus();
            scene.getStylesheets().add(GameStage.STYLESHEET);
            primaryStage.setScene(scene);
        });

        load.setOnAction(event -> {
            GamePane gp = new GamePane(primaryStage);
            GameStage.setGamePane(gp);
            ArrayList<Object> newMap = null;
            try {
                newMap = SaveManager.deserialize();
                gp.setCurrentMapFile((String) newMap.get(1));
                gp.setPlayer((PlayerSprite) newMap.get(0));
                gp.getMainPlayerSprite().setImage(gp.getMainPlayerSprite().getImageLocation());
                gp.setId((String) newMap.get(2));
            } catch (SerializationException | NullPointerException e) {
                GameStage.logger.error(e);
            }

            try {
               MapContainer map = new MapContainer(gp.getMainPlayerSprite(), (String) newMap.get(1));
                gp.setMapContainer(map);
            } catch (Exception e) {
                GameStage.logger.error(e);
            }

            gp.fillEnemies();

            GameScene scene = new GameScene(gp, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
            gp.requestFocus();
            scene.getStylesheets().add(GameStage.STYLESHEET);
            primaryStage.setScene(scene);
        });

        quit.setOnAction(event -> {
            System.exit(0);
        });
    }
}
