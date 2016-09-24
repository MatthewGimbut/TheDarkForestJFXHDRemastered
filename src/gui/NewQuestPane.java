package gui;

import gui.GamePane;
import gui.ItemPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import quests.Quest;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewQuestPane extends BorderPane {

    private GamePane currentView;
    private Quest quest;

    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private Button accept;
    @FXML private Button decline;
    @FXML private Label gold;
    @FXML private Label questDescription;
    @FXML private Label questTitle;
    @FXML private FlowPane rewardsPane;
    @FXML private Label xp;

    public NewQuestPane(GamePane currentView, Quest quest) {
        this.currentView = currentView;
        this.quest = quest;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\NewQuestPane.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {
        xp.setText(quest.getExpReward() + " xp");
        gold.setText(quest.getMoneyReward() + " gold");
        questTitle.setText(quest.getQuestName());
        questDescription.setText(quest.getDescription());

        accept.setOnAction(event -> {
            //TODO accept quest

            currentView.removeNewQuestPane(this);
        });

        decline.setOnAction(event -> {
            //TODO decline quest

            currentView.removeNewQuestPane(this);
        });


        quest.getReward().forEach(item -> {
            //TODO Make it so that items are hidden if we don't want the user to see the reward yet, maybe display "?" for each item?
            rewardsPane.getChildren().add(new ItemPane(item, currentView.getMainPlayerSprite().getPlayer()));
        });
    }
}
