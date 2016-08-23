package gui;

import javafx.scene.control.Button;
import javafx.scene.text.Font;

public class GameButton extends Button {

    public GameButton() {

    }

    public GameButton(String text) {
        this();
        this.setText(text);
    }

    public GameButton(String text, String idName) {
        this(text);
        this.setId(idName);
    }

    public GameButton(String text, int fontSize) {
        this(text);
        this.setFont(new Font("Cambria", fontSize));
    }

}
