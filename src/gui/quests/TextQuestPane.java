package gui.quests;

import characters.Player;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import quests.Quest;


public class TextQuestPane extends HBox {

    private Label questName;
    private Quest quest;
    private Player player;
    private Color color;

    public TextQuestPane(Quest q, Player player) {
        this.quest = q;
        this.player = player;
        this.color = Color.BLACK;

        questName = new Label(q.getQuestName());
        questName.setTextFill(color);

        this.setId("questTitle");

        this.getChildren().add(questName);
    }

    public Label getLabel() {
        return this.questName;
    }

    public Color getColor() {
        return this.color;
    }

    public Quest getQuest() {
        return this.quest;
    }

    public void setColor(Color color) {
        this.color = color;
        questName.setTextFill(this.color);
    }

}
