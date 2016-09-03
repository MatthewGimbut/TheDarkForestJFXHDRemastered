package gui;

import characters.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import main.GameStage;
import sun.plugin.javascript.navig.Anchor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ScrollingInventoryPane extends BorderPane {

    private GamePane currentView;
    private Player player;
    @FXML AnchorPane pane;
    @FXML private Label title;
    @FXML private ResourceBundle resources;
    @FXML private URL location;


    public ScrollingInventoryPane(GamePane currentView, Player player) {
        this.currentView = currentView;
        this.player = player;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\ScrollingInventoryPane.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {
        assert title != null : "fx:id=\"title\" was not injected: check your FXML file 'ScrollingInventoryPane.fxml'.";
        System.out.println("test");
        System.out.println(title.getText());
        this.setCenter(pane);
    }
}
