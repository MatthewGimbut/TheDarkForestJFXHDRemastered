package gui;

import battle.BattleHandler;
import characters.Enemy;
import characters.Neutral;
import characters.Player;
import gui.items.LootPane;
import gui.items.ScrollingInventoryPane;
import gui.menus.MenuPane;
import gui.menus.OptionsPane;
import gui.quests.JournalPane;
import gui.quests.NewQuestPane;
import gui.quests.QuestSuccess;
import gui.quests.QuestSummary;
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
import java.util.Random;

import java.lang.reflect.Array;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import javafx.scene.layout.BorderPane;
import sun.awt.image.ImageWatched;

import java.util.Iterator;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GamePane extends StackPane {

    private Stage primaryStage;
    private boolean menuCurrentlyDisplayed, statsCurrentlyDisplayed,
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
    private LinkedList<Sprite> playerProjectiles = new LinkedList<Sprite>();
    private ArrayList<Sprite> enemies = new ArrayList<Sprite>();
    private final int MAX_PLAYER_PROJECTILES_ON_SCREEN = 3;

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
                    case "L": //Projectile attack for demo purposes until they are added to player
                        switch (player.getImageLocation()) {
                            case Player.FACING_NORTH:
                                projectileAttack(player.getX(), player.getY() - player.getHeight(), 0, -5);
                                break;
                            case Player.FACING_SOUTH:
                                projectileAttack(player.getX(), player.getY() + player.getHeight(), 0, 5);
                                break;
                            case Player.FACING_EAST:
                                projectileAttack(player.getX() + player.getWidth(), player.getY(), 5, 0);
                                break;
                            case Player.FACING_WEST:
                                projectileAttack(player.getX() - player.getWidth(), player.getY(), -5, 0);
                                break;
                        }
                        break;
                    case "K": //Magic attack
                        //TODO magic attack
                        break;
                    case "ESCAPE":
                        if(!engagedMinusMenu()) toggleMenuPane();
                        break;
                    case "I":
                        if(!inventoryCurrentlyDisplayed) displayInventoryPane();
                        break;
                }
            }
        });

        this.setOnKeyReleased(event -> {
            String code = event.getCode().toString();
            switch(code) {
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
                        BattleHandler.attack(player.getPlayer(), e);
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
        if(playerProjectiles.size() <= MAX_PLAYER_PROJECTILES_ON_SCREEN - 1) {
            Sprite interact = new Sprite(x, y, "file:Images\\fire.png");
            interact.setVelocity(dx, dy);
            interact.setObstacle(false);
            interact.render(gc);
            playerProjectiles.add(interact);
        } else {
            System.out.println("Max playerProjectiles");
        }
    }

    /**
     * Method which calls move and then renders every projectile on screen.
     */
    private void updateProjectiles(GraphicsContext gc) {
        if(!engaged()) {
            moveProjectiles();
        }
        playerProjectiles.forEach(s -> s.render(gc));
    }

    /**
     * Method which iterates the movement of all the playerProjectiles on the screen.
     * Projectiles will continuously move until colliding with an object or exiting game bounds.
     * If a projectile goes outside of the game window bounds it is despawned.
     * If a projectile collides with an enemy damage is done and the projectile is removed.
     * If a projectile collides with a non-enemy the projectile is removed.
     */
    private void moveProjectiles() {
        Iterator<Sprite> it = playerProjectiles.iterator();
        Sprite s;
        while(it.hasNext()) {
            s = it.next();

            s.modifyX(s.getDx());
            s.modifyY(s.getDy());

            if(s.getX() < 1 || s.getY() < 1 || s.getX() > GameStage.WINDOW_WIDTH - 35 ||
                    s.getY() > GameStage.WINDOW_HEIGHT - 70) {
                it.remove(); // if the projectile hits the edge of the screen despawn it
            }

            Sprite collision = projectileCollision(s);

            if(collision instanceof NPC && ((NPC) collision).getNPC() instanceof Enemy) { //Collision with enemy
                NPC np = (NPC) collision;
                Enemy e = (Enemy) np.getNPC();

                if(e.isActive()) {
                    BattleHandler.attack(player.getPlayer(), e);
                }

                it.remove();
                System.out.println("Test: Collision with enemy success");
            } else if(collision != null && !(collision instanceof LowerLayer)) {
                it.remove();
                System.out.println("Test: Collision with non-enemy success");
            }
        }
    }

    /**
     * Detects projectile collision for a given sprite.
     * A projectile can collide with anything but we only care if it collides with an enemy.
     * Collision is checked after the projectile movement is updated.
     * @param s The sprite of the projectile
     * @return The collided obstacle, or null if no obstacle
     */
    private Sprite projectileCollision(Sprite s) {
        for(Sprite obstacle : map.getCollisions()) {
            if(s.getBounds().intersects(obstacle.getBounds())) {
                return obstacle;
            }
        }
        return null; // no collisions
    }

    /**
     * A method to despawn all the player playerProjectiles on the map.
     * Used when the player switches from a projectile weapon to a non-projectile weapon.
     */
    public void despawnPlayerProjectiles() {
        playerProjectiles.clear();
    }

    private void updateEnemies(GraphicsContext gc) {
        if(!engaged()) {
            moveEnemies(); // in future use enemyAI to determine individual AI
        }
        enemies.forEach(s -> s.render(gc));
    }

    /**
     * Private method which moves all of the enemies on screen towards the player
     */
    private void moveEnemies() {
        Iterator<Sprite> it = enemies.iterator();
        Random random = new Random();
        Enemy e;
        Sprite s;
        int playerX = player.getX();
        int playerY = player.getY();
        int deltaX, deltaY;
        while(it.hasNext()) {
            s = it.next();
            e = ((Enemy) ((NPC)s).getNPC());

            e.setActive(true); // TODO remove once it works properly

            if(e.isActive()) { // only do movement if the enemy is active
                deltaX = s.getX() - playerX; // positive means enemy is father right than player
                deltaY = s.getY() - playerY; // positive means enemy is father down than player

                s.setDx(2);
                s.setDy(2);

                LinkedList<Cardinal> path = s.getPath();

                if(path != null && !path.isEmpty()) { // path is created, so use it until it is done
                    Cardinal direction = path.remove(0);

                    switch(direction) {
                        case North:
                            s.modifyY(-s.getDy()); // move up
                            break;
                        case South:
                            s.modifyY(s.getDy()); // move down
                            break;
                        case East:
                            s.modifyX(s.getDx()); // move right
                            break;
                        case West:
                            s.modifyX(-s.getDx()); // move left
                            break;
                    }

                    // no need to check for collision since that was done in path generation
                } else { // no path created
                    double ratio = 1.0; // ratio of deltaX / deltaY
                    if(deltaY != 0) {
                        ratio = Math.abs((1.0*deltaX) / (1.0*(Math.abs(deltaY)+Math.abs(deltaX))));
                    }

                    double num = random.nextDouble();
                    if(num <= ratio) { // move in x direction
                        if(deltaX > 0) { // move left
                            s.modifyX(-s.getDx());
                        } else { // move right
                            s.modifyX(s.getDx());
                        }
                        s.modifyY(0);
                    } else { // move in y direction
                        if(deltaY > 0) { // move up
                            s.modifyY(-s.getDy());
                        } else { // move down
                            s.modifyY(s.getDy());
                        }
                        s.modifyX(0);
                    }

                    if (s.getX() < 1) s.setX(1);
                    if (s.getY() < 1) s.setY(1);
                    if (s.getX() > GameStage.WINDOW_WIDTH - 35) s.setX(GameStage.WINDOW_WIDTH - 35);
                    if (s.getY() > GameStage.WINDOW_HEIGHT - 70) s.setY(GameStage.WINDOW_HEIGHT - 70);

                    if(enemyCollision(s)) { // back the enemy up if it collides with something
                        Cardinal direction;
                        Sprite collision = getCollisionObstacle(s);
                        boolean intersectedPlayer = s.intersects(player);
                        if(num <= ratio) { // move in x direction
                            if(deltaX > 0) { // collision on left, move right
                                s.modifyX(s.getDx());
                                direction = Cardinal.West;
                            } else { // collision on right, move left
                                s.modifyX(-s.getDx());
                                direction = Cardinal.East;
                            }
                            s.modifyY(0);
                        } else { // move in y direction
                            if(deltaY > 0) { // collision up, move down
                                s.modifyY(s.getDy());
                                direction = Cardinal.North;
                            } else { // collision down, move up
                                s.modifyY(-s.getDy());
                                direction = Cardinal.South;
                            }
                            s.modifyX(0);
                        }
                        if(!intersectedPlayer) {
                            s.setPath(pathAroundSprite(s.clone(), collision, direction));
                        }
                    }
                }

                deltaX = s.getX() - playerX;
                deltaY = s.getY() - playerY;
                if(Math.abs(deltaX) < 32 && Math.abs(deltaY) < 32 && !e.getAttacking()) { // TODO do enemy attacking, also make it timer based so enemies cannot attack infinitely
                    System.out.println("Enemy attack");
                }
            }
        }
    }

    /**
     * Determines a path around a colliding sprite for another sprite.
     * @param s The sprite to path
     * @param collision The sprite to path around
     * @param direction The direction the colliding sprite is with respect to the sprite
     * @return The path
     */
    private LinkedList<Cardinal> pathAroundSprite(Sprite s, Sprite collision, Cardinal direction) {
        LinkedList<Cardinal> result = new LinkedList<>();

        int initialX = s.getX();
        int initialY = s.getY();

        if(direction == Cardinal.North || direction == Cardinal.South) { // vertical collision, move sideways
            int deltaXFromLeft = Math.abs(collision.getX() - (s.getX() + s.getWidth() + 5));
            int deltaXFromRight = Math.abs(collision.getX() + collision.getWidth() + 5 - s.getX());

            int deltaY;
            if(direction == Cardinal.North) { // collision is north, so path north
                deltaY = Math.abs(collision.getY() - (s.getY() + s.getHeight() + 5));
            } else { // collision is south, so path south
                deltaY = Math.abs(s.getY() - (collision.getY() + collision.getHeight() + 5));
            }

            if(deltaXFromLeft < deltaXFromRight) { // try the left side first

                if(attemptMoveHorizontally(s, deltaXFromLeft, initialX, result, false)) { // now try to path vertically
                    return moveVerticallyUntilCanRealignHorizontally(s, deltaY, initialX, result, direction == Cardinal.North, true);
                } else { // move failed, try other direction
                    result.clear();
                    if(attemptMoveHorizontally(s, deltaXFromRight, initialX, result, true)) { // now try to path vertically
                        return moveVerticallyUntilCanRealignHorizontally(s, deltaY, initialX, result, direction == Cardinal.North, false);
                    }
                }

            } else { // try the right side

                if(attemptMoveHorizontally(s, deltaXFromRight, initialX, result, true)) { // now try to path vertically
                    return moveVerticallyUntilCanRealignHorizontally(s, deltaY, initialX, result, direction == Cardinal.North, false);
                } else { // move failed, try other direction
                    result.clear();
                    if(attemptMoveHorizontally(s, deltaXFromLeft, initialX, result, false)) { // now try to path vertically
                        return moveVerticallyUntilCanRealignHorizontally(s, deltaY, initialX, result, direction == Cardinal.North, true);
                    }
                }

            }
        } else { // horizontal collision, move vertically
            int deltaYFromTop = Math.abs(s.getY() + s.getHeight() + 5 - collision.getY());
            int deltaYFromBottom = Math.abs(s.getY() - (collision.getY() + collision.getHeight() + 5));

            int deltaX;
            if(direction == Cardinal.East) { // collision is east, so path east
                deltaX = Math.abs(collision.getX() + collision.getWidth() + 5 - s.getX());
            } else { // collision is west so path west
                deltaX = Math.abs(collision.getX() - (s.getX() + s.getWidth() + 5));
            }

            if(deltaYFromTop < deltaYFromBottom) { // try the top side first
                if(attemptMoveVertically(s, deltaYFromTop, initialY, result, true)) { // now try to path horizontally
                    return moveHorizontallyUntilCanRealignVertically(s, deltaX, initialY, result, direction == Cardinal.East, false);
                } else {
                    result.clear();
                    if(attemptMoveVertically(s, deltaYFromBottom, initialY, result, false)) { // now try to path horizontally
                        return moveHorizontallyUntilCanRealignVertically(s, deltaX, initialY, result, direction == Cardinal.East, true);
                    }
                }
            } else { // try the bottom side first
                if(attemptMoveVertically(s, deltaYFromBottom, initialY, result, false)) { // now try to path horizontally
                    return moveHorizontallyUntilCanRealignVertically(s, deltaX, initialY, result, direction == Cardinal.East, true);
                } else {
                    result.clear();
                    if(attemptMoveVertically(s, deltaYFromTop, initialY, result, true)) {
                        return moveHorizontallyUntilCanRealignVertically(s, deltaX, initialY, result, direction == Cardinal.East, false);
                    }
                }
            }
        }


        return result;
    }

    /**
     * If the given sprite is able to move left the given amount of distance without collisions.
     * @param s The sprite to move
     * @param deltaX The distance to move
     * @param initialX The initial X position before the move was attempted
     * @param result The result list of Cardinals to make the path
     * @param right If the movement is to the right
     * @return If the move was successful
     */
    private boolean attemptMoveHorizontally(Sprite s, int deltaX, int initialX, LinkedList<Cardinal> result, boolean right) {
        int amountMovedHorizontally = 0;
        Cardinal direction = right ? Cardinal.East : Cardinal.West;

        // move the sprite deltaX distance to the (right/left) in steps of s.getWidth()
        while(tryMoveHorizontally(s,
                amountMovedHorizontally+15 > deltaX ? deltaX-amountMovedHorizontally : 15,
                right) && amountMovedHorizontally < deltaX) {
            amountMovedHorizontally += amountMovedHorizontally+15 > deltaX ? deltaX-amountMovedHorizontally : 15;
        }

        if(amountMovedHorizontally == deltaX) { // if the move was successful add the cardinals to the list
            int temp = amountMovedHorizontally;
            while(temp > 0) {
                result.add(direction); // add the appropriate amount of directional Cardinals to the list
                temp -= s.getDx();
            }
        } else {
            s.setX(initialX);
        }

        return amountMovedHorizontally == deltaX;
    }

    /**
     * If the given sprite is able to move left the given amount of distance without collisions.
     * @param s The sprite to move
     * @param deltaY The distance to move
     * Wparam initialY The initial Y position before the move was attempted
     * @param result The result list of Cardinals to make the path
     * @param up If the movement is up
     * @return If the move was successful
     */
    private boolean attemptMoveVertically(Sprite s, int deltaY, int initialY, LinkedList<Cardinal> result, boolean up) {
        int amountMovedVertically = 0;
        Cardinal direction = up ? Cardinal.North : Cardinal.South;

        // move the sprite deltaY distance (up/down) in steps of s.getHeight()
        while(tryMoveVertically(s,
                amountMovedVertically+15 > deltaY ? deltaY-amountMovedVertically : 15,
                up) && amountMovedVertically < deltaY) {
            amountMovedVertically += amountMovedVertically+15 > deltaY ? deltaY-amountMovedVertically : 15;
        }

        if(amountMovedVertically == deltaY) { // if the move was successful add the cardinals to the list
            int temp = amountMovedVertically;
            while(temp > 0) {
                result.add(direction); // add the appropriate amount of directional Cardinals to the list
                temp -= s.getDy();
            }
        } else {
            s.setY(initialY);
        }

        return amountMovedVertically == deltaY;
    }

    /**
     * Moves the Sprite vertically in the given direction until it is able to realign to its initial x position without collision.
     * @param s The sprite to move
     * @param deltaY The y distance to move before checking if it can move horizontally
     * @param initialX The initial X position to return to
     * @param result The current path
     * @param up If the vertical direction of movement is up
     * @param right If the horizontal direction of movement is right
     * @return The final path or null if it gets stuck
     */
    private LinkedList<Cardinal> moveVerticallyUntilCanRealignHorizontally(Sprite s, int deltaY, int initialX, LinkedList<Cardinal> result, boolean up, boolean right) {
        final int MAX_ATTEMPTS = 5;
        int counter = 0;
        do {
            attemptMoveVertically(s, deltaY, s.getY(), result, up);
            counter++;
            if(counter == MAX_ATTEMPTS) {
                return null; // path failed
            }
        } while(!attemptMoveHorizontally(s, Math.abs(s.getX() - initialX), s.getX(), result, right)); // until you can move horizontally keep moving vertically

        return result; // SUCCESS RETURN THAT SHITTTTTTT
    }

    /**
     * Moves the Sprite vertically in the given direction until it is able to realign to its initial x position without collision.
     * @param s The sprite to move
     * @param deltaX The x distance to move before checking if it can move vertically
     * @param initialY The initial Y position to return to
     * @param result The current path
     * @param right If the vertical direction of movement is right
     * @param up If the horizontal direction of movement is up
     * @return The final path or null if it gets stuck
     */
    private LinkedList<Cardinal> moveHorizontallyUntilCanRealignVertically(Sprite s, int deltaX, int initialY, LinkedList<Cardinal> result, boolean right, boolean up) {
        final int MAX_ATTEMPTS = 5;
        int counter = 0;
        do {
            attemptMoveHorizontally(s, deltaX, s.getX(), result, right);
            counter++;
            if(counter == MAX_ATTEMPTS) {
                return null; // path failed
            }
        } while(!attemptMoveVertically(s, Math.abs(s.getY() - initialY), s.getY(), result, up)); // until you can move vertically keep moving horizontally

        return result; // SUCCESS RETURN THAT SHITTTTTTT
    }

    /**
     * Attempts to move the sprite delta units veritcally.
     * If movement is unsuccessful the sprite is moved back.
     * @param s The sprite to move
     * @param delta The amount to be moved
     * @param up If the movement is up (false -> down)
     * @return If the movement is successful
     */
    private boolean tryMoveVertically(Sprite s, int delta, boolean up) {
        if(up) {
            s.modifyY(-delta);
        } else {
            s.modifyY(delta);
        }

        boolean temp = enemyCollision(s);
        if(temp) {
            s.modifyY(delta);
        } else {
            s.modifyY(-delta);
        }

        return !temp;
    }

    /**
     * Attempts to move the sprite delta units veritcally.
     * If movement is unsuccessful the sprite is moved back.
     * @param s The sprite to move
     * @param delta The amount to be moved
     * @param right If the movement is up (false -> down)
     * @return If the movement is successful
     */
    private boolean tryMoveHorizontally(Sprite s, int delta, boolean right) {
        if(right) {
            s.modifyX(delta);
        } else {
            s.modifyX(-delta);
        }

        boolean temp = enemyCollision(s);
        if(temp) {
            if(right) {
                s.modifyX(-delta);
            } else {
                s.modifyX(delta);
            }
        }
        return !temp;
    }

    /**
     * Determines if a Sprite collides with anything on screen
     * @param s The sprite to test
     * @return if the sprite collides with an object (besides itself)
     */
    private boolean enemyCollision(Sprite s) {
        if(s.intersects(player)) {
            return true;
        }

        for(Sprite obstacle : map.getCollisions()) {
            if(!s.equals(obstacle) && s.getBounds().intersects(obstacle.getBounds())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the Sprite that the given Sprite is colliding with
     * @param s The Sprite to test
     * @return The obstacle that s is colliding with (besides itself)
     */
    private Sprite getCollisionObstacle(Sprite s) {
        for(Sprite obstacle : map.getCollisions()) {
            if(!s.equals(obstacle) && s.getBounds().intersects(obstacle.getBounds())) {
                return obstacle;
            }
        }
        return null;
    }

    /**
     * Method for controlling the enemy AI.
     * Enemies will walk towards the player.
     * Enemies will attack randomly when in range and when facing the player.
     * Applies on the list of all enemies on screen.
     */
    private void enemyAI() {
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
            fillEnemies();
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
        this.despawnPlayerProjectiles();
        map.loadNewFile(nextMap);
        setCurrentMapFile(nextMap); //Sets the current map file and map items to the new map.
        this.setId(map.getIdName());
        fillEnemies();
    }

    /**
     * Fills the enemies list with all the enemies in the current map.
     */
    private void fillEnemies() {
        this.enemies.clear();
        ArrayList<Sprite> temp = map.getMapItems();
        temp.forEach(s -> {
            if(s instanceof NPC) {
                NPC np = (NPC) s;
                if(np.getNPC() instanceof Enemy) {
                    enemies.add(s);
                }
            }
        });
    }

    /**
     * Removes the enemy from the current enemies when it is killed.
     * @param s The killed enemy
     */
    public void enemyKilled(Sprite s) {
        enemies.remove(s);
        // TODO drop items and shit
        drawLayers(gc);
    }

    private void drawLayers(GraphicsContext gc) {
        map.getUnderLayer().forEach(sprite -> sprite.render(gc));
        updatePlayer(gc);
        updateProjectiles(gc);
        updateEnemies(gc);
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
        statsCurrentlyDisplayed = false;
        inventoryCurrentlyDisplayed = false;
        lootCurrentlyDisplayed = false;
        settingsCurrentlyDisplayed = false;
        messageCurrentlyDisplayed = false;
        equipmentCurrentlyDisplayed = false;
        questCurrentlyDisplayed = false;
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


    public void toggleMenuPane() {
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

    public void displayInventoryPane() {
        if(!inventoryCurrentlyDisplayed) {
            ScrollingInventoryPane pane = new ScrollingInventoryPane(this,  player.getPlayer());
            this.getChildren().add(pane);
            inventoryCurrentlyDisplayed = true;
            pane.requestFocus();
        }
    }

    public void removeInventoryPane(ScrollingInventoryPane pane) {
        this.getChildren().remove(pane);
        inventoryCurrentlyDisplayed = false;
        this.requestFocus();
    }

    public void displayLootPane(Lootable loot) {
        if(!lootCurrentlyDisplayed) {
            LootPane lp = new LootPane(this, loot, player.getPlayer());
            this.getChildren().add(lp);
            lootCurrentlyDisplayed = true;
            lp.requestFocus();
        }
    }

    public void removeLootPane(LootPane pane) {
        this.getChildren().remove(pane);
        lootCurrentlyDisplayed = false;
        this.requestFocus();
    }

    public void displayOptionsPane() {
        if(!settingsCurrentlyDisplayed) {
            OptionsPane op = new OptionsPane(this, player.getPlayer());
            this.getChildren().add(op);
            settingsCurrentlyDisplayed = true;
            op.requestFocus();
        }
    }

    public void removeOptionsPane(OptionsPane pane) {
        this.getChildren().remove(pane);
        settingsCurrentlyDisplayed = false;
        this.requestFocus();
    }

    public void displayMessagePane(String message) {
        MessagePane mp = new MessagePane(message, player.getPlayer(), this);
        messageInfo(mp);

    }

    public void displayMessagePane(String[] message, NPC npc) {
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

    public void removeMessagePane(MessagePane pane) {
        t = new Timeline(new KeyFrame(Duration.millis(150), event -> {}));
        t.setOnFinished(event -> messageCurrentlyDisplayed = false);
        t.play();

        this.getChildren().remove(pane);
        this.requestFocus();
    }

    public void displayStatsPane() {
        if(!statsCurrentlyDisplayed) {
            statsCurrentlyDisplayed = true;
            StatsPane pane = new StatsPane(this, player);
            this.getChildren().add(pane);
            pane.requestFocus();
        }
    }

    public void removeStatsPane(StatsPane pane) {
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

    public void removeNewQuestPane(NewQuestPane pane) {
        this.getChildren().remove(pane);
        questCurrentlyDisplayed = false;
        this.requestFocus();
        if(questPanelStack.size() != 0) {
            popQuestPanelStack();
        }
    }

    public void displayJournal() {
        if(!questCurrentlyDisplayed) {
            questCurrentlyDisplayed = true;
            JournalPane journal = new JournalPane(this);
            this.getChildren().add(journal);
            journal.requestFocus();
        }
    }

    public void removeJournalPane(JournalPane journal) {
        this.getChildren().remove(journal);
        questCurrentlyDisplayed = false;
        this.requestFocus();
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
        } else {
            this.requestFocus();
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
