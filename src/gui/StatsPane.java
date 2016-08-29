package gui;

import characters.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sprites.PlayerSprite;

import java.text.DecimalFormat;

/**
 * Created by Matthew on 8/29/2016.
 */
public class StatsPane extends BorderPane {

    private GamePane currentView;
    private PlayerSprite player;
    private GameButton exit;
    private DecimalFormat dec;

    public StatsPane(GamePane currentView, PlayerSprite player) {
        this.currentView = currentView;
        this.player = player;

        this.setId("standardPane");

        dec = new DecimalFormat("#.#");

        exit = new GameButton("Exit");
        exit.setOnAction(event -> {
            currentView.removeStatsPane(this);
            currentView.requestFocus();
        });

        HBox stats = new HBox(25);
        stats.setAlignment(Pos.CENTER);

        VBox leftStats = new VBox(5);
        leftStats.setAlignment(Pos.CENTER_LEFT);
        VBox rightStats = new VBox(5);
        rightStats.setAlignment(Pos.CENTER_LEFT);

        HBox imageBox = new HBox(10);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.getChildren().add(new ImageView(player.getImage()));
        this.setMargin(imageBox, new Insets(0, 30, 0, 0));

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        top.getChildren().add(new GameLabel(player.getPlayer().getName() + " - Level " + player.getPlayer().getLvl()));

        leftStats.getChildren().add(new GameLabel("HP: "));
        leftStats.getChildren().add(new GameLabel("XP: "));
        leftStats.getChildren().add(new GameLabel("Mana: "));
        leftStats.getChildren().add(new GameLabel("Weight: "));
        leftStats.getChildren().add(new GameLabel("Atk: "));
        leftStats.getChildren().add(new GameLabel("Def: "));
        leftStats.getChildren().add(new GameLabel("Magic: "));

        rightStats.getChildren().add(new GameLabel(player.getPlayer().getCurrentHP() + "/" + player.getPlayer().getMaxHP()));
        rightStats.getChildren().add(new GameLabel(player.getPlayer().getXp() + "/" + "100"));
        rightStats.getChildren().add(new GameLabel(player.getPlayer().getCurrentMana() + "/" + player.getPlayer().getMaxMana()));
        rightStats.getChildren().add(new GameLabel(dec.format(player.getPlayer().getCurrentCarry()) + "/" + player.getPlayer().getCarryCap()));
        rightStats.getChildren().add(new GameLabel("" + player.getPlayer().getAtk()));
        rightStats.getChildren().add(new GameLabel("" + player.getPlayer().getDef()));
        rightStats.getChildren().add(new GameLabel("" + player.getPlayer().getMagic()));

        stats.getChildren().addAll(leftStats, rightStats);

        HBox bottom = new HBox(10);
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().add(exit);

        this.setLeft(stats);
        this.setRight(imageBox);
        this.setTop(top);
        this.setBottom(bottom);

        this.setMaxWidth(300);
        this.setMaxHeight(350);
    }
}
