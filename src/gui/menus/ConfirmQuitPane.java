package gui.menus;

import gui.GamePane;
import gui.GameScene;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import main.AudioManager;
import main.GameStage;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmQuitPane extends BorderPane {

    private GamePane currentView;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private Button cancel;
    @FXML private Button quit;
    @FXML private Label text;
    @FXML private AnchorPane anchor;

    public ConfirmQuitPane(GamePane currentView) {
        this.currentView = currentView;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\Menus\\ConfirmQuit.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (Exception exception) {
            GameStage.logger.error(exception.getMessage());
            GameStage.logger.error(exception);
            GameStage.logger.error(exception.getStackTrace());
        }
    }

    @FXML void initialize() {

        cancel.setOnAction(action -> {
            currentView.uiManager.removeConfirmQuitPane(this);
        });

        quit.setOnAction(action -> {
            Stage stage = currentView.getPrimaryStage();
            AudioManager.getInstance().stopAllAudio();
            MainMenuPane pane = new MainMenuPane(stage);
            Scene newScene = new GameScene(pane, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
            stage.setScene(newScene);
        });

        this.setCenter(anchor);

        Rectangle rekt = new Rectangle(anchor.getPrefWidth(), anchor.getPrefHeight());
        rekt.setArcHeight(GamePane.ARC_SIZE);
        rekt.setArcWidth(GamePane.ARC_SIZE);
        anchor.setClip(rekt);
    }
}
