package main;

import characters.Merchant;
import gui.GamePane;
import gui.MessagePane;
import gui.StatsPane;
import gui.items.LootPane;
import gui.items.ScrollingInventoryPane;
import gui.items.ShopPane;
import gui.menus.ConfirmQuitPane;
import gui.menus.MenuPane;
import gui.menus.OptionsPane;
import gui.quests.JournalPane;
import gui.quests.NewQuestPane;
import gui.quests.QuestSuccess;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import quests.Quest;
import sprites.Lootable;
import sprites.NPC;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Matthew on 11/15/2016.
 */
public class UIManager {

    private boolean menuCurrentlyDisplayed, statsCurrentlyDisplayed,
            inventoryCurrentlyDisplayed, lootCurrentlyDisplayed, settingsCurrentlyDisplayed,
            messageCurrentlyDisplayed, equipmentCurrentlyDisplayed, questCurrentlyDisplayed;

    private Queue<BorderPane> panelStack = new LinkedList<BorderPane>();
    private GamePane currentView;
    private Timeline t;
    private MenuPane menu;


    public UIManager(GamePane currentView) {
        this.currentView = currentView;
        menu = new MenuPane(currentView);
        initFlags();
    }

    /**
     * Checks to see if the player is currently engaged in some menu.
     * @return Whether or not the player is engaged.
     */
    public boolean engaged() {
        return (menuCurrentlyDisplayed
                || statsCurrentlyDisplayed || inventoryCurrentlyDisplayed
                || lootCurrentlyDisplayed || settingsCurrentlyDisplayed
                || messageCurrentlyDisplayed || equipmentCurrentlyDisplayed
                || questCurrentlyDisplayed);
    }

    public boolean engagedMinusMenu() {
        return (statsCurrentlyDisplayed || inventoryCurrentlyDisplayed
                || lootCurrentlyDisplayed || settingsCurrentlyDisplayed
                || messageCurrentlyDisplayed || equipmentCurrentlyDisplayed
                || questCurrentlyDisplayed);
    }

    public Queue<BorderPane> getPanelStack() {
        return panelStack;
    }

    private void initFlags() {
        menuCurrentlyDisplayed = false;
        statsCurrentlyDisplayed = false;
        inventoryCurrentlyDisplayed = false;
        lootCurrentlyDisplayed = false;
        settingsCurrentlyDisplayed = false;
        messageCurrentlyDisplayed = false;
        equipmentCurrentlyDisplayed = false;
        questCurrentlyDisplayed = false;
    }

    public void displayQuestSuccessPane(quests.Quest quest) {
        QuestSuccess qs = new QuestSuccess(currentView.getMainPlayerSprite(), currentView, quest);
        if(!questCurrentlyDisplayed) {
            questCurrentlyDisplayed = true;
            currentView.getChildren().add(qs);
            qs.requestFocus();
        } else {
            panelStack.add(qs);
        }
    }

    public void removeQuestSuccessPane(QuestSuccess qs) {
        currentView.getChildren().remove(qs);
        questCurrentlyDisplayed = false;
        currentView.requestFocus();
        if(panelStack.size() != 0) {
            popQuestPanelStack();
        }
    }

    public void toggleMenuPane() {
        if(!menuCurrentlyDisplayed) {
            currentView.getChildren().add(menu);
            currentView.setMargin(menu, new Insets(60, 940, 460, 5));
            menuCurrentlyDisplayed = true;

            menu.requestFocus();
        } else {
            currentView.getChildren().remove(menu);
            menuCurrentlyDisplayed = false;
            currentView.requestFocus();
        }
    }

    public void displayInventoryPane() {
        if(!inventoryCurrentlyDisplayed) {
            ScrollingInventoryPane pane = new ScrollingInventoryPane(currentView,  currentView.getMainPlayerSprite().getPlayer());
            currentView.getChildren().add(pane);
            inventoryCurrentlyDisplayed = true;
            pane.requestFocus();
        }
    }

    public void removeInventoryPane(ScrollingInventoryPane pane) {
        currentView.getChildren().remove(pane);
        inventoryCurrentlyDisplayed = false;
        currentView.requestFocus();
    }

    public void displayLootPane(Lootable loot) {
        if(!lootCurrentlyDisplayed) {
            LootPane lp = new LootPane(currentView, loot, currentView.getMainPlayerSprite().getPlayer());
            currentView.getChildren().add(lp);
            lootCurrentlyDisplayed = true;
            lp.requestFocus();
        }
    }

    public void removeLootPane(LootPane pane) {
        currentView.getChildren().remove(pane);
        lootCurrentlyDisplayed = false;
        currentView.requestFocus();
    }

    public void displayOptionsPane() {
        if(!settingsCurrentlyDisplayed) {
            OptionsPane op = new OptionsPane(currentView, currentView.getMainPlayerSprite().getPlayer());
            currentView.getChildren().add(op);
            settingsCurrentlyDisplayed = true;
            op.requestFocus();
        }
    }

    public void removeOptionsPane(OptionsPane pane) {
        currentView.getChildren().remove(pane);
        settingsCurrentlyDisplayed = false;
        currentView.requestFocus();
    }

    public void displayMessagePane(String message) {
        MessagePane mp = new MessagePane(message, currentView.getMainPlayerSprite().getPlayer(), currentView);
        messageInfo(mp);

    }

