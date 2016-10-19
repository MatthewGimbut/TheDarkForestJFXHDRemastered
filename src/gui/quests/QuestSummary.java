package gui.quests;

import gui.GamePane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.GameStage;
import quests.Quest;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QuestSummary extends AnchorPane {

    private GamePane currentView;
    private Quest quest;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private AnchorPane anchor;
    @FXML private Label description;
    @FXML private Label title;

    public QuestSummary(GamePane currentView) {
        this.currentView = currentView;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\quests\\QuestSummary.fxml"));
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
        title.setFont(Font.font("Cambria", FontWeight.BOLD, 12));
        title.setText("No quest currently selected.");
        description.setFont(Font.font("Cambria", FontWeight.BOLD, 12));
        description.setText(" ¯\\_(ツ)_/¯");

        this.getChildren().add(anchor);

        Rectangle rekt = new Rectangle(anchor.getPrefWidth(), anchor.getPrefHeight());
        rekt.setArcHeight(GamePane.ARC_SIZE);
        rekt.setArcWidth(GamePane.ARC_SIZE);
        anchor.setClip(rekt);
    }

    public void setQuest(Quest quest) {
        //TODO add animation here in some fashion to avoid harsh transition (cross fade?)
        this.quest = quest;
        title.setText(quest.getQuestName());
        description.setText(quest.getCurrentTask().toString());
    }


    public Quest getQuest() {
        return this.quest;
    }
}
