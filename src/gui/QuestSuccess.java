package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import quests.QuestHandler;
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

    public QuestSuccess(PlayerSprite playerSprite, GamePane currentView) {
        this.playerSprite = playerSprite;
        this.currentView = currentView;

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

        exit.setOnAction(event -> System.out.println("test"));

        quests.Quest priority = QuestHandler.priorityQuest;

        goldLabel.setText("" + priority.getMoneyReward());
        questDescription.setText(priority.getDescription());
        questTitle.setText(priority.getQuestName());
        xpLabel.setText("" + priority.getExpReward());
        priority.getReward().forEach(i -> {
            rewardsPane.getChildren().add(new ItemPane(i, null));
        }); //I hope this works?

        playerSprite.getPlayer().increaseXP(priority.getExpReward());
        //add money when it is included as a feature in player
        priority.getReward().forEach(playerSprite.getPlayer()::addItem);


        this.setCenter(anchor);
    }
}
