package gui.items;

import characters.Player;
import gui.GameButton;
import gui.GameLabel;
import items.Armor.*;
import items.Item;
import items.Weapons.Weapon;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

@Deprecated
public class EquipmentPane extends VBox {

    private GameLabel title;
    private Player player;
    private GameButton exit;
    private TilePane center;
    private final String PLACEHOLDER_IMAGE = "file:Images\\NotBlank40x40.png";
    private InventoryPane invPane;

    public EquipmentPane(Player player, InventoryPane invPane) {
        this.invPane = invPane;
        this.player = player;
        title = new GameLabel(player.getName()+"'s equipment", 16);

        //this.setId("neonTest");

        HBox topAlign = new HBox();
        topAlign.getChildren().add(title);
        topAlign.setAlignment(Pos.CENTER);

        HBox bottomAlign = new HBox();
        //bottomAlign.getChildren().add(exit);
        bottomAlign.setAlignment(Pos.CENTER);

        center = new TilePane(10, 40);
        initPanes();
        //this.setTop(topAlign);
        //this.setBottom(bottomAlign);
        this.setFillWidth(false);
        this.getChildren().add(center);
        this.setMaxWidth(350);
        this.setMaxHeight(300);
        this.setMinHeight(300);
        this.setMinWidth(350);
    }

    private void initPanes() {
        center.setHgap(10);
        center.setVgap(40);
        center.setPrefColumns(3);
        center.setPrefRows(3);

        Helmet helmet = player.getHelmet();
        ChestPiece chest = player.getChestPiece();
        Legs legs = player.getLeggings();
        Gloves gloves = player.getGloves();
        Boots boots = player.getBoots();
        Shield shield = player.getLeftHand();
        Weapon weapon = player.getWeaponHandR();
        //TODO When spells are added, add section for spell tome here

        //TODO If spell tome is not equal to null

        center.getChildren().add(new ImageView(PLACEHOLDER_IMAGE));

        if(helmet != null) {
            ItemPane ip = new ItemPane(helmet, player);
            ip.setOnMouseClicked(new UnequipHandler(helmet));
            center.getChildren().add(ip);
        } else {
            center.getChildren().add(new ImageView(PLACEHOLDER_IMAGE));
        }

        if(chest != null) {
            ItemPane ip = new ItemPane(chest, player);
            ip.setOnMouseClicked(new UnequipHandler(chest));
            center.getChildren().add(ip);
        } else {
            center.getChildren().add(new ImageView(PLACEHOLDER_IMAGE));
        }

        if(legs != null) {
            ItemPane ip = new ItemPane(legs, player);
            ip.setOnMouseClicked(new UnequipHandler(legs));
            center.getChildren().add(ip);
        } else {
            center.getChildren().add(new ImageView(PLACEHOLDER_IMAGE));
        }

        center.getChildren().add(new ImageView(player.FACING_SOUTH));

        if(gloves != null) {
            ItemPane ip = new ItemPane(gloves, player);
            ip.setOnMouseClicked(new UnequipHandler(gloves));
            center.getChildren().add(ip);
        } else {
            center.getChildren().add(new ImageView(PLACEHOLDER_IMAGE));
        }

        if(boots != null) {
            ItemPane ip = new ItemPane(boots, player);
            ip.setOnMouseClicked(new UnequipHandler(boots));
            center.getChildren().add(ip);
        } else {
            center.getChildren().add(new ImageView(PLACEHOLDER_IMAGE));
        }

        if(shield != null) {
            ItemPane ip = new ItemPane(shield, player);
            ip.setOnMouseClicked(new UnequipHandler(shield));
            center.getChildren().add(ip);
        } else {
            center.getChildren().add(new ImageView(PLACEHOLDER_IMAGE));
        }

        if(weapon != null) {
            ItemPane ip = new ItemPane(weapon, player);
            ip.setOnMouseClicked(new UnequipHandler(weapon));
            center.getChildren().add(ip);
        } else {
            center.getChildren().add(new ImageView(PLACEHOLDER_IMAGE));
        }
    }

    public void redraw() {
        center.getChildren().clear();
        initPanes();
    }

    private class UnequipHandler implements EventHandler<MouseEvent> {

        private Item i;

        public UnequipHandler(Item i) { this.i = i; }

        @Override
        public void handle(MouseEvent event) {
            if(event.getButton() == MouseButton.SECONDARY) {
                player.unequip(i);
                redraw();
                if(i instanceof  Armor) {
                    invPane.displayArmors();
                } else {
                    invPane.displayWeapons();
                }
                if(i.isFavorite()) invPane.displayFavorites();
            }
        }
    }
}
