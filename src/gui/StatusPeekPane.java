package gui;

import characters.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import main.GameStage;

import java.net.URL;
import java.util.ResourceBundle;

public class StatusPeekPane extends AnchorPane {

    private GamePane currentView;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private AnchorPane anchor;
    @FXML private Label hp;
    @FXML private ProgressBar hpBar;
    @FXML private Label mana;
    @FXML private ProgressBar manaBar;

    public StatusPeekPane(GamePane currentView) {
        this.currentView = currentView;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\StatusPeekPane.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (Exception exception) {
            GameStage.logger.error(exception.getMessage());
            GameStage.logger.error(exception);
            GameStage.logger.error(exception.getStackTrace());
        }
    }

    @FXML
    void initialize() {
        update();
        this.getChildren().add(anchor);
    }

    public void update() {
        Player p = currentView.getMainPlayerSprite().getPlayer();
        hp.setText(p.getCurrentHP() + "/" + p.getMaxHP());
        mana.setText(p.getCurrentMana() + "/" + p.getMaxMana());
        manaBar.setProgress((p.getCurrentMana()+0.0)/(p.getMaxMana()+0.0));
        hpBar.setProgress((p.getCurrentHP()+0.0)/(p.getMaxHP()+0.0));
        hpBar.setStyle(currentView.getPlayerHealthAccentColor());
        this.setVisible(true);
    }
}
