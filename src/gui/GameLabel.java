package gui;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 * Created by Matthew on 8/15/2016.
 */
public class GameLabel extends Label {

    private final String FONT = "Cambria";

    public GameLabel() {
        this.setFont(new Font(FONT, 14));
    }

    public GameLabel(String text) {
        this();
        this.setText(text);
    }

    public GameLabel(String text, int fontSize) {
        this.setText(text);
        this.setFont(new Font(FONT, fontSize));
    }
}
