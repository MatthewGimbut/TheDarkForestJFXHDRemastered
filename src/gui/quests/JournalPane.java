package gui.quests;

import gui.GamePane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import main.GameStage;
import quests.Quest;
import quests.QuestHandler;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;
import java.util.ResourceBundle;

public class JournalPane extends BorderPane {

    private GamePane currentView;
    private Random rand;
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
    @FXML private WebView video;


    public JournalPane(GamePane currentView) {
        this.currentView = currentView;
        this.rand = new Random();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\quests\\JournalPane.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            GameStage.logger.error(exception.getMessage());
            GameStage.logger.error(exception);
        }
    }

    @FXML void initialize() {

        brexit.setOnAction(event -> {
            currentView.removeJournalPane(this);
        });

        this.setOnKeyReleased((KeyEvent key) -> {
            String code = key.getCode().toString();
            if(code.equals("ESCAPE")) brexit.fire();
        });

        drawQuests();

        if(rand.nextInt(100) < 95) { //5% chance to get Lit Lizards? I'll take it.
            anchor.getChildren().remove(video);
        } else {
            video.getEngine().load("https://www.youtube.com/watch?v=dpjFcLIm6mc?autoplay=1");
            //video.setPrefSize(640, 390);
        }

        this.setCenter(anchor);
    }

    private void drawQuests() {
        LinkedList<Quest> questList = (LinkedList<Quest>) quests.QuestHandler.activeQuests;
        questList.forEach(quest -> {
            TextQuestPane tqp = new TextQuestPane(quest, currentView.getMainPlayerSprite().getPlayer());

            tqp.setOnMouseEntered(mousey -> {
                title.setText(tqp.getQuest().getQuestName());
                description.setText(tqp.getQuest().getDescription());
                xp.setText(tqp.getQuest().getExpReward() + " xp");
                gold.setText(tqp.getQuest().getMoneyReward() + " gold");
            });

            tqp.setOnMouseClicked(click -> {
                quests.QuestHandler.setPriorityQuest(tqp.getQuest());
                mainBox.getChildren().clear();
                sideBox.getChildren().clear();
                drawQuests();
            });

            Quest active = QuestHandler.priorityQuest;

            if(quest.equals(active)) {
                tqp.setColor(Color.RED);
            }

            Insets margin = new Insets(2, 0, 2, 5);
            if(quest.isStory()) {
                mainBox.setMargin(tqp, margin);
                mainBox.getChildren().add(tqp);
            } else {
                sideBox.setMargin(tqp, margin);
                sideBox.getChildren().add(tqp);
            }
        });
    }
}
