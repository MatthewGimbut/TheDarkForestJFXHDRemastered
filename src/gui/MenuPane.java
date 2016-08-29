package gui;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.GameStage;
import main.MapContainer;
import main.SaveManager;
import sprites.PlayerSprite;
import java.util.ArrayList;

public class MenuPane extends VBox {

    private GameButton options, stats, inventory, exit, quit, load;
    private MapContainer map;


    public MenuPane(GamePane currentView) {
        this.setId("standardPane");
        this.setAlignment(Pos.CENTER);
        options = new GameButton("Options");
        stats = new GameButton("Stats");
        inventory = new GameButton("Inventory");
        load = new GameButton("Load");
        quit = new GameButton("Quit");
        exit = new GameButton("Exit");


        exit.setOnAction(event -> {
            currentView.toggleMenuPane();
        });

        load.setOnAction(event -> {
            currentView.toggleMenuPane();
            ArrayList<Object> newMap = SaveManager.deserialize();
            currentView.setCurrentMapFile((String) newMap.get(1));
            currentView.setPlayer((PlayerSprite) newMap.get(0));
            currentView.getPlayer().setImage(currentView.getPlayer().getImageLocation());
            try {
                map = new MapContainer(currentView.getPlayer(), (String) newMap.get(1));
                currentView.setMapContainer(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentView.setId((String) newMap.get(2));
        });

        quit.setOnAction(event -> {
            Stage stage = currentView.getPrimaryStage();
            MainMenuPane pane = new MainMenuPane(stage);
            Scene newScene = new GameScene(pane, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
            stage.setScene(newScene);
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

        this.setSpacing(7.5);
        this.setMaxWidth(125);
        this.setMaxHeight(300);
        this.getChildren().addAll(inventory, stats, options, load, exit,  quit);
        this.requestFocus();
    }
}
