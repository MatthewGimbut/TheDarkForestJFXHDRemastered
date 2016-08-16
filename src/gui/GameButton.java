package gui;

import javafx.scene.control.Button;

/**
 * Created by Matthew on 8/13/2016.
 */
public class GameButton extends Button {

    public GameButton() {

    }

    public GameButton(String text) {
        this();
        this.setText(text);
    }

    public GameButton(String text, String idName) {
        this();
        this.setText(text);
        this.setId(idName);
    }

}
