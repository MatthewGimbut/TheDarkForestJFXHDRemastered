package gui;

import characters.Player;
import items.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.GameStage;

public class ItemPane extends BorderPane {

    private GameLabel itemName;
    private Image itemImage;
    private Item i;
    private Player player;

    public ItemPane(Item i, Player player) {
        this.i = i;
        this.player = player;

        itemImage = new Image(i.getImageLocation());
        System.out.println(i.getImageLocation());

        ImageView iv = new ImageView(itemImage);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(iv);

        this.setCenter(box);

        Tooltip tp = new Tooltip(i.getItemToolTipText());
        tp.setOpacity(1);
        tp.setAutoHide(false);

        tp.setFont(new Font("Cambria", 14));

        Tooltip.install(iv, tp);

        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        String name = i.getSimpleName();
        if(i.isFavorite()) {
            name += " ★";
        }

        itemName = new GameLabel(name);
        setRarityColor(i);
        vb.getChildren().add(itemName);
        this.setTop(vb);

        itemName.setTooltip(tp);
        this.setPadding(new Insets(5, 1, 1, 0));

        itemName.setOnMouseEntered(event -> {
            //itemName.setTextFill(Color.MAGENTA);
        });

    }

    /**
     * Method to set the rarity color of the name label of the item
     * @param i The item to be checked
     */
    private void setRarityColor(Item i) {
        switch(i.getHowRare()) {
            case JUNK:
                itemName.setTextFill(Color.GRAY);
                break;
            case COMMON:
                itemName.setTextFill(Color.BLACK);
                break;
            case UNCOMMON:
                itemName.setTextFill(Color.GREEN);
                break;
            case RARE:
                itemName.setTextFill(Color.BLUE);
                break;
            case VERY_RARE:
                itemName.setTextFill(Color.MAGENTA);
                break;
            case LEGENDARY:
                itemName.setTextFill(GameStage.ORANGE); //ORANGE BECAUSE ROB SAID SO
                break;
        }
    }

    public GameLabel getLabel() {
        return itemName;
    }

    public Image getItemImage() {
        return itemImage;
    }
}
