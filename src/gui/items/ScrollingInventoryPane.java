package gui.items;

import characters.Player;
import gui.GamePane;
import items.Armor.Armor;
import items.Consumables.Consumable;
import items.Item;
import items.Weapons.Weapon;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.GameStage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ScrollingInventoryPane extends BorderPane {

    private GamePane currentView;
    private Player player;
    private ArrayList<Item> miscList, favoritesList;
    private ArrayList<Armor> armorList;
    private ArrayList<Weapon> weaponList;
    private ArrayList<Consumable> consumableList;
    private ArrayList<TextItemPane> favLabels, wepLabels, armorLabels, consumableLabels, miscLabels, allLabels;
    private DecimalFormat format;
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
    @FXML private Label info;
    @FXML private Label weight;
    @FXML private ScrollPane allScroll;
    @FXML private Tab allTab;



    public ScrollingInventoryPane(GamePane currentView, Player player) {
        this.currentView = currentView;
        this.player = player;

        miscList = new ArrayList<>();
        weaponList = new ArrayList<>();
        armorList = new ArrayList<>();
        consumableList = new ArrayList<>();
        favoritesList = new ArrayList<>();

        allLabels = new ArrayList<>();
        favLabels = new ArrayList<>();
        wepLabels = new ArrayList<>();
        armorLabels = new ArrayList<>();
        consumableLabels = new ArrayList<>();
        miscLabels = new ArrayList<>();

        format = new DecimalFormat("#.#");

        this.setOnKeyReleased((KeyEvent key) -> {
            String code = key.getCode().toString();
            if(code.equals("ESCAPE")) exit.fire();
        });

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\items\\ScrollingInventoryPane.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {
        title.setText(player.getName() + "'s Inventory");
        title.setFont(new Font("Cambria", 20));
        weight.setText("(" + format.format(player.getCurrentCarry()) + " / " + player.getCarryCap() + ")");
        weight.setFont(new Font("Cambria", 18));

        exit.setOnAction(event -> {
            currentView.removeInventoryPane(this);
        });

        sortItems();

        allScroll.setContent(drawAll());
        drawColors(allLabels);
        favoritesScroll.setContent(drawFavorites());
        drawColors(favLabels);
        weaponsScroll.setContent(drawWeapons());
        drawColors(wepLabels);
        armorScroll.setContent(drawArmors());
        drawColors(armorLabels);
        consumablesScroll.setContent(drawConsumables());
        miscScroll.setContent(drawMisc());
        drawColors(miscLabels);

        this.setCenter(pane);
    }

    private void sortItems() {
        weaponList.clear();
        armorList.clear();
        consumableList.clear();
        favoritesList.clear();
        miscList.clear();

        favoritesList = (ArrayList<Item>) player.getInventory().stream()
                .filter(Item::isFavorite)
                .collect(Collectors.toList());

        player.getInventory().forEach(item -> {
            if (item instanceof Weapon) {
                weaponList.add((Weapon) item);
            } else if (item instanceof Armor) {
                armorList.add((Armor) item);
            } else if (item instanceof Consumable) {
                consumableList.add((Consumable) item);
            } else {
                miscList.add(item);
            }
        });
    }

    VBox drawAll() {
        allScroll.setContent(null);
        VBox allBox = new VBox(5);

        for(Item i : player.getInventory()) {
            TextItemPane label = new TextItemPane(i, player);
            addHandlers(label);
            allLabels.add(label);
            allBox.setMargin(label, new Insets(2, 0, 2, 5));
            allBox.getChildren().add(label);
        }

        return allBox;
    }

    VBox drawFavorites() {
        favoritesScroll.setContent(null);
        VBox favoritesBox = new VBox(5);

        for(Item i : favoritesList) {
            TextItemPane label = new TextItemPane(i, player);
            addHandlers(label);
            favLabels.add(label);
            favoritesBox.setMargin(label, new Insets(2, 0, 2, 5));
            favoritesBox.getChildren().add(label);
        }

        return favoritesBox;
    }

    VBox drawWeapons() {
        weaponsScroll.setContent(null);
        VBox weaponsBox = new VBox(5);

        for(Weapon w : weaponList) {
            TextItemPane label = new TextItemPane(w, player);
            addHandlers(label);
            wepLabels.add(label);
            weaponsBox.setMargin(label, new Insets(2, 0, 2, 5));
            weaponsBox.getChildren().add(label);
        }

        return weaponsBox;
    }

    VBox drawArmors() {
        armorScroll.setContent(null);
        VBox armorsBox = new VBox(5);

        for(Armor a : armorList) {
            TextItemPane label = new TextItemPane(a, player);
            addHandlers(label);
            armorLabels.add(label);
            armorsBox.setMargin(label, new Insets(2, 0, 2, 5));
            armorsBox.getChildren().add(label);
        }

        return armorsBox;
    }

    VBox drawConsumables() {
        consumablesScroll.setContent(null);
        VBox consumablesBox = new VBox(5);

        for(Consumable c : consumableList) {
            TextItemPane label = new TextItemPane(c, player);
            addHandlers(label);
            consumablesBox.setMargin(label, new Insets(2, 0, 2, 5));
            consumablesBox.getChildren().add(label);
        }

        return consumablesBox;
    }

    VBox drawMisc() {
        miscScroll.setContent(null);
        VBox miscBox = new VBox(5);

        //TODO implement miscellaneous items???

        return miscBox;
    }

    private void addHandlers(TextItemPane label) {
        label.setOnMouseEntered(new ItemDisplayHandler(label));
        label.setOnMouseClicked(new EquipHandler(label));
        label.setId("inventoryItem");
    }

    private void drawColors(ArrayList<TextItemPane> labels) {
        labels.forEach(label -> {
            if(label.getItem().isCurrentlyEquipped()) {
                label.setColor(Color.RED);
            } else {
                label.setColor(Color.BLACK);
            }
        });
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
            Tooltip tp = new Tooltip(label.getItem().getItemToolTipText());
            tp.setOpacity(.85);
            tp.setAutoHide(false);
            tp.setFont(new Font("Cambria", 15));
            Tooltip.install(label, tp);
        }
    }

    private class EquipHandler implements EventHandler<MouseEvent> {

        private TextItemPane itemPane;

        public EquipHandler(TextItemPane i) { this.itemPane = i; }

        @Override
        public void handle(MouseEvent event) {
            if(event.getButton() == MouseButton.PRIMARY) { //Left Click
                if (itemPane.getItem().isCurrentlyEquipped()) { //Unequip
                    if (itemPane.getItem() instanceof Armor) {
                        player.unequip(itemPane.getItem());
                        GameStage.playSound("Sounds\\Inventory\\Equip\\leather_inventory.mp3");
                        drawColors(armorLabels);
                    } else if (itemPane.getItem() instanceof Weapon) {
                        player.unequip(itemPane.getItem());
                        GameStage.playSound("Sounds\\Inventory\\Equip\\leather_inventory.mp3");
                        drawColors(wepLabels);
                    } else {
                        player.unequip(itemPane.getItem());
                        GameStage.playSound("Sounds\\Inventory\\Equip\\leather_inventory.mp3");
                    }
                    info.setText("Unequipped " + itemPane.getItem().getSimpleName() + ". ");
                } else { //Equip
                    if (itemPane.getItem() instanceof Armor) {
                        player.equip((Armor) itemPane.getItem());
                        GameStage.playSound("Sounds\\Inventory\\Equip\\leather_inventory.mp3");
                        drawColors(armorLabels);
                        info.setText("Equipped " + itemPane.getItem().getSimpleName() + ". ");
                    } else if (itemPane.getItem() instanceof Weapon) {
                        player.equip((Weapon) itemPane.getItem());
                        info.setText("Equipped " + itemPane.getItem().getSimpleName() + ". ");
                        drawColors(wepLabels);
                    } else {
                        player.consume((Consumable) itemPane.getItem());
                        info.setText("Consumed " + itemPane.getItem().getSimpleName());
                        sortItems();
                        consumablesScroll.setContent(drawConsumables());
                        favoritesScroll.setContent(drawFavorites());
                    }
                }
                drawColors(allLabels);
                if (itemPane.getItem().isFavorite()) drawColors(favLabels);
            } else if(event.getButton() == MouseButton.SECONDARY){ //Right Click
                if(itemPane.getItem().isFavorite()) {
                    itemPane.getItem().setFavorite(false);
                    if(itemPane.getItem() instanceof Armor) {
                        armorScroll.setContent(drawArmors());
                        drawColors(armorLabels);
                    }
                    else if(itemPane.getItem() instanceof Weapon) {
                        weaponsScroll.setContent(drawWeapons());
                        drawColors(wepLabels);
                    }
                    else if(itemPane.getItem() instanceof Consumable) {
                        consumablesScroll.setContent(drawConsumables());
                        drawColors(consumableLabels);
                    }
                } else {
                    itemPane.getItem().setFavorite(true);
                    itemPane.getLabel().setText(itemPane.getLabel().getText() + " ★");
                }

                sortItems();
                favoritesScroll.setContent(drawFavorites());
                allScroll.setContent(drawAll());
                drawColors(favLabels);
                drawColors(allLabels);
            }
        }
    }
}
