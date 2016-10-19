package gui;

import characters.Enemy;
import characters.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import main.GameStage;
import sprites.NPC;

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
    public static final String ENEMY = "enemy";
    private NPC enemy;

    public StatusPeekPane(GamePane currentView, String display) {
        this.currentView = currentView;
        this.display = display;
        loadFXML();
    }

    public StatusPeekPane(GamePane currentView, String display, NPC enemy) {
        this.currentView = currentView;
        this.display = display;
        this.enemy = enemy;
        loadFXML();
    }

    private void loadFXML() {
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
        } else if(display.equals("stamina")) {
            bar.setStyle(GamePane.STYLE_STAMINA);
        }

        borderImage.setImage(new Image("file:Images\\UI\\bordertest.png"));

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
                break;
            case "stamina":
                stat.setText("Stamina: ");
                total.setText(p.getCurrentStamina() + "/" + p.getMaxStamina());
                bar.setProgress((p.getCurrentStamina()+0.0)/(p.getMaxStamina()+0.0));
                break;
            case "enemy":
                currentView.setMargin(this, new Insets(enemy.getY()-15,
                        GameStage.WINDOW_WIDTH-enemy.getX()-(enemy.getWidth()*2),
                        GameStage.WINDOW_HEIGHT-enemy.getY()+15,
                        enemy.getX()-(enemy.getWidth())));
                //top right bottom left
                stat.setText("");
                total.setText("");
                bar.setProgress((enemy.getNPC().getCurrentHP()+0.0)/(enemy.getNPC().getMaxHP()+0.0));
                bar.setStyle(currentView.getDefaultHealthAccentColor(enemy.getNPC()));
                break;
        }
        this.setVisible(true);
        this.setOpacity(1.0);
    }

    public NPC getEnemy() {
        return this.enemy;
    }
}
