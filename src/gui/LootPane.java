package gui;

import characters.Player;
import items.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import sprites.Lootable;

public class LootPane extends BorderPane {

    private GameLabel title;
    private GamePane currentView;
    private Lootable lootContainer;
    private Player player;
    private GameButton takeAll, exit;
    private HBox itemBox;
    private HBox hb;


    public LootPane(GamePane currentView, Lootable lootContainer, Player player) {
        this.currentView = currentView;
        this.lootContainer = lootContainer;
        this.player = player;
        itemBox = new HBox(15);
        this.setId("standardPane");
        this.title = new GameLabel(lootContainer.getItems().size() + (lootContainer.getItems().size() == 1 ? " item" : " items"));

        takeAll = new GameButton("Take All");
        takeAll.setOnAction(event -> {
            int totalWeight = 0;

            for(Item i : lootContainer.getItems()) {
                totalWeight += i.getWeight();
            }

            if((player.getCarryCap() - player.getCurrentCarry() > totalWeight)) {
                lootContainer.getItems().forEach(i -> player.addItem(i));
                lootContainer.getItems().removeAll(lootContainer.getItems());
                currentView.removeLootPane(this);
            } else {
                title.setText("Not enough space!");
            }
        });

        exit = new GameButton("Exit");
        exit.setOnAction(event -> {
            currentView.removeLootPane(this);
        });

        hb = new HBox(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(takeAll, exit);

        HBox titleWrapper = new HBox();
        titleWrapper.setAlignment(Pos.CENTER);
        titleWrapper.getChildren().add(title);

        drawItems();
        checkIfEmpty();
        this.setTop(title);
        this.setBottom(hb);
        this.setCenter(itemBox);
        hb.setMargin(itemBox, new Insets(5,0,0,0));
        if(lootContainer.getItems().size() > 0) {
            this.setMaxWidth(100 + 80*lootContainer.getItems().size());
        } else {
            this.setMaxWidth(150);
        }
        this.setMaxHeight(120);
    }

    private void drawItems() {
        if(lootContainer.getItems().size() > 0) {
            lootContainer.getItems().forEach(i -> {
                VBox vb = new VBox();
                vb.getChildren().add(new ItemPane(i, player));
                GameButton gb = new GameButton("Take");
                gb.setOnAction(event -> {
                    if(!player.addItem(i)) {
                        currentView.displayMessagePane("You cannot carry that!");
                    } else {
                        lootContainer.getItems().remove(i);
                    }
                    itemBox.getChildren().remove(vb);
                    checkIfEmpty();
                });
                vb.setAlignment(Pos.CENTER);
                vb.getChildren().add(gb);
                itemBox.getChildren().add(vb);
            });
        }
        this.setMargin(itemBox, new Insets(5, 0, 5, 0));
        itemBox.setAlignment(Pos.CENTER);
    }

    private void checkIfEmpty() {
        if(lootContainer.getItems().size() == 0) {
            GameLabel GameLabel = new GameLabel("Empty");
            GameLabel.setFont(new Font("Cambria", 18));
            this.setAlignment(GameLabel, Pos.CENTER);
            this.setTop(GameLabel);
            takeAll.setDisable(true);
        }
    }
}
