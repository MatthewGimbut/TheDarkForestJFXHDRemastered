package gui;

import characters.Player;
import items.Armor.Armor;
import items.Consumables.Consumable;
import items.Item;
import items.Weapons.Weapon;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.GameStage;
import sun.plugin.javascript.navig.Anchor;
import sun.plugin.javascript.navig.Array;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ScrollingInventoryPane extends BorderPane {

    private GamePane currentView;
    private Player player;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private ScrollPane armorScroll;
    @FXML private Tab armorTab;
    @FXML private ScrollPane consumablesScroll;
    @FXML private Tab consumablesTab;
    @FXML private Button exit;
    @FXML private ScrollPane favoritesScroll;
    @FXML private Tab favoritesTab;
    @FXML private ImageView itemImage;
    @FXML private Label itemMessage;
    @FXML private Label itemName;
    @FXML private ScrollPane miscScroll;
    @FXML private Tab miscTab;
    @FXML private AnchorPane pane;
    @FXML private Label stat1;
    @FXML private Label stat2;
    @FXML private Label stat3;
    @FXML private Label stat4;
    @FXML private Label stat5;
    @FXML private Label stat6;
    @FXML private Label title;
    @FXML private ScrollPane weaponsScroll;
    @FXML private Tab weaponsTab;


    public ScrollingInventoryPane(GamePane currentView, Player player) {
        this.currentView = currentView;
        this.player = player;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\ScrollingInventoryPane.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {
        /*assert armorScroll != null : "fx:id=\"armorScroll\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert armorTab != null : "fx:id=\"armorTab\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert consumablesScroll != null : "fx:id=\"consumablesScroll\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert consumablesTab != null : "fx:id=\"consumablesTab\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert exit != null : "fx:id=\"exit\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert favoritesScroll != null : "fx:id=\"favoritesScroll\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert favoritesTab != null : "fx:id=\"favoritesTab\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert itemImage != null : "fx:id=\"itemImage\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert itemMessage != null : "fx:id=\"itemMessage\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert itemName != null : "fx:id=\"itemName\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert miscScroll != null : "fx:id=\"miscScroll\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert miscTab != null : "fx:id=\"miscTab\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert stat1 != null : "fx:id=\"stat1\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert stat2 != null : "fx:id=\"stat2\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert stat3 != null : "fx:id=\"stat3\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert stat4 != null : "fx:id=\"stat4\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert title != null : "fx:id=\"title\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert weaponsScroll != null : "fx:id=\"weaponsScroll\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        assert weaponsTab != null : "fx:id=\"weaponsTab\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";*/

        title.setText(player.getName() + "'s Inventory");
        title.setFont(new Font("Cambria", 20));

        exit.setOnAction(event -> {
            currentView.removeInventoryPane(this);
        });

        favoritesScroll.setContent(drawFavorites());
        weaponsScroll.setContent(drawWeapons());
        armorScroll.setContent(drawArmors());
        consumablesScroll.setContent(drawConsumables());
        miscScroll.setContent(drawMisc());

        this.setCenter(pane);
    }

    VBox drawFavorites() {
        favoritesScroll.setContent(null);
        VBox favoritesBox = new VBox(15);
        ArrayList<Item> favorites = new ArrayList<>();
        player.getInventory().forEach(item -> {
            if(item.isFavorite()) {
                favorites.add(item);
            }
        });

        for(Item i : favorites) {
            TextItemPane label = new TextItemPane(i, player);
            label.setOnMouseEntered(new ItemDisplayHandler(label));
            label.setId("inventoryItem");
            favoritesBox.setMargin(label, new Insets(2, 0, 2, 5));
            favoritesBox.getChildren().add(label);
        }

        return favoritesBox;
    }

    VBox drawWeapons() {
        weaponsScroll.setContent(null);
        VBox weaponsBox = new VBox(15);
        ArrayList<Weapon> weapons = new ArrayList<>();
        player.getInventory().forEach(item -> {
            if(item instanceof Weapon) {
                weapons.add((Weapon) item);
            }
        } );

        for(Weapon w : weapons) {
            TextItemPane label = new TextItemPane(w, player);
            label.setOnMouseEntered(new ItemDisplayHandler(label));
            label.setOnMouseExited(new ItemRemovalHandler(label));


            weaponsBox.setMargin(label, new Insets(2, 0, 2, 5));
            label.setId("inventoryItem");
            weaponsBox.getChildren().add(label);
        }

        return weaponsBox;
    }

    VBox drawArmors() {
        armorScroll.setContent(null);
        VBox armorsBox = new VBox(15);
        ArrayList<Armor> armors = new ArrayList<>();
        player.getInventory().forEach(item -> {
            if(item instanceof Armor) {
                armors.add((Armor) item);
            }
        });

        for(Armor a : armors) {
            TextItemPane label = new TextItemPane(a, player);
            label.setOnMouseEntered(new ItemDisplayHandler(label));
            label.setId("inventoryItem");
            armorsBox.setMargin(label, new Insets(2, 0, 2, 5));
            armorsBox.getChildren().add(label);
        }

        return armorsBox;
    }

    VBox drawConsumables() {
        consumablesScroll.setContent(null);
        VBox consumablesBox = new VBox(15);
        ArrayList<Consumable> consumables = new ArrayList<>();
        player.getInventory().forEach(item -> {
            if(item instanceof Consumable) {
                consumables.add((Consumable) item);
            }
        });

        for(Consumable c : consumables) {
            TextItemPane label = new TextItemPane(c, player);
            label.setOnMouseEntered(new ItemDisplayHandler(label));
            label.setId("inventoryItem");
            consumablesBox.setMargin(label, new Insets(2, 0, 2, 5));
            consumablesBox.getChildren().add(label);
        }

        return consumablesBox;
    }

    VBox drawMisc() {
        miscScroll.setContent(null);
        VBox miscBox = new VBox(15);

        //TODO implement miscellaneous items???

        return miscBox;
    }

    private class ItemDisplayHandler implements EventHandler {

        private TextItemPane label;

        public ItemDisplayHandler(TextItemPane label) {
            this.label = label;
        }

        @Override
        public void handle(Event event) {
            itemName.setText(label.getItem().getSimpleName());
            itemImage.setImage(label.getItemImage());
            stat1.setText("Atk: " + label.getItem().getAtk());
            stat2.setText("Def: " + label.getItem().getDef());
            stat3.setText("Spd: -" + label.getItem().getSpeedModifier());
            stat4.setText("Weight: " + label.getItem().getWeight());
            stat5.setText("Magic: " + label.getItem().getMagic());
            stat6.setText("HP: +" + label.getItem().getHpBoost());
            itemMessage.setText(label.getItem().getItemToolTipText());
        }
    }

    private class ItemRemovalHandler implements EventHandler {

        private TextItemPane label;

        public ItemRemovalHandler(TextItemPane label) {
            this.label = label;
        }

        @Override
        public void handle(Event event) {

        }
    }

}
