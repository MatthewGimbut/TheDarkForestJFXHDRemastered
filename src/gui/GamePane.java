package gui;

import characters.Enemy;
import characters.Neutral;
import characters.Player;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.GameStage;
import main.MapContainer;
import main.SaveManager;
import sprites.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GamePane extends StackPane {

    private Stage primaryStage;
    private boolean menuCurrentlyDisplayed, battleCurrentlyDisplayed, statsCurrentlyDisplayed,
            inventoryCurrentlyDisplayed, lootCurrentlyDisplayed, settingsCurrentlyDisplayed,
            messageCurrentlyDisplayed, equipmentCurrentlyDisplayed;
    private PlayerSprite player;
    private ArrayList<String> input;
    private MapContainer map;
    private String currentMapFile;
    private boolean hostile;
    private MenuPane menu;
    private GraphicsContext gc;
    private Timeline t;

    public GamePane(Stage primaryStage) {

        menu = new MenuPane(this);
        input = new ArrayList<>();
        initFlags();
        this.primaryStage = primaryStage;
        Canvas canvas = new Canvas(GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
        this.getChildren().add(canvas);

        player = new PlayerSprite(200, 100, new Player("Matthew Gimbut"));

        gc = canvas.getGraphicsContext2D();

        this.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if(!engaged()) {
                switch(code) {
                    case "E":
                        switch (player.getImageLocation()) {
                            case Player.FACING_NORTH:
                                interact(player.getX(), player.getY() - player.getHeight());
                                break;
                            case Player.FACING_SOUTH:
                                interact(player.getX(), player.getY() + player.getHeight());
                                break;
                            case Player.FACING_EAST:
                                interact(player.getX() + player.getWidth(), player.getY());
                                break;
                            case Player.FACING_WEST:
                                interact(player.getX() - player.getWidth()-5, player.getY());
                                break;
                        }
                        break;
                    case "SHIFT":
                        player.setPlayerSpeed(4);
                        break;
                    case "W":
                        player.setImage(Player.FACING_NORTH);
                        player.setDy(-player.getPlayerSpeed());
                        break;
                    case "A":
                        player.setImage(Player.FACING_WEST);
                        player.setDx(-player.getPlayerSpeed());
                        break;
                    case "S":
                        player.setImage(Player.FACING_SOUTH);
                        player.setDy(player.getPlayerSpeed());
                        break;
                    case "D":
                        player.setImage(Player.FACING_EAST);
                        player.setDx(player.getPlayerSpeed());
                        break;
                }
            }
        });

        this.setOnKeyReleased(event -> {
            String code = event.getCode().toString();
            switch(code) {
                case "ESCAPE":
                    if(!engagedMinusMenu()) toggleMenuPane();
                    break;
                case "I":
                    if(!inventoryCurrentlyDisplayed) displayInventoryPane();
                    break;
                case "SHIFT":
                    player.setPlayerSpeed(2);
                    break;
                case "A":
                    player.setDx(0);
                    break;
                case "D":
                    player.setDx(0);
                    break;
                case "W":
                    player.setDy(0);
                    break;
                case "S":
                    player.setDy(0);
                    break;
            }
        });

        initCollections();
        AnimationTimer animate = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                gc.clearRect(0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
                drawLayers(gc);
            }
        };
        animate.start();
    }

    public void displayQuestSuccessPane(quests.Quest quest) {
        if(!messageCurrentlyDisplayed) {
            messageCurrentlyDisplayed = true;
            QuestSuccess qs = new QuestSuccess(player, this, quest);
            this.getChildren().add(qs);
            qs.requestFocus();
        }
    }

    public void removeQuestSuccessPane(QuestSuccess qs) {
        this.getChildren().remove(qs);
        messageCurrentlyDisplayed = false;
        this.requestFocus();
    }

    /**
     * Method for determining interactions.
     * Creates an invisible Rectangle2D in the direction the player is facing.
     * If that Rectangle2D intersects an obstacle, the player interacts with it.
     * @param x The x coordinate of where to set the Rectangle2D.
     * @param y The y coordinate of where to set the Rectangle2D.
     */
    private void interact(int x, int y) {
        Sprite interact = new Sprite(x, y, "file:Images\\Blank24x24.png");
        interact.setObstacle(false);
        //map.getMapItems().add(interact);
        //map.updateLayers();
        DisplayItem remove = null;
        interact.render(gc);

        boolean found = false; //We separate it this way to prevent two actions from firing at once and conflicting if two obstacles are within range

        ArrayList<Sprite> items = (ArrayList<Sprite>) map.getMapItems().stream()
                .filter(i -> !(i instanceof LowerLayer))
                .collect(Collectors.toList());

        for(int i = 0; i < items.size() && !found; i++) {
            if (interact.getBounds().intersects(items.get(i).getBounds())) {
                found = true;
                if (items.get(i) instanceof Lootable) {
                    displayLootPane(((Lootable) items.get(i)));
                } else if (items.get(i) instanceof Save) {
                    SaveManager.serialize(currentMapFile, player, this.getId());
                    displayMessagePane("Save succeeded!");
                } else if (items.get(i) instanceof NPC) {
                    System.out.println("reached");
                    npcInteraction(items.get(i));
                } else if (items.get(i) instanceof DisplayItem) {
                    boolean pickedUp = itemInteraction((DisplayItem) items.get(i));
                    if(pickedUp) remove = (DisplayItem) items.get(i);
                }
            }
        }
        if (remove != null) { //Removes an item from the map if it is tagged for removal.
            map.removeSprite(remove);
        }
    }

    /**
     * Method for a player interacting with an item on the ground.
     * Right now, the only action is to allow them to pick it up and add it to their inventory.
     * @param item The Sprite of the item to be added to the inventory.
     */
    private boolean itemInteraction(DisplayItem item) {
        player.setDx(0); //Sets the player's x and y movement to 0 so that they don't begin moving on their own after finished with dialogue.
        player.setDy(0);
        if(player.getPlayer().addItem(item.getItem())) {
            displayMessagePane("You have picked up a " + item.getItem().getSimpleName() + ".");

            //Below segment is commented out for testing reasons.
            //It works perfectly, but I don't want items being removed from file while testing.

		/*try {
			MapParser.removeItem("item", currentMapFile, item.getX(), item.getY());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
            return true;
        } else {
            displayMessagePane("You don't currently have the inventory space to carry that!");
            return false;
        }
    }

    /**
     * Method for a player interacting with NPCs.
     * Shows plain dialogue for non-hostile NPCs.
     * Shows dialogue then a battle for hostiles.
     * @param obstacle The sprite on the map that contains the NPC.
     */
    private void npcInteraction(Sprite obstacle) {
        switch(player.getImageLocation()) {
            case Player.FACING_NORTH:
                ((NPC) obstacle).setCurrentImage(((NPC) obstacle).getNPC().getSouthImage());
                break;
            case Player.FACING_SOUTH:
                ((NPC) obstacle).setCurrentImage(((NPC) obstacle).getNPC().getNorthImage());
                break;
            case Player.FACING_EAST:
                ((NPC) obstacle).setCurrentImage(((NPC) obstacle).getNPC().getWestImage());
                break;
            case Player.FACING_WEST:
                ((NPC) obstacle).setCurrentImage(((NPC) obstacle).getNPC().getEastImage());
                break;
        }

        if(((NPC) obstacle).getNPC() instanceof Enemy) {
            if(obstacle.getMessage() != null) {
                displayMessagePane(obstacle.getMessage(), (NPC) obstacle);
            } else {
                //TODO inefficient/pointless, find better way to do this
                ArrayList<Enemy> enemy = new ArrayList<>();
                enemy.add((Enemy) ((NPC) obstacle).getNPC());
                //displayBattlePanel(enemy, obstacle);
            }
        } else if (((NPC) obstacle).getNPC() instanceof Neutral) {
            ((NPC) obstacle).questInteraction();
            displayMessagePane(obstacle.getMessage(), (NPC) obstacle);
        }

    }

    private void initCollections() {
        try {
            String mapLoc = "Saves\\Save01\\Maps\\Map0-0.map";
            map = new MapContainer(player, mapLoc);
            this.setId(map.getIdName());
            //mapItems = mapParser.parseMap(map);
            setCurrentMapFile(mapLoc);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates map items when the player moves to the next region of the map.
     * @param exit The invisible Exit sprite that contains the next map and next player coordinates.
     */
    private void updateMapItems(Exit exit) {
        try {
            String nextMap = exit.getNextMapLocation();
            map.loadNewFile(nextMap);
            setCurrentMapFile(nextMap); //Sets the current map file and map items to the new map.
            this.setId(map.getIdName());
        } catch (FileNotFoundException e) {
            System.out.println("File not found???");
        } catch (IOException e) {
            System.out.println("Something broke I/O");
        }
    }

    private void drawLayers(GraphicsContext gc) {
        map.getUnderLayer().forEach(sprite -> sprite.render(gc));
        updatePlayer(gc);
        map.getOverLayer().forEach(sprite -> sprite.render(gc));
    }

    void updatePlayer(GraphicsContext gc) {
        if(!engaged()) move(player);
        if(player.isVisible()) player.render(gc);
    }

    void move(Sprite sprite) {
        sprite.modifyX(sprite.getDx());
        sprite.modifyY(sprite.getDy());

        //If the player tries moving out of bounds to the left.
        if (sprite.getX() < 1) sprite.setX(1);
        //If the player tries moving out of bounds upwards.
        if (sprite.getY() < 1) sprite.setY(1);
        //If the player tries moving out of bounds to the right.
        if (sprite.getX() > GameStage.WINDOW_WIDTH - 35) sprite.setX(GameStage.WINDOW_WIDTH - 35);
        //If the player tries moving out of bounds downwards.
        if (sprite.getY() > GameStage.WINDOW_HEIGHT - 70) sprite.setY(GameStage.WINDOW_HEIGHT - 70);

        if (collision()) {
            sprite.modifyX(-sprite.getDx());
            sprite.modifyY(-sprite.getDy());
        }
    }

    /**
     * Checks for player collisions upon movement.
     * @return
     */
    private boolean collision() {
        for (Sprite obstacle : map.getCollisions()) {
            if (player.getBounds().intersects(obstacle.getBounds())) {
                if(obstacle instanceof Exit) {
                    player.setX(((Exit) obstacle).getNextX()); //Sets the player to the appropriate coordinates in the new area.
                    player.setY(((Exit) obstacle).getNextY());
                    if(((Exit) obstacle).getNextMapLocation().equals("random")) {
                        try {
                            map.randomize("Saves\\Default Maps\\Maps\\tempMap.map", currentMapFile,
                                    (Exit) obstacle);
                            ((Exit) obstacle).setNextMapLocation("Saves\\Default Maps\\Maps\\tempMap.map");
                        } catch (Exception e) {
                            System.out.println("Failed to generate random map.");
                            e.printStackTrace();
                        }
                    } else {
                        updateMapItems((Exit) obstacle);	//Moves the player to the next area if they move on an exit.
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }


    private void initFlags() {
        menuCurrentlyDisplayed = false;
        battleCurrentlyDisplayed = false;
        statsCurrentlyDisplayed = false;
        inventoryCurrentlyDisplayed = false;
        lootCurrentlyDisplayed = false;
        settingsCurrentlyDisplayed = false;
        messageCurrentlyDisplayed = false;
        equipmentCurrentlyDisplayed = false;
    }

    /**
     * Checks to see if the player is currently engaged in some menu.
     * @return Whether or not the player is engaged.
     */
    boolean engaged() {
        return (menuCurrentlyDisplayed || battleCurrentlyDisplayed
                || statsCurrentlyDisplayed || inventoryCurrentlyDisplayed
                || lootCurrentlyDisplayed || settingsCurrentlyDisplayed
                || messageCurrentlyDisplayed || equipmentCurrentlyDisplayed);
    }

    boolean engagedMinusMenu() {
        return (battleCurrentlyDisplayed
                || statsCurrentlyDisplayed || inventoryCurrentlyDisplayed
                || lootCurrentlyDisplayed || settingsCurrentlyDisplayed
                || messageCurrentlyDisplayed || equipmentCurrentlyDisplayed);
    }

    public String getCurrentMapFile() {
        return currentMapFile;
    }

    public void setCurrentMapFile(String currentMapFile) {
        this.currentMapFile = currentMapFile;
    }

    public void setMapContainer(MapContainer mc) {
        this.currentMapFile = mc.getLocation();
        this.map = mc;
    }

    public boolean isHostile() {
        return hostile;
    }

    public void setHostile(boolean hostile) {
        this.hostile = hostile;
    }

    public void setMapItems(ArrayList<Sprite> mapItems) {
        this.map.setMapItems(mapItems);
        this.hostile = map.isHostile();
        this.setId(map.getIdName());
    }


    void toggleMenuPane() {
        if(!menuCurrentlyDisplayed) {
            this.getChildren().add(menu);
            this.setMargin(menu, new Insets(10, 940, 470, 10));
            menuCurrentlyDisplayed = true;

            menu.requestFocus();
        } else {
            this.getChildren().remove(menu);
            menuCurrentlyDisplayed = false;
            this.requestFocus();
        }
    }

    void displayInventoryPane() {
        if(!inventoryCurrentlyDisplayed) {
            ScrollingInventoryPane pane = new ScrollingInventoryPane(this,  player.getPlayer());
            this.getChildren().add(pane);
            inventoryCurrentlyDisplayed = true;
            pane.requestFocus();
        }
    }

    void removeInventoryPane(ScrollingInventoryPane pane) {
        this.getChildren().remove(pane);
        inventoryCurrentlyDisplayed = false;
        this.requestFocus();
    }

    void displayLootPane(Lootable loot) {
        if(!lootCurrentlyDisplayed) {
            LootPane lp = new LootPane(this, loot, player.getPlayer());
            this.getChildren().add(lp);
            lootCurrentlyDisplayed = true;
            lp.requestFocus();
        }
    }

    void removeLootPane(LootPane pane) {
        this.getChildren().remove(pane);
        lootCurrentlyDisplayed = false;
        this.requestFocus();
    }

    void displayOptionsPane() {
        if(!settingsCurrentlyDisplayed) {
            OptionsPane op = new OptionsPane(this, player.getPlayer());
            this.getChildren().add(op);
            settingsCurrentlyDisplayed = true;
            op.requestFocus();
        }
    }

    void removeOptionsPane(OptionsPane pane) {
        this.getChildren().remove(pane);
        settingsCurrentlyDisplayed = false;
        this.requestFocus();
    }

    void displayMessagePane(String message) {
        MessagePane mp = new MessagePane(message, player.getPlayer(), this);
        messageInfo(mp);

    }

    void displayMessagePane(String[] message, NPC npc) {
        MessagePane mp = new MessagePane(message, player.getPlayer(), this, npc);
        messageInfo(mp);
    }

    private void messageInfo(MessagePane message) {
        if(!messageCurrentlyDisplayed) {
            messageCurrentlyDisplayed = true;
            if (player.getY() < GameStage.WINDOW_HEIGHT / 2) {
                this.setMargin(message, new Insets(GameStage.WINDOW_HEIGHT - message.getMaxHeight() - 80, 0, 0, 0));
            } else {
                this.setMargin(message, new Insets(0, 0, GameStage.WINDOW_HEIGHT - message.getMaxHeight() - 35, 0));
            }

            this.getChildren().add(message);
            message.requestFocus();
        }
    }

    void removeMessagePane(MessagePane pane) {
        t = new Timeline(new KeyFrame(Duration.millis(150), event -> {}));
        t.setOnFinished(event -> messageCurrentlyDisplayed = false);
        t.play();

        this.getChildren().remove(pane);
        this.requestFocus();
    }

    void displayStatsPane() {
        if(!statsCurrentlyDisplayed) {
            statsCurrentlyDisplayed = true;
            StatsPane pane = new StatsPane(this, player);
            this.getChildren().add(pane);
            pane.requestFocus();
        }
    }

    void removeStatsPane(StatsPane pane) {
        this.getChildren().remove(pane);
        statsCurrentlyDisplayed = false;
        this.requestFocus();
    }

    public PlayerSprite getPlayer() {
        return this.player;
    }
    public void setPlayer(PlayerSprite player) { this.player = player; }
    public Stage getPrimaryStage() { return primaryStage; }
}
