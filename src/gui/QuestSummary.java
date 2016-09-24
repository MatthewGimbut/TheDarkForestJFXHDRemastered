package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
                "gui\\QuestSummary.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {
        title.setText("No quest currently selected.");
        description.setText(" ¯\\_(ツ)_/¯");

        this.getChildren().add(anchor);
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
        title.setText(quest.getQuestName());
        description.setText(quest.getCurrentTask().toString());
    }


    public Quest getQuest() {
        return this.quest;
    }
}
