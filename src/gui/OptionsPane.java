package gui;

import characters.Player;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Created by Matthew on 8/15/2016.
 */
public class OptionsPane extends BorderPane {

    private Player player;
    private GamePane currentView;
    private GameButton exitWithoutSave, saveAndExit;

    public OptionsPane(GamePane currentView, Player player) {
        this.player = player;
        this.currentView = currentView;
        this.setId("standardPane");

        exitWithoutSave = new GameButton("Exit without saving");
        saveAndExit = new GameButton("Save and exit");

        exitWithoutSave.setOnAction(event -> {
            currentView.removeOptionsPane(this);
            currentView.requestFocus();
        });

        saveAndExit.setOnAction(event -> {
            //TODO assign settings to player variables

            currentView.removeOptionsPane(this);
            currentView.requestFocus();
        });

        HBox hb = new HBox();
        hb.getChildren().addAll(saveAndExit, exitWithoutSave);

        this.setMaxWidth(400);
        this.setMaxHeight(200);
    }

}
