package gui;

import characters.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class OptionsPane extends BorderPane {

    private Player player;
    private GamePane currentView;
    private GameButton exitWithoutSave, saveAndExit;
    private GameLabel title;
    private GameLabel textSpeedLabel;
    private VBox centerBox;
    private Slider textSpeedSlider;

    public OptionsPane(GamePane currentView, Player player) {
        this.player = player;
        this.currentView = currentView;
        this.setId("standardPane");
        centerBox = new VBox(5);

        exitWithoutSave = new GameButton("Exit without saving");
        saveAndExit = new GameButton("Save and exit");

        exitWithoutSave.setOnAction(event -> {
            currentView.removeOptionsPane(this);
            currentView.requestFocus();
        });

        saveAndExit.setOnAction(event -> {
            //TODO assign settings to player variables
            player.setTextScrollingSpeed((int) textSpeedSlider.getValue());

            currentView.removeOptionsPane(this);
            currentView.requestFocus();
        });

        HBox textSpeedBox = new HBox(100);
        textSpeedBox.setAlignment(Pos.CENTER_LEFT);
        textSpeedLabel = new GameLabel("Text speed: " + player.getTextScrollingSpeed() + " ms");
        textSpeedBox.getChildren().add(textSpeedLabel);

        textSpeedSlider = new Slider(10, 90, player.getTextScrollingSpeed());
        textSpeedSlider.setShowTickLabels(true);
        textSpeedSlider.setShowTickMarks(true);
        textSpeedSlider.valueProperty().addListener(event -> {
            textSpeedLabel.setText("Text speed: " + (int) textSpeedSlider.getValue() + " ms");
        });

        textSpeedBox.getChildren().add(textSpeedSlider);

        centerBox.getChildren().add(textSpeedBox);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);
        title = new GameLabel("Options", 24);
        top.getChildren().add(title);
        this.setTop(top);

        HBox hb = new HBox(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(saveAndExit, exitWithoutSave);

        this.setBottom(hb);
        this.setCenter(centerBox);
        this.setMaxWidth(400);
        this.setMaxHeight(200);
    }

}
