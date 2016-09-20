package gui;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import main.GameStage;

public class GameScene extends Scene {
    public GameScene(Pane pane, int width, int height) {
        super(pane, width, height);
        this.getStylesheets().add(GameStage.STYLESHEET);
    }
}
