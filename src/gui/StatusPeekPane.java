package gui;

import characters.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML private Label total;
    @FXML private Label stat;
    @FXML private ProgressBar bar;
    @FXML private ImageView borderImage;
    private String display;
    public static final String HEALTH = "health";
    public static final String MANA = "mana";
    public static final String XP = "xp";
    public static final String STAMINA = "stamina";

    public StatusPeekPane(GamePane currentView, String display) {
        this.currentView = currentView;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\StatusPeekPane.fxml"));
        fxmlLoader.setController(this);

        this.display = display;
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

        if(display.equals("mana")) {
            bar.setStyle(GamePane.STYLE_MANA);
        }

        borderImage.setImage(new Image("file:Images\\bordertest.png"));

        this.getChildren().add(anchor);
    }

    public void update() {
        Player p = currentView.getMainPlayerSprite().getPlayer();

        switch(display) {
            case "health":
                stat.setText("HP: ");
                total.setText(p.getCurrentHP() + "/" + p.getMaxHP());
                bar.setProgress((p.getCurrentHP()+0.0)/(p.getMaxHP()+0.0));
                bar.setStyle(currentView.getPlayerHealthAccentColor());
                break;
            case "mana":
                stat.setText("Mana: ");
                total.setText(p.getCurrentMana() + "/" + p.getMaxMana());
                bar.setProgress((p.getCurrentMana()+0.0)/(p.getMaxMana()+0.0));
                break;
            case "xp":
                stat.setText("XP: ");
                total.setText(p.getXp() + "/100");
                bar.setProgress((p.getXp()+0.0)/100.0);
        }
        this.setVisible(true);
        this.setOpacity(1.0);
    }
}
