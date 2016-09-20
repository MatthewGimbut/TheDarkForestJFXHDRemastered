package gui;

import characters.Player;
import items.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import sprites.Lootable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LootPane extends BorderPane {

    private GamePane currentView;
    private Lootable lootContainer;
    private Player player;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private Button exit;
    @FXML private FlowPane itemPane;
    @FXML private Label numItems;
    @FXML private Button takeAll;
    @FXML private AnchorPane anchor;

    public LootPane(GamePane currentView, Lootable lootContainer, Player player) {
        this.currentView = currentView;
        this.lootContainer = lootContainer;
        this.player = player;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\LootPane.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {

        exit.setOnAction(event -> currentView.removeLootPane(this));

        takeAll.setOnAction(event -> {
            int totalWeight = 0;

            for(Item i : lootContainer.getItems()) {
                totalWeight += i.getWeight();
            }

            if((player.getCarryCap() - player.getCurrentCarry() > totalWeight)) {
                lootContainer.getItems().forEach(player::addItem);
                lootContainer.getItems().removeAll(lootContainer.getItems());
                currentView.removeLootPane(this);
            } else {
                numItems.setText("Not enough space!");
            }
        });

        drawItems();
        this.setCenter(anchor);
    }

    private void drawItems() {
        numItems.setText(lootContainer.getItems().size() + " Items");
        if(lootContainer.getItems().size() > 0) {
            lootContainer.getItems().forEach(i -> {
                VBox vb = new VBox();
                vb.getChildren().add(new ItemPane(i, player));
                Button gb = new Button("Take");
                gb.setOnAction(event -> {
                    if(!player.addItem(i)) {
                        currentView.displayMessagePane("You cannot carry that!");
                    } else {
                        lootContainer.getItems().remove(i);
                    }
                    itemPane.getChildren().remove(vb);
                });
                vb.setAlignment(Pos.CENTER);
                vb.getChildren().add(gb);
                itemPane.getChildren().add(vb);
            });
        }
    }
}
