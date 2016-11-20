package gui.menus;

import characters.Player;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import gui.GamePane;
import gui.GameScene;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.util.Duration;
import main.GameStage;
import main.SaveManager;
import mapping.MapContainer;
import quests.QuestHandler;
import sprites.PlayerSprite;

import javax.tools.Tool;
import java.io.InvalidClassException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainMenuPane extends BorderPane {

    public static final String SAVE_01 = "Saves\\save01.ser";
    public static final String SAVE_02 = "Saves\\save02.ser";
    public static final String SAVE_03 = "Saves\\save03.ser";
    private Stage primaryStage;

    @FXML private ResourceBundle resources;
    @FXML private AnchorPane pane;
    @FXML private URL location;
    @FXML private Button continue1;
    @FXML private Button continue2;
    @FXML private Button continue3;
    @FXML private Button newGame1;
    @FXML private Button newGame2;
    @FXML private Button newGame3;
    @FXML private ImageView saveImage1;
    @FXML private ImageView saveImage2;
    @FXML private ImageView saveImage3;
    @FXML private ImageView saveImage4;
    @FXML private ImageView saveImage5;
    @FXML private ImageView saveImage6;
    @FXML private ImageView optionsImage;
    @FXML private Label title1;
    @FXML private Label title2;
    @FXML private Label title3;
    @FXML private Label info1;
    @FXML private Label info2;
    @FXML private Label info3;


    public MainMenuPane(Stage primaryStage) {
        this.primaryStage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "gui\\Menus\\MainMenu.fxml"));
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (Exception exception) {
            GameStage.logger.error(exception.getMessage());
            GameStage.logger.error(exception);
            GameStage.logger.error(exception.getStackTrace());
        }


        Group g = new Group();
        Rectangle rekt = new Rectangle(GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
        rekt.setFill(Color.BLACK);
        FadeTransition ft = new FadeTransition(Duration.millis(1500), g);
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.setOnFinished(event -> {
            getChildren().remove(g);
        });
        g.getChildren().addAll(rekt);
        this.getChildren().add(g);
        ft.play();
    }

    @FXML void initialize() {
        String saveLoc = "file:Images\\SaveArea.png";
        saveImage1.setImage(new Image(saveLoc));
        saveImage2.setImage(new Image(saveLoc));
        saveImage3.setImage(new Image(saveLoc));
        saveImage4.setImage(new Image(saveLoc));
        saveImage5.setImage(new Image(saveLoc));
        saveImage6.setImage(new Image(saveLoc));
        optionsImage.setImage(new Image("file:Images\\UI\\gears.png"));

        RotateTransition rotation = new RotateTransition(Duration.seconds(0.5), optionsImage);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.setByAngle(360);
        optionsImage.setOnMouseEntered(mouse -> rotation.play());
        optionsImage.setOnMouseExited(mouse -> rotation.pause());
        Tooltip.install(optionsImage, new Tooltip("Options"));
        optionsImage.setOnMouseClicked(mouse -> {

        });


        title1.setText("Save Slot 1");
        title2.setText("Save Slot 2");
        title3.setText("Save Slot 3");

        newGame1.setOnAction(new NewHandler(SAVE_01, "Saves\\Save01\\"));

        continue1.setOnAction(new LoadHandler(SAVE_01, "Saves\\Save01\\"));
        newGame2.setOnAction(new NewHandler(SAVE_02, "Saves\\Save02\\"));
        continue2.setOnAction(new LoadHandler(SAVE_02, "Saves\\Save02\\"));
        newGame3.setOnAction(new NewHandler(SAVE_03, "Saves\\Save03\\"));
        continue3.setOnAction(new LoadHandler(SAVE_03, "Saves\\Save03\\"));

        try {
            ArrayList<Object> save1 = SaveManager.deserialize(SAVE_01);
            Player p = ((PlayerSprite) save1.get(0)).getPlayer();
            info1.setText(p.getName() + " - Lvl " + p.getLvl());
        } catch (InvalidClassException e) {
            System.out.println("Save 1 old version. Ignoring.");
            info1.setText("Old version");
            continue1.setDisable(true);
        } catch (Exception e) {
            info1.setText("Empty");
            continue1.setDisable(true);
        }

        try {
            ArrayList<Object> save2 = SaveManager.deserialize(SAVE_02);
            Player p = ((PlayerSprite) save2.get(0)).getPlayer();
            info2.setText(p.getName() + " - Lvl " + p.getLvl());
        } catch (InvalidClassException e) {
            System.out.println("Save 2 old version. Ignoring.");
            info2.setText("Old version");
            continue2.setDisable(true);
        } catch (Exception e) {
            System.out.println("Save 2 set to empty.");
            info2.setText("Empty");
            continue2.setDisable(true);
        }

        try {
            ArrayList<Object> save3 = SaveManager.deserialize(SAVE_03);
            Player p = ((PlayerSprite) save3.get(0)).getPlayer();
            info3.setText(p.getName() + " - Lvl " + p.getLvl());
        } catch (InvalidClassException e) {
            System.out.println("Save 3 old version. Ignoring.");
            info3.setText("Old version");
            continue3.setDisable(true);
        } catch (Exception e) {
            System.out.println("Save 3 set to empty.");
            info3.setText("Empty");
            continue3.setDisable(true);
        }

        this.setCenter(pane);
    }

    private class NewHandler implements EventHandler<ActionEvent> {

        private String dir;
        private String file;

        public NewHandler(String file, String dir) {
            this.dir = dir;
            this.file = file;
        }

        @Override
        public void handle(ActionEvent event) {
            GamePane gp = new GamePane(primaryStage, file, dir);
            GameStage.setGamePane(gp);
            GameScene scene = new GameScene(gp, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
            gp.fillEnemies();
            QuestHandler.resetQuests();
            gp.requestFocus();
            scene.getStylesheets().add(GameStage.STYLESHEET);
            primaryStage.setScene(scene);
        }
    }

    private class LoadHandler implements EventHandler<ActionEvent> {

        private String file;
        private String dir;

        public LoadHandler(String file, String dir) {
            this.dir = dir;
            this.file = file;
        }

        @Override
        public void handle(ActionEvent event) {
            GamePane gp = new GamePane(primaryStage, file, dir);
            GameStage.setGamePane(gp);
            ArrayList<Object> newMap = null;
            try {
                newMap = SaveManager.deserialize(file);
                gp.setCurrentMapFile((String) newMap.get(1));
                gp.setPlayer((PlayerSprite) newMap.get(0));
                gp.getMainPlayerSprite().setImage(gp.getMainPlayerSprite().getImageLocation());
                gp.setId((String) newMap.get(2));
            } catch (SerializationException | NullPointerException e) {
                GameStage.logger.error(e);
            } catch (InvalidClassException e) {
                System.out.println("Should never happen");
            }

            try {
                MapContainer map = new MapContainer(gp.getMainPlayerSprite(), (String) newMap.get(1));
                gp.setMapContainer(map);
            } catch (Exception e) {
                GameStage.logger.error(e);
            }

            gp.fillEnemies();

            GameScene scene = new GameScene(gp, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
            gp.requestFocus();
            scene.getStylesheets().add(GameStage.STYLESHEET);
            gp.getMainPlayerSprite().getPlayer().deserializeQuests();
            primaryStage.setScene(scene);
        }
    }
}
