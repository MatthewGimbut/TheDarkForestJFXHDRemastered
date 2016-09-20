package gui;

import characters.Enemy;
import characters.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import sprites.NPC;
import java.util.ArrayList;

public class MessagePane extends BorderPane {

    private Label displayedMessage;
    private String currentDisplay;
    private String singleMessage;
    private String[] messages;
    private int messageCounter;
    private char[] messageChars;
    private int scrollIndex;
    private GamePane currentView;
    private NPC npc;
    private Player player;
    private boolean processing;
    private Timeline t;
    private boolean keyHasBeenReleased;
    private int millis;

    private MessagePane(Player player, GamePane currentView) {
        keyHasBeenReleased = true;
        currentDisplay = "";
        this.setId("standardPane");
        this.player = player;
        this.currentView = currentView;
        this.displayedMessage = new Label("");
        this.displayedMessage.setFont(new Font("Cambria", 34));
        this.displayedMessage.setAlignment(Pos.BASELINE_LEFT);
        this.millis = player.getTextScrollingSpeed();

        this.setOnKeyPressed(event -> {
            if(event.getCode().toString().equals("E") && keyHasBeenReleased) {
                keyHasBeenReleased = false;
                if (!processing) {
                    if (messages != null) {
                        messageCounter++;
                        try {
                            if(messages[messageCounter] == null) {
                                checkForBattle();
                                currentView.removeMessagePane(this);
                            } else {
                                scrollMessage(messages[messageCounter]);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            checkForBattle();
                            currentView.removeMessagePane(this);
                        }
                    } else {
                        checkForBattle();
                        currentView.removeMessagePane(this);
                    }
                } else {
                    if(messages != null)
                        setDisplayedMessage(messages[messageCounter]);
                    else
                        setDisplayedMessage(singleMessage);
                }
            }
        });

        this.setMaxWidth(1000);
        this.setMaxHeight(50);

        this.setOnKeyReleased(event -> keyHasBeenReleased = true );
    }

    public MessagePane(String message, Player player, GamePane currentView) {
        this(player, currentView);
        this.singleMessage = message;
        this.currentView = currentView;
        this.displayedMessage.setOpacity(100);
        this.setLeft(this.displayedMessage);
        scrollMessage(message);
    }

    public MessagePane(String[] message, Player player, GamePane currentView, NPC npc) {
        this(player, currentView);
        this.messages = message;
        this.currentView = currentView;
        this.npc = npc;
        this.currentDisplay = "";

        ImageView convBubble = new ImageView(new Image("file:Images\\Misc\\conversation.png"));

        HBox top = new HBox();
        if(npc != null) {
            Label name = new Label(npc.getNPC().getName());
            name.setFont(new Font("Cambria", 28));
            top.getChildren().addAll(convBubble, name);
            this.setTop(top);
            this.setMaxWidth(1000);
            this.setMaxHeight(100);
        }
        this.setLeft(this.displayedMessage);
        scrollMessage(this.messages[messageCounter]);
    }

    private void scrollMessage(String toScroll) {
        scrollIndex = 0;
        messageChars = toScroll.toCharArray();
        t = new Timeline(new KeyFrame(
                Duration.millis(this.millis), event -> {
            try {
                processing = true;
                currentDisplay += messageChars[scrollIndex];
                displayedMessage.setText(currentDisplay);
                scrollIndex++;
            } catch(ArrayIndexOutOfBoundsException e) {
                t.stop();
            }
        }));

        t.setCycleCount(messageChars.length);
        t.setOnFinished(event -> {
            processing = false;
            currentDisplay = "";
        });
        t.play();
    }

    private void setDisplayedMessage(String toDisplay) {
        t.stop();
        displayedMessage.setText(toDisplay);
        processing = false;
        currentDisplay = "";
    }

    private void checkForBattle() {
        if(npc != null && npc.getNPC() instanceof Enemy) {
            //TODO inefficient/pointless, find better way to do this
            ArrayList<Enemy> enemy = new ArrayList<>();
            enemy.add((Enemy) npc.getNPC());
            //currentView.displayBattlePanel(enemy, npc);
            System.out.println("normally a battle would trigger now");
        }
    }
}
