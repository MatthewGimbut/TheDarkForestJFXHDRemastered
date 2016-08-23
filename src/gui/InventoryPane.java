package gui;

import characters.Player;
import items.Armor.Armor;
import items.Consumables.Consumable;
import items.Consumables.Potion;
import items.Item;
import items.Weapons.Weapon;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import main.GameStage;
import sprites.Sprite;

import java.text.DecimalFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class InventoryPane extends BorderPane {

    private Player player;
    private GamePane currentView;
    private TabPane tabbedPane;
    private Tab favorites, weapons, armor, consumables, misc;
    private TilePane favoritesPane, weaponsPane, armorPane, consumablesPane, miscPane;
    private TilePane[] panes;
    private GameButton nextPage, prevPage;
    private GameLabel gold, page, weight, info;
    private final int FONT_SIZE = 12;
    private final int ITEMS_PER_ROW = 4;
    private final int ITEMS_PER_PAGE = ITEMS_PER_ROW * 3;
    private int currentWeaponIndex, currentArmorIndex, currentFavoriteIndex = 0, currentConsumableIndex, currentMiscIndex;
    private int wepPages, armorPages, consumablePages, miscPages, favoritesPages;
    private int currentWepPage = 1, currentArmorPage = 1, currentConsumablePage = 1, currentMiscPage = 1, currentFavoritePage = 1;
    private ArrayList<Item> favoritesList, miscList;
    private ArrayList<Weapon> weaponsList;
    private ArrayList<Consumable> consumableList;
    private ArrayList<Armor> armorList;
    private DecimalFormat format;
    private EquipmentPane equipmentPane;


    public InventoryPane(GamePane currentView, Player player) {
        this.player = player;
        this.currentView = currentView;
        this.setId("standardPane");
        this.panes = new TilePane[5];

        VBox centerPane = new VBox(5);
        favorites = new Tab("Favorites");
        favorites.setClosable(false);
        weapons = new Tab("Weapons");
        weapons.setClosable(false);
        armor = new Tab("Armor");
        armor.setClosable(false);
        consumables = new Tab("Consumables");
        consumables.setClosable(false);
        misc = new Tab("Miscellaneous");
        misc.setClosable(false);

        page = new GameLabel("");
        info = new GameLabel("");
        format = new DecimalFormat("#.#");

        favoritesList = new ArrayList<>();
        miscList = new ArrayList<>();
        weaponsList = new ArrayList<>();
        consumableList = new ArrayList<>();
        armorList = new ArrayList<>();

        nextPage = new GameButton("→");
        nextPage.setOnAction(event -> {
            switch(tabbedPane.getSelectionModel().getSelectedIndex()) {
                case 0: // Favorites tab
                    currentFavoriteIndex += ITEMS_PER_PAGE;
                    currentFavoritePage++;
                    displayFavorites();
                    break;
                case 1: // Weapons tab
                    currentWeaponIndex += ITEMS_PER_PAGE;
                    currentWepPage++;
                    displayWeapons();
                    break;
                case 2: // Armor tab
                    currentArmorIndex += ITEMS_PER_PAGE;
                    currentArmorPage++;
                    displayArmors();
                    break;
                case 3: // Consumables tab
                    currentConsumableIndex += ITEMS_PER_PAGE;
                    currentConsumablePage++;
                    displayConsumables();
                    break;
                case 4: //Miscellaneous tab
                    currentMiscIndex += ITEMS_PER_PAGE;
                    currentMiscPage++;
                    displayMiscellaneous();
                    break;
            }
            checkItemIndices();
            updatePageNumbers();
        });
        prevPage = new GameButton("←");
        prevPage.setOnAction(event -> {
            switch(tabbedPane.getSelectionModel().getSelectedIndex()) {
                case 0: // Favorites tab
                    currentFavoriteIndex -= ITEMS_PER_PAGE;
                    currentFavoritePage--;
                    displayFavorites();
                    break;
                case 1: // Weapons tab
                    currentWeaponIndex -= ITEMS_PER_PAGE;
                    currentWepPage--;
                    displayWeapons();
                    break;
                case 2: // Armor tab
                    currentArmorIndex -= ITEMS_PER_PAGE;
                    currentArmorPage--;
                    displayArmors();
                    break;
                case 3: // Consumables tab
                    currentConsumableIndex -= ITEMS_PER_PAGE;
                    currentConsumablePage--;
                    displayConsumables();
                    break;
                case 4: //Miscellaneous tab
                    currentMiscIndex -= ITEMS_PER_PAGE;
                    currentMiscPage--;
                    displayMiscellaneous();
                    break;
            }
            checkItemIndices();
            updatePageNumbers();
        });

        tabbedPane = new TabPane();
        tabbedPane.setMaxHeight(550);
        tabbedPane.setMaxWidth(475);
        tabbedPane.getSelectionModel().selectedItemProperty().addListener(event -> {
            checkItemIndices();
            updatePageNumbers();
        });
        tabbedPane.getTabs().addAll(favorites, weapons, armor, consumables, misc);

        favoritesPane = new TilePane();
        weaponsPane = new TilePane();
        armorPane = new TilePane();
        consumablesPane = new TilePane();
        miscPane = new TilePane();

        panes[0] = favoritesPane;
        panes[1] = weaponsPane;
        panes[2] = armorPane;
        panes[3] = consumablesPane;
        panes[4] = miscPane;

        for(TilePane pane : panes) {
            pane.setHgap(10);
            pane.setVgap(15);
            pane.setPrefColumns(4);
            pane.setPrefRows(3);
            pane.setAlignment(Pos.TOP_LEFT);
        }

        favorites.setContent(favoritesPane);
        weapons.setContent(weaponsPane);
        armor.setContent(armorPane);
        consumables.setContent(consumablesPane);
        misc.setContent(miscPane);

        HBox topPane = new HBox(30);
        topPane.setAlignment(Pos.CENTER);
        topPane.getChildren().add(new GameLabel(player.getName() + "'s inventory", 22));
        weight = new GameLabel("(" + format.format(player.getCurrentCarry()) + " / " + player.getCarryCap() + ")", 15);
        topPane.getChildren().add(weight);

        HBox bottomButtons = new HBox(5);
        bottomButtons.setAlignment(Pos.CENTER);
        GameButton exit = new GameButton("Exit");
        exit.setOnAction(event -> {
            currentView.removeInventoryPane(this);
            currentView.requestFocus();
        });
        bottomButtons.getChildren().addAll(prevPage, exit, nextPage);

        sortItems();
        displayItems();
        checkItemIndices();
        updatePageNumbers();

        VBox sp = new VBox(3);
        sp.setAlignment(Pos.CENTER);
        sp.getChildren().addAll(info, page, bottomButtons);

        VBox left = new VBox(5);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setFillWidth(false);
        equipmentPane = new EquipmentPane(player, this);
        left.getChildren().add(equipmentPane);

        centerPane.getChildren().addAll(tabbedPane, page, sp);
        centerPane.setAlignment(Pos.CENTER);


        this.setMargin(left, new Insets(0, 10, 0, 10));
        this.setTop(topPane);
        this.setCenter(centerPane);
        //this.setBottom(sp);
        this.setLeft(left);
        this.setMaxWidth(875);
        this.setMaxHeight(500);
        this.setAlignment(tabbedPane, Pos.CENTER);
    }

    private void updatePageNumbers() {
        switch (tabbedPane.getSelectionModel().getSelectedIndex()) {
            case 0: //Favorites tab
                if (favoritesPages != 0) {
                    page.setText("Page " + currentFavoritePage + " of " + favoritesPages);
                } else {
                    //favoritesPane.setAlignment(Pos.CENTER);
                    page.setText("No favorites to display.");
                }
                break;
            case 1: //Weapons tab
                if (wepPages != 0) {
                    page.setText("Page " + currentWepPage + " of " + wepPages);
                } else {
                    page.setText("No weapons to display.");
                }
                break;
            case 2: //Armor tab
                if (armorPages != 0) {
                    page.setText("Page " + currentArmorPage + " of " + armorPages);
                } else {
                    page.setText("No armor to display.");
                }
                break;
            case 3: //Consumables tab
                if (consumablePages != 0) {
                    page.setText("Page " + currentConsumablePage + " of " + consumablePages);
                } else {
                    page.setText("No consumables to display.");
                }
                break;
            case 4: //Miscellaneous tab
                if(miscPages != 0) {
                    page.setText("Page " + currentMiscPage + " of " + miscPages);
                } else {
                    page.setText("No miscellaneous items to display.");
                }
                break;
        }
    }

    private void sortItems() {
        weaponsList.removeAll(weaponsList);
        armorList.removeAll(armorList);
        consumableList.removeAll(consumableList);
        miscList.removeAll(miscList);
        favoritesList.removeAll(favoritesList);
        favoritesList = (ArrayList<Item>) player.getInventory().stream()
                .filter(Item::isFavorite)
                .collect(Collectors.toList());

        player.getInventory().forEach(item -> {
            if (item instanceof Weapon) {
                weaponsList.add((Weapon) item);
            } else if (item instanceof Armor) {
                armorList.add((Armor) item);
            } else if (item instanceof Consumable) {
                consumableList.add((Consumable) item);
            } else {
                miscList.add(item);
            }
        });

        wepPages = (int) Math.ceil(weaponsList.size()/(ITEMS_PER_PAGE + 0.0));
        armorPages = (int) Math.ceil(armorList.size()/(ITEMS_PER_PAGE + 0.0));
        consumablePages = (int) Math.ceil(consumableList.size()/(ITEMS_PER_PAGE + 0.0));
        miscPages = (int) Math.ceil(miscList.size()/(ITEMS_PER_PAGE + 0.0));
        favoritesPages = (int) Math.ceil(favoritesList.size()/(ITEMS_PER_PAGE + 0.0));
    }

    public void displayWeapons() {
        weaponsPane.getChildren().clear();
        for(int i = currentWeaponIndex; i < currentWeaponIndex+ITEMS_PER_PAGE; i++) {
            try {
                weaponsPane.getChildren().add(initWeapon(weaponsList.get(i)));
            } catch (IndexOutOfBoundsException e) {
                //weapons.add(new ItemPanel("placeholder"));
            }
        }
    }

    public void displayArmors() {
        armorPane.getChildren().clear();
        for(int i = currentArmorIndex; i < currentArmorIndex+ITEMS_PER_PAGE; i++) {
            try {
                armorPane.getChildren().add(initArmor(armorList.get(i)));
            } catch (IndexOutOfBoundsException e) {
                //armors.add(new ItemPanel());
            }
        }
    }

    public void displayConsumables() {
        consumablesPane.getChildren().clear();
        for(int i = currentConsumableIndex; i < currentConsumableIndex+ITEMS_PER_PAGE; i++) {
            try {
                consumablesPane.getChildren().add(initConsumable(consumableList.get(i)));
            } catch (IndexOutOfBoundsException e) {
                //consumables.add(new ItemPanel());
            }
        }
    }

    public void displayFavorites() {
        favoritesPane.getChildren().clear();
        for(int i = currentFavoriteIndex; i < currentFavoriteIndex+ITEMS_PER_PAGE; i++) {
            try {
                Item it = favoritesList.get(i);
                if(it instanceof  Weapon) {
                    favoritesPane.getChildren().add(initWeapon((Weapon) it));
                } else if(it instanceof Armor) {
                    favoritesPane.getChildren().add(initArmor((Armor) it));
                } else if(it instanceof Consumable) {
                    favoritesPane.getChildren().add(initConsumable((Consumable) it));
                } else {
                    favoritesPane.getChildren().add(initMisc(it));
                }
            } catch (IndexOutOfBoundsException e) {
                //consumables.add(new ItemPanel());
            }
        }
    }

    public void displayMiscellaneous() {
        miscPane.getChildren().clear();
        for(int i = currentMiscIndex; i < currentMiscIndex+ITEMS_PER_PAGE; i++) {
            try {
                miscPane.getChildren().add(initMisc(miscList.get(i)));
            } catch (IndexOutOfBoundsException e) {
                //misc.add(new ItemPanel());
            }
        }
    }

    public void displayItems() {
        displayFavorites();
        displayWeapons();
        displayArmors();
        displayConsumables();
        displayMiscellaneous();
        //checkEmpty();
    }

    private void checkEmpty() {
        if(favoritesList.size() == 0) {
            favoritesPane.getChildren().add(new GameLabel("No favorites."));
        }
        if(weaponsList.size() == 0) {
            weaponsPane.getChildren().add(new GameLabel("No weapons."));
        }
        if(armorList.size() == 0) {
            armorPane.getChildren().add(new GameLabel("No armors."));
        }
        if(consumableList.size() == 0) {
            consumablesPane.getChildren().add(new GameLabel("No consumables."));
        }
        if(miscList.size() == 0) {
            miscPane.getChildren().add(new GameLabel("No miscellaneous items."));
        }
    }

    private VBox initWeapon(Weapon w) { //TODO set padding/minsize of buttons to test movement prevention?
        ItemPane ip = new ItemPane(w, player);
        ip.getLabel().setOnMouseClicked(event -> {
            if(w.isFavorite()) {
                w.setFavorite(false);
            } else {
                w.setFavorite(true);
            }
            sortItems();
            displayWeapons();
            displayFavorites();
            checkItemIndices();
            updatePageNumbers();
            equipmentPane.redraw();
        });

        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);

        box.getChildren().add(ip);

        GameButton equip;
        HBox buttons = new HBox(3);
        if(w.isCurrentlyEquipped()) {
            equip = new GameButton("Unequip", FONT_SIZE);
            equip.setOnAction(new UnequipHandler(w));
        } else {
            equip = new GameButton("Equip", FONT_SIZE);
            equip.setOnAction(new EquipHandler(w));
        }

        GameButton drop = new GameButton("Drop", FONT_SIZE);
        drop.setOnAction(new DropHandler(w));

        buttons.getChildren().addAll(equip, drop);

        box.getChildren().add(buttons);
        return box;
    }

    //TODO fix exact copy paste
    private VBox initArmor(Armor a) {
        ItemPane ip = new ItemPane(a, player);
        ip.getLabel().setOnMouseClicked(event -> {
            if(a.isFavorite()) {
               a.setFavorite(false);
            } else {
               a.setFavorite(true);
            }
            sortItems();
            displayArmors();
            displayFavorites();
            checkItemIndices();
            updatePageNumbers();
        });

        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);

        box.getChildren().add(ip);

        GameButton equip;
        HBox buttons = new HBox(3);
        if(a.isCurrentlyEquipped()) {
            equip = new GameButton("Unequip", FONT_SIZE);
            equip.setOnAction(new UnequipHandler(a));
        } else {
            equip = new GameButton("Equip", FONT_SIZE);
            equip.setOnAction(new EquipHandler(a));
        }

        GameButton drop = new GameButton("Drop", FONT_SIZE);
        drop.setOnAction(new DropHandler(a));

        buttons.getChildren().addAll(equip, drop);

        box.getChildren().add(buttons);
        return box;
    }

    private VBox initConsumable(Consumable c) {
        ItemPane ip = new ItemPane(c, player);
        ip.getLabel().setOnMouseClicked(event -> {
            if(c.isFavorite()) {
                c.setFavorite(false);
            } else {
                c.setFavorite(true);
            }
            sortItems();
            displayConsumables();
            displayFavorites();
            checkItemIndices();
            updatePageNumbers();
        });

        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);

        box.getChildren().add(ip);

        HBox buttons = new HBox(3);
        GameButton consume = new GameButton("Consume", FONT_SIZE);
        consume.setOnAction(event -> {
            player.consume((Potion) c);
            info.setText("Restored " + ((Potion) c).getAmount() + " health.");
            updateInventory();
            displayConsumables();
            checkForEmptyConsumable();
        });

        GameButton drop = new GameButton("Drop", FONT_SIZE);
        drop.setOnAction(new DropHandler(c));

        buttons.getChildren().addAll(consume, drop);

        box.getChildren().add(buttons);
        return box;
    }

    private VBox initMisc(Item i) {
        ItemPane ip = new ItemPane(i, player);
        ip.getLabel().setOnMouseClicked(event -> {
            if(i.isFavorite()) {
                i.setFavorite(false);
            } else {
                i.setFavorite(true);
            }
            sortItems();
            displayMiscellaneous();
            displayFavorites();
            checkItemIndices();
            updatePageNumbers();
        });
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);

        box.getChildren().add(ip);

        HBox buttons = new HBox(3);

        GameButton drop = new GameButton("Drop", FONT_SIZE);
        drop.setOnAction(new DropHandler(i));

        buttons.getChildren().addAll(drop);

        box.getChildren().add(buttons);
        return box;
    }

    private void checkItemIndices() {
        switch (tabbedPane.getSelectionModel().getSelectedIndex()) {
            case 0: //Favorites tab
                try {
                    favoritesList.get(currentFavoriteIndex+ITEMS_PER_PAGE);
                    nextPage.setDisable(false);
                } catch (IndexOutOfBoundsException e1) {
                    nextPage.setDisable(true);
                }
                try {
                    weaponsList.get(currentWeaponIndex-1);
                    prevPage.setDisable(false);
                } catch (IndexOutOfBoundsException e2) {
                    prevPage.setDisable(true);
                }
                break;
            case 1: //Weapons tab
                try {
                    weaponsList.get(currentWeaponIndex+ITEMS_PER_PAGE);
                    nextPage.setDisable(false);
                } catch (IndexOutOfBoundsException e1) {
                    nextPage.setDisable(true);
                }
                try {
                    weaponsList.get(currentWeaponIndex-1);
                    prevPage.setDisable(false);
                } catch (IndexOutOfBoundsException e2) {
                    prevPage.setDisable(true);
                }

                break;
            case 2: //Armor tab
                try {
                    armorList.get(currentArmorIndex+ITEMS_PER_PAGE);
                    nextPage.setDisable(false);
                } catch (IndexOutOfBoundsException e1) {
                    nextPage.setDisable(true);
                }
                try {
                    armorList.get(currentArmorIndex-1);
                    prevPage.setDisable(false);
                } catch (IndexOutOfBoundsException e2) {
                    prevPage.setDisable(true);
                }
                break;
            case 3: //Consumables tab
                try {
                    consumableList.get(currentConsumableIndex+ITEMS_PER_PAGE);
                    nextPage.setDisable(false);
                } catch (IndexOutOfBoundsException e1) {
                    nextPage.setDisable(true);
                }
                try {
                    consumableList.get(currentConsumableIndex-1);
                    prevPage.setDisable(false);
                } catch (IndexOutOfBoundsException e2) {
                    prevPage.setDisable(true);
                }
                break;
            case 4: //Miscellaneous tab
                try {
                    miscList.get(currentMiscIndex+ITEMS_PER_PAGE);
                    nextPage.setDisable(false);
                } catch (IndexOutOfBoundsException e1) {
                    nextPage.setDisable(true);
                }
                try {
                    miscList.get(currentMiscIndex-1);
                    prevPage.setDisable(false);
                } catch (IndexOutOfBoundsException e2) {
                    prevPage.setDisable(true);
                }
                break;
        }
    }

    private void updateInventory() {
        sortItems();
        checkItemIndices();
        updatePageNumbers();
    }

    private void checkForEmptyConsumable() {
        try {
            consumableList.get(currentConsumableIndex);
        } catch (IndexOutOfBoundsException e1) {
            prevPage.fire();
        }
    }

    private void checkForEmptyWeapon() {
        try {
            weaponsList.get(currentWeaponIndex);
        } catch (IndexOutOfBoundsException e1) {
            prevPage.fire();
        }
    }

    private void checkForEmptyArmor() {
        try {
            armorList.get(currentArmorIndex);
        } catch (IndexOutOfBoundsException e1) {
            prevPage.fire();
        }
    }

    private void checkForEmptyMisc() {
        try {
            miscList.get(currentMiscIndex);
        } catch (IndexOutOfBoundsException e1) {
            prevPage.fire();
        }
    }

    private class DropHandler implements EventHandler {

        private Item i;

        public DropHandler(Item i) { this.i = i; }

        @Override
        public void handle(Event event) {
            if(i.isCurrentlyEquipped()) {
                player.unequip(i);
            }
            player.removeSingleItem(i);
            info.setText("Dropped " + i.getSimpleName() + ".");
            if (i instanceof Weapon) {
                weaponsList.remove(i);
                displayWeapons();
                checkForEmptyWeapon();
            } else if (i instanceof Armor) {
                armorList.remove(i);
                displayArmors();
                checkForEmptyArmor();
            } else if (i instanceof Consumable) {
                consumableList.remove(i);
                displayConsumables();
                checkForEmptyConsumable();
            } else if (i instanceof Item) {
                miscList.remove(i);
                displayMiscellaneous();
                checkForEmptyMisc();
            }
            updateInventory();
            if(i.isFavorite()) displayFavorites();
            weight.setText("(" + format.format(player.getCurrentCarry()) + " / " + player.getCarryCap() + ")");
        }
    }

    private class EquipHandler implements EventHandler {

        private Item equip;

        public EquipHandler(Item i) { this.equip = i; }

        @Override
        public void handle(Event event) {
            if(equip instanceof Armor) {
                player.equip((Armor) equip);
                GameStage.playSound("Sounds\\Inventory\\Equip\\leather_inventory.mp3");
                updateInventory();
                displayArmors();
                checkForEmptyArmor();
            } else {
                player.equip((Weapon) equip);
                updateInventory();
                displayWeapons();
                checkForEmptyWeapon();
            }
            if(equip.isFavorite()) displayFavorites();
            info.setText("Equipped " + equip.getSimpleName() + ". ");
            equipmentPane.redraw();
        }
    }

    private class UnequipHandler implements EventHandler {

        private Item unequip;

        public UnequipHandler(Item i) { this.unequip = i; }

        @Override
        public void handle(Event event) {
            if(unequip instanceof Armor) {
                player.unequip(unequip);
                GameStage.playSound("Sounds\\Inventory\\Equip\\leather_inventory.mp3");
                updateInventory();
                displayArmors();
                checkForEmptyArmor();
            } else {
                player.unequip(unequip);
                GameStage.playSound("Sounds\\Inventory\\Equip\\leather_inventory.mp3");
                updateInventory();
                displayWeapons();
                checkForEmptyWeapon();
            }
            if(unequip.isFavorite()) displayFavorites();
            info.setText("Unequipped " + unequip.getSimpleName() + ". ");
            equipmentPane.redraw();
        }
    }

}