    public void displayMessagePane(String[] message, NPC npc) {
        MessagePane mp = new MessagePane(message, currentView.getMainPlayerSprite().getPlayer(), currentView, npc);
        messageInfo(mp);
    }

    private void messageInfo(MessagePane message) {
        if(!messageCurrentlyDisplayed) {
            messageCurrentlyDisplayed = true;
            if (currentView.getMainPlayerSprite().getY() < GameStage.WINDOW_HEIGHT / 2) {
                currentView.setMargin(message, new Insets(GameStage.WINDOW_HEIGHT - message.getMaxHeight() - 80, 0, 0, 0));
            } else {
                currentView.setMargin(message, new Insets(0, 0, GameStage.WINDOW_HEIGHT - message.getMaxHeight() - 35, 0));
            }

            currentView.getChildren().add(message);
            message.requestFocus();
        }
    }

    public void removeMessagePane(MessagePane pane) {
        t = new Timeline(new KeyFrame(Duration.millis(150), event -> {}));
        t.setOnFinished(event -> messageCurrentlyDisplayed = false);
        t.play();

        currentView.getChildren().remove(pane);
        currentView.requestFocus();
    }

    public void displayStatsPane() {
        if(!statsCurrentlyDisplayed) {
            statsCurrentlyDisplayed = true;
            StatsPane pane = new StatsPane(currentView, currentView.getMainPlayerSprite());
            currentView.getChildren().add(pane);
            pane.requestFocus();
        }
    }

    public void removeStatsPane(StatsPane pane) {
        currentView.getChildren().remove(pane);
        statsCurrentlyDisplayed = false;
        currentView.requestFocus();
    }

    public void displayNewQuestPane(Quest quest) {
        NewQuestPane nqp = new NewQuestPane(currentView, quest);
        if(!questCurrentlyDisplayed) {
            questCurrentlyDisplayed = true;
            currentView.getChildren().add(nqp);
            nqp.requestFocus();
        } else {
            panelStack.add(nqp);
        }
    }

    public void removeNewQuestPane(NewQuestPane pane) {
        currentView.getChildren().remove(pane);
        questCurrentlyDisplayed = false;
        currentView.requestFocus();
        if(panelStack.size() != 0) {
            popQuestPanelStack();
        }
    }

    public void displayJournal() {
        if(!questCurrentlyDisplayed) {
            questCurrentlyDisplayed = true;
            JournalPane journal = new JournalPane(currentView);
            currentView.getChildren().add(journal);
            journal.requestFocus();
        }
    }

    public void removeJournalPane(JournalPane journal) {
        currentView.getChildren().remove(journal);
        questCurrentlyDisplayed = false;
        currentView.requestFocus();
    }

    public void resetMessagePaneFocus() {
        MessagePane pane = null;

        for(Object o : currentView.getChildren()) {
            if(o instanceof MessagePane) {
                pane = (MessagePane) o;
            }
        }

        if(pane != null) {
            pane.requestFocus();
        } else {
            currentView.requestFocus();
        }
    }

    public void popQuestPanelStack() {
        BorderPane bp = panelStack.remove();
        questCurrentlyDisplayed = true;
        currentView.getChildren().add(bp);
        bp.requestFocus();
    }

    /*public void popMerchantPanelStack() {
        BorderPane bp = panelStack.remove();
        menuCurrentlyDisplayed = true;
        currentView.getChildren().add(bp);
        bp.requestFocus();
    }*/

    public void showConfirmQuitPane() {
        menuCurrentlyDisplayed = true;
        ConfirmQuitPane cqp = new ConfirmQuitPane(currentView);
        currentView.getChildren().add(cqp);
        cqp.requestFocus();
    }

    public void removeConfirmQuitPane(ConfirmQuitPane cqp) {
        currentView.getChildren().remove(cqp);
        currentView.requestFocus();
        menuCurrentlyDisplayed = false;
    }

    public void displayShopPane(NPC npc) {
        if(!menuCurrentlyDisplayed) {
            Merchant merchant = (Merchant) npc.getNPC();
            ShopPane pane = new ShopPane(currentView, merchant);
            menuCurrentlyDisplayed = true;
            currentView.getChildren().add(pane);
            pane.requestFocus();
        }
    }

    public void removeShopPane(ShopPane pane) {
        currentView.getChildren().remove(pane);
        currentView.requestFocus();
        menuCurrentlyDisplayed = false;
    }

    public boolean isMenuCurrentlyDisplayed() {
        return menuCurrentlyDisplayed;
    }

    public boolean isStatsCurrentlyDisplayed() {
        return statsCurrentlyDisplayed;
    }

    public boolean isInventoryCurrentlyDisplayed() {
        return inventoryCurrentlyDisplayed;
    }

    public boolean isLootCurrentlyDisplayed() {
        return lootCurrentlyDisplayed;
    }

    public boolean isSettingsCurrentlyDisplayed() {
        return settingsCurrentlyDisplayed;
    }

    public boolean isMessageCurrentlyDisplayed() {
        return messageCurrentlyDisplayed;
    }

    public boolean isEquipmentCurrentlyDisplayed() {
        return equipmentCurrentlyDisplayed;
    }

    public boolean isQuestCurrentlyDisplayed() {
        return questCurrentlyDisplayed;
    }

}
