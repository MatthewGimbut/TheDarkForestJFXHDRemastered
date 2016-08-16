package gui;

import characters.Player;
import items.Item;
import javafx.geometry.Insets;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public class ItemPane extends BorderPane {

    private GameLabel itemName;
    private Image itemImage;
    private Item i;
    private Player player;

    public ItemPane(Item i, Player player) {
        this.i = i;
        this.player = player;

        itemImage = new Image(i.getImageLocation());
        ImageView iv = new ImageView(itemImage);
        this.setCenter(iv);

        Tooltip tp = new Tooltip(i.getItemToolTipText());
        tp.setOpacity(.92);
        tp.setAutoHide(false);

        tp.setFont(new Font("Cambria", 14));

        Tooltip.install(iv, tp);

        itemName = new GameLabel(i.getSimpleName(), 13);
        this.setTop(itemName);

        itemName.setTooltip(tp);
        this.setPadding(new Insets(5, 15, 5, 15));
    }
}
