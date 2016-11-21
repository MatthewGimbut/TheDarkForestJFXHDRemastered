package gui.items;

import characters.Merchant;
import characters.Player;
import gui.GamePane;
import items.Armor.Armor;
import items.Armor.Shield;
import items.Consumables.Consumable;
import items.Consumables.Potion;
import items.Item;
import items.Weapons.Magic;
import items.Weapons.Weapon;
import items.accessories.Accessory;
import items.ammunition.Ammunition;
import items.ammunition.Arrow;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import main.AudioManager;
import main.GameStage;
import sprites.DisplayItem;
import sprites.NPC;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ShopPane extends BorderPane {

    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private AnchorPane anchor;
    @FXML private Label description;
    @FXML private Button exit;
    @FXML private ImageView itemImage;
    @FXML private Label itemName;
    @FXML private Label message;
    @FXML private Tab playerLabel;
    @FXML private ScrollPane playerScroll;
    @FXML private Tab shopkeeperLabel;
    @FXML private ScrollPane shopkeeperPane;
    @FXML private Label stat00;
    @FXML private Label stat01;
    @FXML private Label stat10;
    @FXML private Label stat11;
    @FXML private Label stat20;
    @FXML private Label stat21;
    @FXML private Label stat30;
    @FXML private Label stat31;
    @FXML private GridPane statGrid;
    @FXML private Label playerGold;
    @FXML private Label shopGold;

    private GamePane currentView;
    private Merchant shopkeeper;

    public ShopPane(GamePane currentView, Merchant shopkeeper) {
        this.currentView = currentView;
        this.shopkeeper = shopkeeper;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\items\\ShopPane.fxml"));
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
        playerLabel.setText(currentView.getMainPlayerSprite().getPlayer().getName() + "'s Items");
        shopkeeperLabel.setText(shopkeeper.getName() + "'s items");

        exit.setOnAction(event -> currentView.uiManager.removeShopPane(this));

        playerScroll.setContent(fillPlayerTab());
        shopkeeperPane.setContent(fillShopTab());

        resetMoneyLabels();

        this.setCenter(anchor);

        Rectangle rekt = new Rectangle(anchor.getPrefWidth(), anchor.getPrefHeight());
        rekt.setArcHeight(GamePane.ARC_SIZE);
        rekt.setArcWidth(GamePane.ARC_SIZE);
        anchor.setClip(rekt);

        message.setFont(new Font("Cambria", 18));
        message.setText(shopkeeper.getMerchantType().toString() + " wares");

        AudioManager.getInstance().playSound(AudioManager.MENU_OPEN);
    }

    private VBox fillPlayerTab() {
        playerScroll.setContent(null);
        VBox playerBox = new VBox(5);
        Player p = currentView.getMainPlayerSprite().getPlayer();

        for(Item i : p.getInventory()) {
            TextItemPane label = new TextItemPane(i,p); //lol
            label.setId("inventoryItem");
            label.setOnMouseClicked(new SellHandler(label));
            label.setOnMouseEntered(new DisplayHandler(label));
            playerBox.setMargin(label, new Insets(2, 0, 2, 5));
            playerBox.getChildren().add(label);
        }

        return  playerBox;
    }

    private VBox fillShopTab() {
        shopkeeperPane.setContent(null);
        VBox shopBox = new VBox(5);

        for(Item i : shopkeeper.getInventory()) {
            TextItemPane label = new TextItemPane(i, shopkeeper);
            label.setId("inventoryItem");
            label.setOnMouseClicked(new BuyHandler(label));
            label.setOnMouseEntered(new DisplayHandler(label));
            shopBox.setMargin(label, new Insets(2, 0, 2, 5));
            shopBox.getChildren().add(label);
        }

        return shopBox;
    }

    void resetMoneyLabels() {
        shopGold.setText(shopkeeper.getName() + ": " + shopkeeper.getGold() + " gold");
        playerGold.setText(currentView.getMainPlayerSprite().getPlayer().getName() + ": " +
                currentView.getMainPlayerSprite().getPlayer().getGold() + " gold");
    }

    private class BuyHandler implements EventHandler<MouseEvent> {

        private TextItemPane label;

        BuyHandler(TextItemPane label) { this.label = label; }

        @Override
        public void handle(MouseEvent event) {
            Player p = currentView.getMainPlayerSprite().getPlayer();
            if(label.getItem().getValue() <= p.getGold()) {
                if(label.getItem().getWeight() <= (p.getCarryCap() - p.getCurrentCarry())) {
                    p.addItem(label.getItem());
                    p.modifyGold(-label.getItem().getValue());
                    shopkeeper.modifyGold(label.getItem().getValue());
                    shopkeeper.removeSingleItem(label.getItem());
                    playerScroll.setContent(fillPlayerTab());
                    shopkeeperPane.setContent(fillShopTab());
                    resetMoneyLabels();
                    message.setText("You have purchased " + label.getItem().getSimpleName()
                            + " for " + label.getItem().getValue() + " gold.");
                } else {
                    message.setText("You don't have enough inventory space!");
                }
            } else {
                message.setText("You don't have enough gold!");
            }
        }
    }

    private class SellHandler implements EventHandler<MouseEvent> {

        private TextItemPane label;

        SellHandler(TextItemPane label) { this.label = label; }

        @Override
        public void handle(MouseEvent event) {
            if(shopkeeper.getGold() >= label.getItem().getValue()) {
                Player p = currentView.getMainPlayerSprite().getPlayer();
                shopkeeper.addItem(label.getItem());
                shopkeeper.modifyGold(-label.getItem().getValue());
                p.modifyGold(label.getItem().getValue());
                p.removeSingleItem(label.getItem());
                playerScroll.setContent(fillPlayerTab());
                shopkeeperPane.setContent(fillShopTab());
                resetMoneyLabels();
                message.setText("You have sold " + label.getItem().getSimpleName() + " for " + label.getItem().getValue() + " gold.");
            } else {
                message.setText("Shopkeeper doesn't have enough money!");
            }

        }
    }

    private class DisplayHandler implements EventHandler {

        private TextItemPane pane;

        DisplayHandler(TextItemPane pane) { this.pane = pane; }

        @Override
        public void handle(Event event) {
            Item i = pane.getItem();
            if (i instanceof Magic) {
                stat00.setText("Attack: " + i.getAtk());
                stat01.setText("Magic: " + i.getMagic());
                stat10.setText("Mana boost: " + i.getManaBoost());
                stat11.setText("Mana cost: " + ((Magic) i).getManaCost());
                stat20.setText("Cooldown: " + i.getCooldown());
                stat21.setText("Element: " + ((Magic) i).getSpellType());
            } else if (i instanceof Ammunition) {
                stat00.setText("Damage: " + ((Ammunition) i).getDamage());
                stat01.setText("Indiv. Weight: " + ((Ammunition) i).getIndividualWeight());
                stat10.setText("Indiv. Value: " + ((Ammunition) i).getIndividualValue());
                stat11.setText("Amount: " + ((Ammunition) i).getCount());
                stat20.setText("");
                stat21.setText("");
            } else if (i instanceof Accessory) {
                stat00.setText("CD Reduction: " + -((Accessory) i).getCooldownReduction());
                stat01.setText("Mana Regen: " + -((Accessory) i).getManaRegenBoost());
                stat10.setText("HP Regen: " + -((Accessory) i).getHpRegenBoost());
                stat11.setText("Stamina Regen: " + -((Accessory) i).getStaminaRegenBoost());
                stat20.setText("HP Boost: " + i.getHpBoost());
                stat21.setText("Mana Boost: " + i.getManaBoost());
            } else if (i instanceof Weapon || i instanceof Shield) {
                stat00.setText("Attack: " + i.getAtk());
                stat01.setText("Defense: " + i.getDef());
                stat10.setText("Magic: " + i.getMagic());
                stat11.setText("Cooldown: " + i.getCooldown());
                stat20.setText("");
                stat21.setText("");
            } else if (i instanceof Armor) {
                stat00.setText("Attack: " + i.getAtk());
                stat01.setText("Defense: " + i.getDef());
                stat10.setText("Magic: " + i.getMagic());
                stat11.setText("");
                stat20.setText("");
                stat21.setText("");
                //TODO Maybe add more details? idk
            } else if (i instanceof Potion) {
                stat00.setText("Type: " + ((Potion) i).getType());
                stat01.setText("Amount: " + ((Potion) i).getAmount());
                stat10.setText("");
                stat11.setText("");
                stat20.setText("");
                stat21.setText("");
            }

            itemImage.setImage(pane.getItemImage());
            itemName.setText(pane.getItem().getSimpleName());
            itemName.setTextFill(ScrollingInventoryPane.determineNameColor(i));
            stat30.setText("Weight: " + i.getWeight());
            stat31.setText(i.getValue() + " gold");
            description.setText(i.getItemToolTipText());
        }
    }
}
