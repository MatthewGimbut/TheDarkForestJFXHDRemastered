package gui.items;

import gui.GamePane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import main.GameStage;
import sprites.NPC;
import java.io.IOException;

public class ShopPane extends BorderPane {

    private GamePane currentView;
    private NPC shopkeeper;

    public ShopPane(GamePane currentView, NPC shopkeeper) {
        this.currentView = currentView;
        this.shopkeeper = shopkeeper;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\quests\\NewQuestPane.fxml"));
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

    }
}
