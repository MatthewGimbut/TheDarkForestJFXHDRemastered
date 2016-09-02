package gui;

import characters.Player;
import items.Item;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class TextItemPane extends HBox {

    private GameLabel itemName;
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

        itemName = new GameLabel(i.getSimpleName());

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

    public GameLabel getItemName() {
        return itemName;
    }

    public void setItemName(GameLabel itemName) {
        this.itemName = itemName;
    }
}
