package gui.quests;

import gui.GamePane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import quests.Quest;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class JournalPane extends BorderPane {

    private GamePane currentView;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private AnchorPane anchor;
    @FXML private Button brexit;
    @FXML private Label description;
    @FXML private Label gold;
    @FXML private VBox mainBox;
    @FXML private VBox sideBox;
    @FXML private Label title;
    @FXML private Label xp;


    public JournalPane(GamePane currentView) {
        this.currentView = currentView;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\quests\\JournalPane.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML void initialize() {

        brexit.setOnAction(event -> {
            currentView.removeJournalPane(this);
        });

        LinkedList<Quest> questList = (LinkedList<Quest>) quests.QuestHandler.activeQuests;
        questList.forEach(quest -> {
            Label label = new Label(quest.getQuestName());
            label.setId("inbentoryItem");

        });

        this.setCenter(anchor);
    }
}
