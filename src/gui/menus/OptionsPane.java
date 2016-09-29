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
import javafx.scene.layout.*;

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
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {

        exit.setOnAction(event -> currentView.removeOptionsPane(this));

        save.setOnAction(event -> {
            player.setTextScrollingSpeed((int) textSpeedSlider.getValue());
            currentView.removeOptionsPane(this);
        });

        textSpeedSlider.setValue(player.getTextScrollingSpeed());

        textSpeedLabel.setText("Text speed: " + player.getTextScrollingSpeed() + " ms");

        textSpeedSlider.valueProperty().addListener(event -> {
            textSpeedLabel.setText("Text speed: " + (int) textSpeedSlider.getValue() + " ms");
        });

        this.setCenter(anchor);
    }

}
