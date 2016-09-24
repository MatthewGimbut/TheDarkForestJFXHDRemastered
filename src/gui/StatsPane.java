package gui;

import characters.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import sprites.PlayerSprite;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StatsPane extends BorderPane {

    private GamePane currentView;
    private PlayerSprite playerSprite;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private AnchorPane anchor;
    @FXML private ImageView bootsImage;
    @FXML private ImageView chestImage;
    @FXML private Button exit;
    @FXML private ProgressBar xpBar;
    @FXML private Label xpLabel;
    @FXML private ImageView glovesImage;
    @FXML private ProgressBar healthBar;
    @FXML private Label healthLabel;
    @FXML private ImageView helmetImage;
    @FXML private ImageView legsImage;
    @FXML private ProgressBar manaBar;
    @FXML private Label manaLabel;
    @FXML private Label playerAttack;
    @FXML private Label playerDefense;
    @FXML private ImageView playerImage;
    @FXML private Label playerLevel;
    @FXML private Label playerMagic;
    @FXML private Label playerName;
    @FXML private ImageView shieldImage;
    @FXML private ImageView spellImage;
    @FXML private ImageView weaponImage;

    public StatsPane(GamePane currentView, PlayerSprite playerSprite) {
        this.currentView = currentView;
        this.playerSprite = playerSprite;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\StatsPane.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    @FXML
    void initialize() {

        exit.setOnAction(event -> currentView.removeStatsPane(this));

        Player player = playerSprite.getPlayer();

        playerName.setText(player.getName());

        playerAttack.setText("Attack: " + player.getAtk());
        playerDefense.setText("Defense: " + player.getDef());
        playerMagic.setText("Magic: " + player.getMagic());
        playerLevel.setText("Level: " + player.getLvl());

        healthLabel.setText("Health: " + player.getCurrentHP() + "/" + player.getMaxHP());
        healthBar.setProgress(player.getCurrentHP()/player.getMaxHP());
        xpLabel.setText("XP: " + player.getXp() + "/100");
        xpBar.setProgress(player.getXp()/100.0);
        manaLabel.setText("Mana: " + player.getCurrentMana() + "/" + player.getMaxMana());
        manaBar.setProgress(player.getCurrentMana()/player.getMaxMana());

        if(player.getWeaponHandR() != null) {
            weaponImage.setImage(new Image(player.getWeaponHandR().getImageLocation()));
            Tooltip.install(weaponImage, new Tooltip(player.getWeaponHandR().getItemToolTipText()));
        }

        if(player.getLeftHand() != null) {
            shieldImage.setImage(new Image(player.getLeftHand().getImageLocation()));
            Tooltip.install(shieldImage, new Tooltip(player.getLeftHand().getItemToolTipText()));
        }

        if(player.getHelmet() != null) {
            helmetImage.setImage(new Image(player.getHelmet().getImageLocation()));
            Tooltip.install(helmetImage, new Tooltip(player.getHelmet().getItemToolTipText()));
        }

        //TODO
        /*
               if(player.getSpellstuff){
                do stuff
               }
         */

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

        playerImage.setImage(new Image(this.playerSprite.getImageLocation()));

        this.setCenter(anchor);
    }
}
