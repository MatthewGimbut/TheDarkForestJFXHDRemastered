package gui;

import characters.Player;
import items.Item;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class TextItemPane extends HBox {

    private Label itemName;
    private Image itemImage;
    private ImageView imageView;
    private Item i;
    private Player player;

    public TextItemPane(Item i, Player player) {
        this.i = i;
        this.player = player;

        itemImage = new Image(i.getImageLocation());
        imageView = new ImageView(itemImage);

        this.setAlignment(Pos.CENTER_LEFT);

        itemName = new Label(i.getSimpleName());

        this.getChildren().add(itemName);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public Image getItemImage() {
        return itemImage;
    }

    public void setItemImage(Image itemImage) {
        this.itemImage = itemImage;
    }

    public Label getItemName() {
        return itemName;
    }

    public void setItemName(Label itemName) {
        this.itemName = itemName;
    }

    public Item getItem() {
        return i;
    }
}
