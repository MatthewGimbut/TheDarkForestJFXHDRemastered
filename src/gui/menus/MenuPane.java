package gui.menus;


import gui.GamePane;
import gui.GameScene;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.AudioManager;
import main.GameStage;
import mapping.MapContainer;
import main.SaveManager;
import sprites.PlayerSprite;
import java.util.ArrayList;

public class MenuPane extends VBox {

    private Button options, stats, inventory, exit, quit, load, log;
    private MapContainer map;


    public MenuPane(GamePane currentView) {
        this.setId("standardPane");
        this.setAlignment(Pos.CENTER);
        options = new Button("Options");
        stats = new Button("Stats");
        inventory = new Button("Inventory");
        load = new Button("Load");
        quit = new Button("Quit");
        exit = new Button("Exit");
        log = new Button("Journal");


        exit.setOnAction(event -> {
            currentView.toggleMenuPane();
        });

        load.setOnAction(event -> {
            currentView.toggleMenuPane();
            ArrayList<Object> newMap = SaveManager.deserialize(currentView.saveLoc);
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
            currentView.toggleMenuPane();
            currentView.showConfirmQuitPane();
        });

        options.setOnAction( event -> {
            currentView.toggleMenuPane();
            currentView.displayOptionsPane();
        });

        inventory.setOnAction(event -> {
            currentView.toggleMenuPane();
            currentView.displayInventoryPane();
        });

        stats.setOnAction(event -> {
            currentView.toggleMenuPane();
            currentView.displayStatsPane();
        });

        log.setOnAction(event -> {
            currentView.toggleMenuPane();
            currentView.displayJournal();
        });

        this.setSpacing(7.5);
        this.setMaxWidth(125);
        this.setMaxHeight(375);
        this.getChildren().addAll(inventory, stats, log, options, load, exit,  quit);
        this.requestFocus();
    }
}
