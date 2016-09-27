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
import mapping.MapContainer;
import main.SaveManager;
import quests.Quest;
import sprites.*;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GamePane extends StackPane {

    private Stage primaryStage;
    private boolean menuCurrentlyDisplayed, battleCurrentlyDisplayed, statsCurrentlyDisplayed,
            inventoryCurrentlyDisplayed, lootCurrentlyDisplayed, settingsCurrentlyDisplayed,
            messageCurrentlyDisplayed, equipmentCurrentlyDisplayed, questCurrentlyDisplayed;
    private PlayerSprite player;
    private ArrayList<String> input;
    private MapContainer map;
    private String currentMapFile;
    private boolean hostile;
    private MenuPane menu;
    private GraphicsContext gc;
    private Timeline t;
    private QuestSummary qs;
    private Queue<BorderPane> questPanelStack = new LinkedList<BorderPane>();

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
                    case "J": //Physical attack
                        switch (player.getImageLocation()) {
                            case Player.FACING_NORTH:
                                physicalAttack(player.getX(), player.getY() - player.getHeight(), 24);
                                break;
                            case Player.FACING_SOUTH:
                                physicalAttack(player.getX(), player.getY() + player.getHeight(), 24);
                                break;
                            case Player.FACING_EAST:
                                physicalAttack(player.getX() + player.getWidth(), player.getY(), 24);
                                break;
                            case Player.FACING_WEST:
                                physicalAttack(player.getX() - player.getWidth() - 5, player.getY(), 24);
                                break;
                        }
                        break;
                    case "K": //Magic attack
                        //TODO magic attack
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

        qs = new QuestSummary(this);
        this.setMargin(qs, new Insets(5, 5, 0, 795));
        this.getChildren().add(qs);

        AnimationTimer animate = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                gc.clearRect(0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
                drawLayers(gc);
            }
        };
        animate.start();
    }

    public void displayQuestSuccessPane(quests.Quest quest) {
        QuestSuccess qs = new QuestSuccess(player, this, quest);
        if(!questCurrentlyDisplayed) {
            questCurrentlyDisplayed = true;
            this.getChildren().add(qs);
            qs.requestFocus();
        } else {
            questPanelStack.add(qs);
        }
    }

    public void removeQuestSuccessPane(QuestSuccess qs) {
        this.getChildren().remove(qs);
        questCurrentlyDisplayed = false;
        this.requestFocus();
        if(questPanelStack.size() != 0) {
            popQuestPanelStack();
        }
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
     * Method for determining physical attack interactions.
     * Creates an invisible Rectangle2D in the direction the player is facing.
     * If that Rectangle2D hits an active enemy damage is done
     * @param x The x coordinate of where to set the Rectangle2D
     * @param y The y coordinate of where to set the Rectangle2D
     * @param size The size of the Rectangle2D (pre determined values)
     */
    private void physicalAttack(int x, int y, int size) {
        Sprite interact = new Sprite(x, y, "file:Images\\Blank24x24.png"); //TODO make this variable sized
        interact.setObstacle(false);
        interact.render(gc);

        ArrayList<Sprite> items = (ArrayList<Sprite>) map.getMapItems().stream()
                .filter(i -> !(i instanceof LowerLayer))
                .collect(Collectors.toList());

        for(int i = 0; i < items.size(); i++) {
            if (interact.getBounds().intersects(items.get(i).getBounds())) {
                if(items.get(i) instanceof  NPC && ((NPC)items.get(i)).getNPC() instanceof Enemy) {
                    Enemy e = ((Enemy)(((NPC)items.get(i))).getNPC());
                    if(e.isActive()) { //if the enemy is not active then do nothing
                        //TODO deal damage
                        //BattleHandler.attack(player.getPlayer(), e);
                    }
                }
            }
        }
    }

    /**
     * Method for determining projectile attack interactions.
     * Creates an invisible Rectangle2D for hitbox detection.
     * Creates a visible sprite to represent the projectile.
     * If the Rectangle2D collides with an enemy damage is done.
     * @param x Initial x coordinate of the projectile
     * @param y Initial y coordinate of the projectile
     * @param dx x speed of the projectile
     * @param dy y speed of the projectile
     */
    private void projectileAttack(int x, int y, int dx, int dy) {
        //TODO stuff
        //Figure out how to detect if the projectile hits at any point in the game
        //Instead of just after firing the projectile
    }

    /**
     * Method for controlling the enemy AI.
     * Enemies will walk in random directions but tend towards the player.
     * Enemies will attack randomly when in range and when facing the player.
     * @param enemies The enemies which need AI on the screen (all active enemies)
     */
    private void enemyAI(List<Enemy> enemies) {
        //TODO lots of shit
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
            String mapLoc = "Saves\\Save01\\Maps\\Map0-0.json";
            //String mapLoc = "Saves\\Save01\\Maps\\JSONTest.map";
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
        String nextMap = exit.getNextMapLocation();
        map.loadNewFile(nextMap);
        setCurrentMapFile(nextMap); //Sets the current map file and map items to the new map.
        this.setId(map.getIdName());
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
                            map.randomize("Saves\\Default Maps\\Maps\\tempMap.json", currentMapFile,
                                    (Exit) obstacle);
                            ((Exit) obstacle).setNextMapLocation("Saves\\Default Maps\\Maps\\tempMap.json");
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
    public boolean engaged() {
        return (menuCurrentlyDisplayed || battleCurrentlyDisplayed
                || statsCurrentlyDisplayed || inventoryCurrentlyDisplayed
                || lootCurrentlyDisplayed || settingsCurrentlyDisplayed
                || messageCurrentlyDisplayed || equipmentCurrentlyDisplayed
                || questCurrentlyDisplayed);
    }

    public boolean engagedMinusMenu() {
        return (battleCurrentlyDisplayed
                || statsCurrentlyDisplayed || inventoryCurrentlyDisplayed
                || lootCurrentlyDisplayed || settingsCurrentlyDisplayed
                || messageCurrentlyDisplayed || equipmentCurrentlyDisplayed
                || questCurrentlyDisplayed);
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

    public void displayNewQuestPane(Quest quest) {
        NewQuestPane nqp = new NewQuestPane(this, quest);
        if(!questCurrentlyDisplayed) {
            questCurrentlyDisplayed = true;
            this.getChildren().add(nqp);
            nqp.requestFocus();
        } else {
            questPanelStack.add(nqp);
        }
    }

    void removeNewQuestPane(NewQuestPane pane) {
        this.getChildren().remove(pane);
        questCurrentlyDisplayed = false;
        this.requestFocus();
        if(questPanelStack.size() != 0) {
            popQuestPanelStack();
        }
    }

    public void resetMessagePaneFocus() {
        MessagePane pane = null;

        for(Object o : this.getChildren()) {
            if(o instanceof MessagePane) {
                pane = (MessagePane) o;
            }
        }

        if(pane != null) {
            pane.requestFocus();
        }
    }

    public void popQuestPanelStack() {
        BorderPane bp = questPanelStack.remove();
        questCurrentlyDisplayed = true;
        this.getChildren().add(bp);
        bp.requestFocus();
    }

    public PlayerSprite getMainPlayerSprite() {
        return this.player;
    }
    public void setPlayer(PlayerSprite player) { this.player = player; }
    public Stage getPrimaryStage() { return primaryStage; }
    public QuestSummary getQuestSummaryPane() {
        return this.qs;
    }
}
