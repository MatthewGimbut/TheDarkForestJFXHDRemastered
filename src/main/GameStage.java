package main;

import gui.GamePane;
import gui.GameScene;
import gui.MainMenuPane;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.nio.file.Paths;
import java.util.Random;

public class GameStage extends Application {

    public static final Color ORANGE = Color.rgb(255, 120, 0);
    public static final Color BUTTON_COLOR_THEME = Color.rgb(242, 203, 138);
    public static final Color BUTTON_LIGHT_COLOR_THEME = Color.rgb(251, 237, 191);
    //public static final Color LIGHTER_GRAY = Color.rgb(-1118482);
    public static final Color DARKER_GRAY = Color.rgb(160,160,160);
    public static final Color BUTTON_TEXT_COLOR = Color.BLACK;
    public static final Color BACKGROUND_COLOR_THEME = Color.rgb(198, 153, 86);
    public static final Color BORDER_COLOR_THEME = Color.rgb(104, 72, 39);
    public static final int TIMER_CONTROLLER = 40;
    public static double voiceVolume = .8;
    public static double effectVolume = .5;
    public static double musicVolume = .2;
    public static final Font GAME_FONT = new Font("Cambria", 14);
    public static final Font GAME_FONT_SMALL = new Font("Cambria", 12);
    private static Media sound;
    private static MediaPlayer soundPlayer;
    private static Random rand = new Random();
    public static final int WINDOW_WIDTH = 1056;
    public static final int WINDOW_HEIGHT = 730;
    public static final String STYLESHEET = "gui/themes.css";

    public static GamePane gamePane;


    public static void setGamePane(GamePane gamePane) {
        GameStage.gamePane = gamePane;
    }

    public static final String[] FEMALE_FIRST_NAMES = { "Mary", "Elizabeth", "Jennifer", "Maria", "Nancy", "Michelle", "Sarah", "Kim",
            "Amy", "Melissa", "Jessica", "Anna", "Kathleen", "Amanda", "Stephanie", "Diana", "Heather", "Gloria",
            "Cheryl", "Katherine", "Ashley", "Nicole", "Theresa", "Tammy", "Sarah", "Julia", "Grace", "Victoria",
            "Sophia", "Emma", "Olivia", "Isabella", "Emily", "Madison", "Aubrey", "Aria", "Kaylee", "Riley", "Chloe" };

    public static final String[] MALE_FIRST_NAMES = { "James", "John", "Robert", "Mike", "William", "David", "Boris", "Richard",
            "Matt", "Charles", "Joe", "Tom", "Chris", "Daniel", "Paul", "Mark", "Donald", "George", "Ken", "Steve",
            "Ed", "Frank", "Jason", "Gary", "Tim", "Greg", "Jerry", "Dennis", "Andrew", "Walter", "Patrick", "Peter",
            "Justin", "Doug", "Harold", "Albert", "Jack", "Terry", "Ralph", "Nick", "Sam", "Adam", "Randy", "Carlos",
            "Ryan" };

    public static final String[] LAST_NAMES = { "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia",
            "Rodriguez", "Wilson", "Martinez", "Anderson", "Taylor", "Hernandez", "Moore", "Martin", "Lee", "Clark",
            "Lewis", "Robinson", "Walker", "Hall", "Allen", "King", "Baker", "Green", "Turner", "Campbell", "Murphy",
            "Parker", "Morris", "Collins", "Cook", "Rivera", "Rogers", "Reed", "Bell", "Stewart", "Bailey", "Wood",
            "Jenkins", "Nufrio", "Leong", "Gimbut", "Trump" };


    @Override
    public void start(Stage primaryStage) throws Exception {
        GameScene mainScene = new GameScene(new MainMenuPane(primaryStage), WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.setMaxWidth(WINDOW_WIDTH);
        primaryStage.setMaxHeight(WINDOW_HEIGHT);
        primaryStage.setTitle("The Dark Forest Alpha");
        Image icon = new Image("file:Images\\SaveArea.png");
        primaryStage.getIcons().add(icon);


        primaryStage.show();
    }

    public static String getRandomName() {
        Random r = new Random();
        return MALE_FIRST_NAMES[r.nextInt(45)] + " " + LAST_NAMES[r.nextInt(45)];
    }

    public static int getRandomSpeed() {
        return rand.nextInt(50);
    }

    public static void playSound(String fileLocation) {
        sound = new Media(Paths.get(fileLocation).toUri().toString());
        soundPlayer = new MediaPlayer(sound);
        soundPlayer.setVolume(effectVolume);
        soundPlayer.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
