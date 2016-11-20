package gui.menus;

import gui.GamePane;
import gui.GameScene;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import main.AudioManager;
import main.GameStage;
import mapping.MapContainer;
import main.SaveManager;
import sprites.PlayerSprite;

import java.io.IOException;
import java.io.InvalidClassException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MenuPane extends BorderPane {

    private GamePane currentView;
    private MapContainer map;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private Button exit;
    @FXML private Button inventory;
    @FXML private Button load;
    @FXML private Button log;
    @FXML private Button options;
    @FXML private Button quit;
    @FXML private Button stats;
    @FXML private AnchorPane anchor;


    public MenuPane(GamePane currentView) {
        this.currentView = currentView;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\menus\\Menu.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            GameStage.logger.error(exception.getMessage());
            GameStage.logger.error(exception);
        }
    }

    @FXML void initialize() {

        exit.setOnAction(event -> {
            currentView.uiManager.toggleMenuPane();
        });

        load.setOnAction(event -> {
            currentView.uiManager.toggleMenuPane();
            System.out.println(currentView.saveLoc);
            ArrayList<Object> newMap = new ArrayList<>();
            try {
                newMap = SaveManager.deserialize(currentView.saveLoc);
            } catch (InvalidClassException e) {
                System.out.println("Unable to load save, invalid version.");
            }
            currentView.setCurrentMapFile((String) newMap.get(1));
            currentView.setPlayer((PlayerSprite) newMap.get(0));
            currentView.getMainPlayerSprite().setImage(currentView.getMainPlayerSprite().getImageLocation());
            try {
                map = new MapContainer(currentView.getMainPlayerSprite(), (String) newMap.get(1));
                currentView.setMapContainer(map);
            } catch (Exception e) {
                GameStage.logger.error(e);
            }
            currentView.setId((String) newMap.get(2));
            currentView.fillEnemies();
        });

        quit.setOnAction(event -> {
            currentView.uiManager.toggleMenuPane();
            currentView.uiManager.showConfirmQuitPane();
        });

        options.setOnAction( event -> {
            currentView.uiManager.toggleMenuPane();
            currentView.uiManager.displayOptionsPane();
        });

        inventory.setOnAction(event -> {
            currentView.uiManager.toggleMenuPane();
            currentView.uiManager.displayInventoryPane();
        });

        stats.setOnAction(event -> {
            currentView.uiManager.toggleMenuPane();
            currentView.uiManager.displayStatsPane();
        });

        log.setOnAction(event -> {
            currentView.uiManager.toggleMenuPane();
            currentView.uiManager.displayJournal();
        });


        this.setCenter(anchor);
        Rectangle rekt = new Rectangle(anchor.getPrefWidth(), anchor.getPrefHeight());
        rekt.setArcHeight(GamePane.ARC_SIZE);
        rekt.setArcWidth(GamePane.ARC_SIZE);
        anchor.setClip(rekt);
        this.requestFocus();
    }
}
