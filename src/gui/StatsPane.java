package gui;

import characters.Player;
import items.Item;
import items.TwoHanded;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import main.AudioManager;
import main.GameStage;
import sprites.PlayerSprite;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StatsPane extends BorderPane {

    private GamePane currentView;
    private PlayerSprite playerSprite;
    private final String BORDER = "file:Images\\UI\\border_long.png";
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private ImageView accessory1Image;
    @FXML private ImageView accessory2Image;
    @FXML private ImageView accessory3Image;
    @FXML private ImageView ammoImage;
    @FXML private AnchorPane anchor;
    @FXML private ImageView bootsImage;
    @FXML private ImageView chestImage;
    @FXML private Button exit;
    @FXML private ImageView glovesImage;
    @FXML private Label gold;
    @FXML private ProgressBar healthBar;
    @FXML private Label healthLabel;
    @FXML private ImageView helmetImage;
    @FXML private ImageView hpBorder;
    @FXML private ImageView legsImage;
    @FXML private ProgressBar manaBar;
    @FXML private ImageView manaBorder;
    @FXML private Label manaLabel;
    @FXML private Label playerAttack;
    @FXML private Label playerDefense;
    @FXML private ImageView playerImage;
    @FXML private Label playerLevel;
    @FXML private Label playerMagic;
    @FXML private Label playerName;
    @FXML private ImageView primaryImage;
    @FXML private ImageView secondaryImage;
    @FXML private Label secondaryLabel;
    @FXML private Rectangle secondaryPane;
    @FXML private ProgressBar staminaBar;
    @FXML private Label staminaLabel;
    @FXML private ProgressBar xpBar;
    @FXML private ImageView xpBorder;
    @FXML private ImageView staminaBorder;
    @FXML private Label xpLabel;

    public StatsPane(GamePane currentView, PlayerSprite playerSprite) {
        this.currentView = currentView;
        this.playerSprite = playerSprite;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\StatsPane.fxml"));
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

        exit.setOnAction(event -> currentView.uiManager.removeStatsPane(this));

        this.setOnKeyReleased((KeyEvent key) -> {
            String code = key.getCode().toString();
            if(code.equals("ESCAPE")) exit.fire();
        });

        Player player = playerSprite.getPlayer();

        hpBorder.setImage(new Image(BORDER));
        manaBorder.setImage(new Image(BORDER));
        xpBorder.setImage(new Image(BORDER));
        staminaBorder.setImage(new Image(BORDER));

        playerName.setText(player.getName());

        playerAttack.setText("Attack: " + player.getAtk());
        playerDefense.setText("Defense: " + player.getDef());
        playerMagic.setText("Magic: " + player.getMagic());
        playerLevel.setText("Level: " + player.getLvl());

        healthLabel.setText("Health: " + player.getCurrentHP() + "/" + player.getMaxHP());
        healthBar.setProgress((player.getCurrentHP()+0.0)/(player.getMaxHP()+0.0));
        healthBar.setStyle(currentView.getPlayerHealthAccentColor());
        xpLabel.setText("XP: " + player.getXp() + "/100");
        xpBar.setProgress(player.getXp()/100.0);
        manaLabel.setText("Mana: " + player.getCurrentMana() + "/" + player.getMaxMana());
        manaBar.setProgress((player.getCurrentMana()+0.0)/(player.getMaxMana()+0.0));
        staminaLabel.setText("Stamina: " + player.getCurrentStamina() + "/" + player.getMaxStamina());
        staminaBar.setProgress((player.getCurrentStamina()+0.0)/(player.getMaxStamina()+0.0));
        staminaBar.setStyle(GamePane.STYLE_STAMINA);
        xpBar.setStyle(GamePane.STYLE_XP);

        gold.setText(player.getGold() + " gold");

        if(player.getWeaponHandR() != null) {
            primaryImage.setImage(new Image(player.getWeaponHandR().getImageLocation()));
            Tooltip.install(primaryImage, new Tooltip(player.getWeaponHandR().getItemToolTipText()));
            if(player.getWeaponHandR() instanceof TwoHanded) {
                secondaryLabel.setOpacity(.50);
                secondaryImage.setOpacity(.50);
            }
        }

        if(player.getLeftHand() != null) {
            secondaryImage.setImage(new Image(((Item) player.getLeftHand()).getImageLocation()));
            Tooltip.install(secondaryImage, new Tooltip(((Item) player.getLeftHand()).getItemToolTipText()));
        }

        if(player.getHelmet() != null) {
            helmetImage.setImage(new Image(player.getHelmet().getImageLocation()));
            Tooltip.install(helmetImage, new Tooltip(player.getHelmet().getItemToolTipText()));
        }

        if(player.getAmmo() != null) {
            ammoImage.setImage(new Image(player.getAmmo().getImageLocation()));
            Tooltip.install(ammoImage, new Tooltip(player.getAmmo().getItemToolTipText()));
        }

        if(player.getGloves() != null) {
            glovesImage.setImage(new Image(player.getGloves().getImageLocation()));
            Tooltip.install(glovesImage, new Tooltip(player.getGloves().getItemToolTipText()));
        }

        if(player.getChestPiece() != null) {
            chestImage.setImage(new Image(player.getChestPiece().getImageLocation()));
            Tooltip.install(chestImage, new Tooltip(player.getChestPiece().getItemToolTipText()));
        }

        if(player.getBoots() != null) {
            bootsImage.setImage(new Image(player.getBoots().getImageLocation()));
            Tooltip.install(bootsImage, new Tooltip(player.getBoots().getItemToolTipText()));
        }

        if(player.getLeggings() != null) {
            legsImage.setImage(new Image(player.getLeggings().getImageLocation()));
            Tooltip.install(legsImage, new Tooltip(player.getLeggings().getItemToolTipText()));
        }

        if(player.getNecklace() != null) {
            accessory1Image.setImage(new Image(player.getNecklace().getImageLocation()));
            Tooltip.install(accessory1Image, new Tooltip(player.getNecklace().getItemToolTipText()));
        }

        if(player.getRing2() != null) {
            accessory2Image.setImage(new Image(player.getRing2().getImageLocation()));
            Tooltip.install(accessory2Image, new Tooltip(player.getRing2().getItemToolTipText()));
        }

        if(player.getRing1() != null) {
            accessory3Image.setImage(new Image(player.getRing1().getImageLocation()));
            Tooltip.install(accessory3Image, new Tooltip(player.getRing1().getItemToolTipText()));
        }

        playerImage.setImage(new Image(Player.FACING_SOUTH));

        this.setCenter(anchor);

        Rectangle rekt = new Rectangle(anchor.getPrefWidth(), anchor.getPrefHeight());
        rekt.setArcHeight(GamePane.ARC_SIZE);
        rekt.setArcWidth(GamePane.ARC_SIZE);
        anchor.setClip(rekt);
        AudioManager.getInstance().playSound(AudioManager.MENU_OPEN);
    }
}
