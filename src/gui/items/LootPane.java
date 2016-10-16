package gui.items;

import characters.Player;
import gui.GamePane;
import items.Item;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import main.GameStage;
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
                "gui\\items\\LootPane.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            GameStage.logger.error(exception.getMessage());
            GameStage.logger.error(exception);
        }
    }

    @FXML
    void initialize() {

        exit.setOnAction(event -> currentView.removeLootPane(this));

        this.setOnKeyReleased((KeyEvent key) -> {
            String code = key.getCode().toString();
            if(code.equals("ESCAPE")) exit.fire();
        });

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
        anchor.setMinHeight((lootContainer.getItems().size()+0.0)/4);
        this.setCenter(anchor);
    }

    private void drawItems() {
        numItems.setText(lootContainer.getItems().size() + " Items");
        if(lootContainer.getItems().size() > 0) {
            lootContainer.getItems().forEach(i -> {
                VBox vb = new VBox();
                ItemPane ip = new ItemPane(i, player);
                vb.getChildren().add(ip);
                ip.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if(!player.addItem(i)) {
                        currentView.displayMessagePane("You cannot carry that!");
                    } else {
                        itemPane.getChildren().remove(vb);
                        lootContainer.getItems().remove(i);
                        numItems.setText(lootContainer.getItems().size() + " Items");
                    }
                });
                vb.setAlignment(Pos.CENTER);
                itemPane.getChildren().add(vb);
            });
        }
    }
}
