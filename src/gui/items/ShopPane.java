package gui.items;

import gui.GamePane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import main.AudioManager;
import main.GameStage;
import sprites.NPC;
import java.io.IOException;
import java.net.URL;
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
    @FXML private GridPane statGrid;

    private GamePane currentView;
    private NPC shopkeeper;

    public ShopPane(GamePane currentView, NPC shopkeeper) {
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
        playerLabel.setText(currentView.getMainPlayerSprite().getPlayer().getName());
        shopkeeperLabel.setText(shopkeeper.getNPC().getName());

        this.setCenter(anchor);

        Rectangle rekt = new Rectangle(anchor.getPrefWidth(), anchor.getPrefHeight());
        rekt.setArcHeight(GamePane.ARC_SIZE);
        rekt.setArcWidth(GamePane.ARC_SIZE);
        anchor.setClip(rekt);

        AudioManager.getInstance().playSound(AudioManager.MENU_OPEN);
    }
}
