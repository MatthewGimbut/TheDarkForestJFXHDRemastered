package gui;

import main.GameStage;
import quests.Quest;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import sprites.PlayerSprite;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QuestSuccess extends BorderPane {

    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private Button exit;
    @FXML private Label goldLabel;
    @FXML private Label questDescription;
    @FXML private Label questTitle;
    @FXML private FlowPane rewardsPane;
    @FXML private Label xpLabel;
    @FXML private AnchorPane anchor;
    private PlayerSprite playerSprite;
    private GamePane currentView;
    private Quest quest;

    public QuestSuccess(PlayerSprite playerSprite, GamePane currentView, Quest quest) {
        this.playerSprite = playerSprite;
        this.currentView = currentView;
        this.quest = quest;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\QuestSuccess.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {

        exit.setOnAction(event -> {
            GameStage.gamePane.removeQuestSuccessPane(this);
            currentView.resetMessagePaneFocus();
        });

        goldLabel.setText("" + quest.getMoneyReward() + " gold");
        questDescription.setText(quest.getDescription());
        questTitle.setText(quest.getQuestName());
        xpLabel.setText("" + quest.getExpReward() + " xp");
        quest.getReward().forEach(i -> {
            rewardsPane.getChildren().add(new ItemPane(i, GameStage.gamePane.getMainPlayerSprite().getPlayer()));
        });

        playerSprite.getPlayer().increaseXP(quest.getExpReward());
        playerSprite.getPlayer().modifyGold(quest.getMoneyReward());
        quest.getReward().forEach(playerSprite.getPlayer()::addItem);
        playerSprite.getPlayer().modifyGold(quest.getMoneyReward());


        this.setCenter(anchor);
    }
}
