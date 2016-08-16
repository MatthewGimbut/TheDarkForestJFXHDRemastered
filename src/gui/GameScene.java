package gui;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import main.GameStage;

/**
 * Created by Matthew on 8/13/2016.
 */
public class GameScene extends Scene {
    public GameScene(Pane pane, int width, int height) {
        super(pane, width, height);
        this.getStylesheets().add(GameStage.STYLESHEET);
    }
}
