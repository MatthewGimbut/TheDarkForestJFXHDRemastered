package gui.menus;

import characters.Player;
import gui.GamePane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import main.AudioManager;
import main.GameStage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsPane extends BorderPane {

    private Player player;
    private GamePane currentView;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private AnchorPane anchor;
    @FXML private ComboBox<?> colorComboBox;
    @FXML private Button exit;
    @FXML private Label textSpeedLabel;
    @FXML private Slider textSpeedSlider;
    @FXML private Button save;

    public OptionsPane(GamePane currentView, Player player) {
        this.player = player;
        this.currentView = currentView;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\menus\\OptionsPane.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            GameStage.logger.error(exception.getMessage());
            GameStage.logger.error(exception);
        }
    }

    @FXML
    void initialize() {

        exit.setOnAction(event -> currentView.uiManager.removeOptionsPane(this));

        this.setOnKeyReleased((KeyEvent key) -> {
            String code = key.getCode().toString();
            if(code.equals("ESCAPE")) exit.fire();
        });

        save.setOnAction(event -> {
            player.setTextScrollingSpeed((int) textSpeedSlider.getValue());
            currentView.uiManager.removeOptionsPane(this);
        });

        textSpeedSlider.setValue(player.getTextScrollingSpeed());

        textSpeedLabel.setText("Text speed: " + player.getTextScrollingSpeed() + " ms");

        textSpeedSlider.valueProperty().addListener(event -> {
            textSpeedLabel.setText("Text speed: " + (int) textSpeedSlider.getValue() + " ms");
        });

        this.setCenter(anchor);

        Rectangle rekt = new Rectangle(anchor.getPrefWidth(), anchor.getPrefHeight());
        rekt.setArcHeight(GamePane.ARC_SIZE);
        rekt.setArcWidth(GamePane.ARC_SIZE);
        anchor.setClip(rekt);
        AudioManager.getInstance().playSound(AudioManager.MENU_OPEN);
    }

}
