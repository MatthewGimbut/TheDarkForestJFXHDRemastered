package gui;

import battle.BattleHandler;
import characters.Character;
import characters.Enemy;
import characters.Neutral;
import characters.Player;
import gui.quests.QuestSummary;
import items.Weapons.Bow;
import items.Weapons.Crossbow;
import items.Weapons.Magic;
import items.Weapons.Projectile;
import items.SpellType;
import items.ammunition.Ammunition;
import items.ammunition.Arrow;
import items.ammunition.Bolt;
import items.ammunition.OutOfAmmoException;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.AudioManager;
import main.GameStage;
import mapping.MapContainer;
import main.SaveManager;
import sprites.*;
import java.util.Random;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GamePane extends StackPane {

    private Stage primaryStage;
    private PlayerSprite player;
    private ArrayList<String> input;
    private MapContainer map;
    private String currentMapFile;
    private boolean hostile;
    private boolean projectileOnCooldown;
    private GraphicsContext gc;
    private QuestSummary qs;
    public StatusPeekPane sppHealth, sppMana, sppStamina;
    private LinkedList<Sprite> playerProjectiles = new LinkedList<Sprite>();
    private ArrayList<NPC> enemies = new ArrayList<>();
    private ArrayList<StatusPeekPane> enemyHealthBars = new ArrayList<>();
    private final int MAX_PLAYER_PROJECTILES_ON_SCREEN = 10;
    public Timeline manaRegen, hpRegen, staminaRegen;
    public static final String STYLE_HIGH_HP =  "-fx-accent: green;";
    public static final String STYLE_LOW_HP =  "-fx-accent: red;";
    public static final String STYLE_MANA =  "-fx-accent: blue;";
    public static final String STYLE_MEDIUM_HP =  "-fx-accent: orange;";
    public static final String STYLE_STAMINA =  "-fx-accent: gold;";
    public static final String STYLE_XP = "-fx-accent: DeepSkyBlue;";
    public AnimatedSprite as;
    public String saveLoc;
    public String saveDir;
    public static final int ARC_SIZE = 22;
    public main.UIManager uiManager;

    public GamePane(Stage primaryStage, String saveLoc, String saveDir) {
        this.saveDir = saveDir;
        this.saveLoc = saveLoc;
        input = new ArrayList<>();
        //initFlags();
        this.primaryStage = primaryStage;
        Canvas canvas = new Canvas(GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
        this.getChildren().add(canvas);

        player = new PlayerSprite(200, 100, new Player("Matthew Gimbut"));
        this.uiManager = new main.UIManager(this);

        /*String[] portal = new String[11];
        for(int i = 0; i < portal.length; i++) {
            int z = i+1;
            portal[i] = "file:Images\\Portal\\portal" + z + ".png";
        }
        as = new AnimatedSprite(600, 100, portal);*/

        projectileOnCooldown = false;

        gc = canvas.getGraphicsContext2D();

        this.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if(!uiManager.engagedMinusMenu()) {
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
                        Player p = player.getPlayer();
                        if(!projectileOnCooldown && (p.getWeaponHandR() != null //If the player has a weapon equipped
                                && p.getWeaponHandR() instanceof Projectile) //If the weapon is a projectile weapon
                                && !(p.getWeaponHandR() instanceof  Magic) //but NOT a magic weapon
                                && ((p.getWeaponHandR() instanceof Bow && p.getAmmo() instanceof Arrow) //And matching ammo types
                                || (p.getWeaponHandR() instanceof Crossbow && p.getAmmo() instanceof Bolt))) { //Ugh so many conditions to fire a fucking piece of wood
                            projectileOnCooldown = true;
                            int cost = player.getPlayer().getWeaponHandR().getStaminaCost();
                            try {
                                if(player.getPlayer().getCurrentStamina() > cost) {
                                    player.getPlayer().getAmmo().decrementAmmoCount();
                                    player.getPlayer().modifyCurrentStamina(-cost);
                                    sppStamina.update();
                                    switch (player.getImageLocation()) { //Player size values reduced in certain places to make the projectiles look centered
                                        case Player.FACING_NORTH:
                                            projectileAttack(player.getX() + 8, player.getY() - (player.getHeight() - 20), 0,
                                                    -((Projectile) player.getPlayer().getWeaponHandR()).getProjectileSpeed(),
                                                    player.getPlayer().getAmmo().northLaunchImageLocation());
                                            break;
                                        case Player.FACING_SOUTH:
                                            projectileAttack(player.getX() + 8, player.getY() + player.getHeight(), 0,
                                                    ((Projectile) player.getPlayer().getWeaponHandR()).getProjectileSpeed(),
                                                    player.getPlayer().getAmmo().southLaunchImageLocation());
                                            break;
                                        case Player.FACING_EAST:
                                            projectileAttack(player.getX() + player.getWidth(), player.getY() + 12,
                                                    ((Projectile) player.getPlayer().getWeaponHandR()).getProjectileSpeed(), 0,
                                                    player.getPlayer().getAmmo().eastLaunchImageLocation());
                                            break;
                                        case Player.FACING_WEST:
                                            projectileAttack(player.getX() - player.getWidth(), player.getY() + 12,
                                                    -((Projectile) player.getPlayer().getWeaponHandR()).getProjectileSpeed(), 0,
                                                    player.getPlayer().getAmmo().westLaunchImageLocation());
                                            break;
                                    }
                                    if(player.getPlayer().getAmmo().getCount() == 0) {
                                        player.getPlayer().unequipUpdateStats(player.getPlayer().getAmmo());
                                        player.getPlayer().getInventory().remove(player.getPlayer().getAmmo());
                                    }
                                }
                            } catch (NullPointerException e) {
                                System.out.println("Player has no ammo equipped!");
                            } catch (OutOfAmmoException e1) {
                                Ammunition a = player.getPlayer().getAmmo();
                                player.getPlayer().unequip(a);
                                player.getPlayer().getInventory().remove(a);
                                System.out.println(e1.getMessage());
                            }
                            startCooldown(player.getPlayer().getSpeed());
                        }
                        break;
                    case "K": //Magic attack
                        if(!projectileOnCooldown && (player.getPlayer().getLeftHand() instanceof Magic || player.getPlayer().getWeaponHandR() instanceof Magic)) {
                            projectileOnCooldown = true;
                            Magic m;
                            SpellType st;
                            if(player.getPlayer().getLeftHand() != null) {
                                m = (Magic) player.getPlayer().getLeftHand();
                                st = m.getSpellType();
                            } else {
                                m = (Magic) player.getPlayer().getWeaponHandR();
                                st = m.getSpellType();
                            }
                            if(player.getPlayer().getCurrentMana() > m.getManaCost()) {
                                player.getPlayer().modifyCurrentMana(-m.getManaCost());
                                sppMana.update();
                                //manaRegen.play();
                                switch (player.getImageLocation()) {
                                    case Player.FACING_NORTH:
                                        projectileAttack(player.getX(), player.getY() - player.getHeight(),
                                                0, -st.getBaseProjectileSpeed(), st.northCastImageLocation());
                                        break;
                                    case Player.FACING_SOUTH:
                                        projectileAttack(player.getX(), player.getY() + player.getHeight(),
                                                0, st.getBaseProjectileSpeed(), st.southCastImageLocation());
                                        break;
                                    case Player.FACING_EAST:
                                        projectileAttack(player.getX() + player.getWidth(), player.getY(),
                                                st.getBaseProjectileSpeed(), 0, st.eastCastImageLocation());
                                        break;
                                    case Player.FACING_WEST:
                                        projectileAttack(player.getX() - player.getWidth(), player.getY(),
                                                -st.getBaseProjectileSpeed(), 0, st.westCastImageLocation());
                                        break;
                                }
                            } else {
                                System.out.println("Not enough mana.");
                            }
                            startCooldown(player.getPlayer().getSpeed());
                        }
                        break;
                    case "ESCAPE":
                        if(!uiManager.engagedMinusMenu()) {
                            uiManager.toggleMenuPane();
                        }
                        break;
                    case "I":
                        if(!uiManager.isInventoryCurrentlyDisplayed()) uiManager.displayInventoryPane();
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
        setMargin(qs, new Insets(5, 5, 0, 795));

        HBox box = new HBox(50);
        sppHealth = new StatusPeekPane(this, StatusPeekPane.HEALTH);
        sppMana = new StatusPeekPane(this, StatusPeekPane.MANA);
        sppStamina = new StatusPeekPane(this, StatusPeekPane.STAMINA);
        sppHealth.setVisible(false);
        sppMana.setVisible(false);
        sppStamina.setVisible(false);
        box.getChildren().addAll(sppHealth, sppMana, sppStamina);
        setMargin(box, new Insets(0, 5, 5, 10));

        this.getChildren().addAll(box, qs);

        final int fadePause = 2500;

        /* Timer that is responsible for HP regen. */
        hpRegen = new Timeline(new KeyFrame(Duration.millis(player.getPlayer().getHpRegen()), event -> {
            Player p = player.getPlayer();
            if(!uiManager.engaged()) {
                if(p.getCurrentHP() < p.getMaxHP()) {
                    p.modifyCurrentHP(1);
                    sppHealth.update();
                } else { //Mana == Max mana, no more need to restore
                    p.setCurrentHP(p.getMaxHP());
                    PauseTransition delay = new PauseTransition(Duration.millis(fadePause));
                    delay.setOnFinished(event2 -> fadeOutStatus(sppHealth));
                    delay.play();
                    hpRegen.stop();
                }
            }
        }));
        hpRegen.setCycleCount(Animation.INDEFINITE);

        /* Timer that is responsible for mana regen. */
        manaRegen = new Timeline(new KeyFrame(Duration.millis(player.getPlayer().getManaRegen()), event -> {
            Player p = player.getPlayer();
            if(!uiManager.engaged()) {
                if(p.getCurrentMana() < p.getMaxMana()) {
                    p.modifyCurrentMana(1);
                    sppMana.update();
                } else { //Health == Max health, no more need to restore
                    p.setCurrentMana(p.getMaxMana());
                    PauseTransition delay = new PauseTransition(Duration.millis(fadePause));
                    delay.setOnFinished(event2 -> fadeOutStatus(sppMana));
                    delay.play();
                    manaRegen.stop();
                }
            }
        }));
        manaRegen.setCycleCount(Animation.INDEFINITE);

        /* Timer that is responsible for stamina regen. */
        staminaRegen = new Timeline(new KeyFrame(Duration.millis(player.getPlayer().getStaminaRegen()), event -> {
            Player p = player.getPlayer();
            if(!uiManager.engaged()) {
                if(p.getCurrentStamina() < p.getMaxStamina()) {
                    p.modifyCurrentStamina(1);
                    sppStamina.update();
                } else {
                    p.setCurrentStamina(p.getMaxStamina());
                    PauseTransition delay = new PauseTransition(Duration.millis(fadePause));
                    delay.setOnFinished(event1 -> fadeOutStatus(sppStamina));
                    delay.play();
                    staminaRegen.stop();
                }
            }
        }));
        staminaRegen.setCycleCount(Animation.INDEFINITE);

        /* The main AnimationTimer that runs the game, defaults to 60fps.
         * Try to keep the code in this section limited. On new systems
         * it may run fine, but older systems may have issues executing
         * these instructions 60 times per second. */
        AnimationTimer animate = new AnimationTimer() {
            public void handle(long currentNanoTime) {

                //Clears and renders the image on the screen
                gc.clearRect(0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
                drawLayers(gc);


                //Triggers UI updates, but only if needed.
                if(enemies.size() > 0 && !uiManager.engaged()) enemyHealthBars.forEach(StatusPeekPane::update);
                Player p = player.getPlayer();
                if(p.getCurrentMana() < p.getMaxMana()) manaRegen.play();
                if(p.getCurrentHP() < p.getMaxHP()) hpRegen.play();
                if(p.getCurrentStamina() < p.getMaxStamina()) staminaRegen.play();
            }
        };
        animate.start();
    }

    private void removeAllEnemyHPBars() {
        for(Iterator<Node> n = this.getChildren().iterator(); n.hasNext();) {
            Node node = n.next();
            if(node instanceof StatusPeekPane) {
                n.remove();
            }
        }
    }

    /**
     * Fades out a StatusPeekPane after 3/4 seconds.
     * @param bar The bar on the screen to fade out.
     */
    public void fadeOutStatus(StatusPeekPane bar) {
        FadeTransition ft = new FadeTransition(Duration.millis(750), bar);
        ft.setOnFinished(f -> bar.setVisible(false));
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }

    /**
     * Starts the cooldown for projectiles fired by the player.
     * @param period The time period for the cooldown.
     */
    private void startCooldown(int period) {
        System.out.println(period);
        PauseTransition delay = new PauseTransition(Duration.millis(period));
        delay.setOnFinished(event -> projectileOnCooldown = false);
        delay.play();
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
            if (interact.getBounds().intersects(items.get(i).getBounds()) && !(items.get(i) instanceof GenericObstacle)) {
                found = true;
                if (items.get(i) instanceof Lootable) {
                    uiManager.displayLootPane(((Lootable) items.get(i)));
                } else if (items.get(i) instanceof Save) {
                    SaveManager.serialize(currentMapFile, player, this.getId(), this.saveLoc);
                    uiManager.displayMessagePane("Save succeeded!");
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
                        BattleHandler.physicalAttack(player.getPlayer(), e);
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
    private void projectileAttack(int x, int y, int dx, int dy, String imageLoc) {
        if(imageLoc == null) {
            imageLoc = "file:Images\\Weapons\\Spells\\corn.png";
        }
        if(playerProjectiles.size() <= MAX_PLAYER_PROJECTILES_ON_SCREEN - 1) {
            Sprite interact = new Sprite(x, y, imageLoc);
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
        if(!uiManager.engaged()) {
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

                it.remove();

                if(e.isActive()) {
                    BattleHandler.physicalAttack(player.getPlayer(), e);
                }

                System.out.println("Test: Collision with enemy success");
            } else if(collision != null && !(collision instanceof LowerLayer || collision instanceof Exit)) {
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
        if(!uiManager.engaged()) {
            moveEnemies(); // in future use enemyAI to determine individual AI
        }
        enemies.forEach(s -> s.render(gc));
    }

    /**
     * Private method which moves all of the enemies on screen towards the player
     */
    private void moveEnemies() {
        Iterator<NPC> it = enemies.iterator();
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
                            ((NPC) s).setCurrentImage(((NPC) s).getNPC().getNorthImage());
                            break;
                        case South:
                            s.modifyY(s.getDy()); // move down
                            ((NPC) s).setCurrentImage(((NPC) s).getNPC().getSouthImage());
                            break;
                        case East:
                            s.modifyX(s.getDx()); // move right
                            ((NPC) s).setCurrentImage(((NPC) s).getNPC().getEastImage());
                            break;
                        case West:
                            s.modifyX(-s.getDx()); // move left
                            ((NPC) s).setCurrentImage(((NPC) s).getNPC().getWestImage());
                            break;
                    }

                    if(enemyCollision(s)) {
                        Sprite collision = getCollisionObstacle(s);
                        boolean noNewPath = s.intersects(player);
                        switch(direction) {
                            case North:
                                s.modifyY(s.getDy()); // move down
                                break;
                            case South:
                                s.modifyY(-s.getDy()); // move up
                                break;
                            case East:
                                s.modifyX(-s.getDx()); // move left
                                break;
                            case West:
                                s.modifyX(s.getDx()); // move right
                                break;
                        }
                        if(!noNewPath) {
                            s.setPath(pathAroundSprite(s.clone(), s, collision, direction));
                        }
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
                            ((NPC) s).setCurrentImage(((NPC) s).getNPC().getWestImage());
                        } else { // move right
                            s.modifyX(s.getDx());
                            ((NPC) s).setCurrentImage(((NPC) s).getNPC().getEastImage());
                        }
                        s.modifyY(0);
                    } else { // move in y direction
                        if(deltaY > 0) { // move up
                            s.modifyY(-s.getDy());
                            ((NPC) s).setCurrentImage(((NPC) s).getNPC().getNorthImage());
                        } else { // move down
                            s.modifyY(s.getDy());
                            ((NPC) s).setCurrentImage(((NPC) s).getNPC().getSouthImage());
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
                            System.out.println("Going in!"); // TODO remove this stupid shit
                            s.setPath(pathAroundSprite(s.clone(), s, collision, direction));
                            System.out.println("AND I'M GONE!");
                        }
                    }
                }

                deltaX = s.getX() - playerX;
                deltaY = s.getY() - playerY;
                if(Math.abs(deltaX) < 32 && Math.abs(deltaY) < 32 && !e.getAttacking()) { // TODO do enemy attacking, also make it timer based so enemies cannot attack infinitely fast
                    System.out.println("Enemy attack");
                }
            }
        }
    }

    /**
     * Determines a path around a colliding sprite for another sprite.
     * @param s The sprite to path
     * @param other The other sprite to avoid (Sprite from the actual enemy not its clone)
     * @param collision The sprite to path around
     * @param direction The direction the colliding sprite is with respect to the sprite
     * @return The path
     */
    private LinkedList<Cardinal> pathAroundSprite(Sprite s, Sprite other, Sprite collision, Cardinal direction) {
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

                if(attemptMoveHorizontally(s, other, deltaXFromLeft, initialX, result, false)) { // now try to path vertically
                    return moveVerticallyUntilCanRealignHorizontally(s, other, deltaY, initialX, result, direction == Cardinal.North, true);
                } else { // move failed, try other direction
                    result.clear();
                    if(attemptMoveHorizontally(s, other, deltaXFromRight, initialX, result, true)) { // now try to path vertically
                        return moveVerticallyUntilCanRealignHorizontally(s, other, deltaY, initialX, result, direction == Cardinal.North, false);
                    }
                }

            } else { // try the right side

                if(attemptMoveHorizontally(s, other, deltaXFromRight, initialX, result, true)) { // now try to path vertically
                    return moveVerticallyUntilCanRealignHorizontally(s, other, deltaY, initialX, result, direction == Cardinal.North, false);
                } else { // move failed, try other direction
                    result.clear();
                    if(attemptMoveHorizontally(s, other, deltaXFromLeft, initialX, result, false)) { // now try to path vertically
                        return moveVerticallyUntilCanRealignHorizontally(s, other, deltaY, initialX, result, direction == Cardinal.North, true);
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
                if(attemptMoveVertically(s, other, deltaYFromTop, initialY, result, true)) { // now try to path horizontally
                    return moveHorizontallyUntilCanRealignVertically(s, other, deltaX, initialY, result, direction == Cardinal.East, false);
                } else {
                    result.clear();
                    if(attemptMoveVertically(s, other, deltaYFromBottom, initialY, result, false)) { // now try to path horizontally
                        return moveHorizontallyUntilCanRealignVertically(s, other, deltaX, initialY, result, direction == Cardinal.East, true);
                    }
                }
            } else { // try the bottom side first
                if(attemptMoveVertically(s, other, deltaYFromBottom, initialY, result, false)) { // now try to path horizontally
                    return moveHorizontallyUntilCanRealignVertically(s, other, deltaX, initialY, result, direction == Cardinal.East, true);
                } else {
                    result.clear();
                    if(attemptMoveVertically(s, other, deltaYFromTop, initialY, result, true)) {
                        return moveHorizontallyUntilCanRealignVertically(s, other, deltaX, initialY, result, direction == Cardinal.East, false);
                    }
                }
            }
        }


        return result;
    }

    /**
     * If the given sprite is able to move left the given amount of distance without collisions.
     * @param s The sprite to move
     * @param other The other sprite to avoid
     * @param deltaX The distance to move
     * @param initialX The initial X position before the move was attempted
     * @param result The result list of Cardinals to make the path
     * @param right If the movement is to the right
     * @return If the move was successful
     */
    private boolean attemptMoveHorizontally(Sprite s, Sprite other, int deltaX, int initialX, LinkedList<Cardinal> result, boolean right) {
        int amountMovedHorizontally = 0;
        Cardinal direction = right ? Cardinal.East : Cardinal.West;

        // move the sprite deltaX distance to the (right/left) in steps of s.getWidth()
        while(tryMoveHorizontally(s, other,
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
     * @param other The other sprite to avoid
     * @param deltaY The distance to move
     * Wparam initialY The initial Y position before the move was attempted
     * @param result The result list of Cardinals to make the path
     * @param up If the movement is up
     * @return If the move was successful
     */
    private boolean attemptMoveVertically(Sprite s, Sprite other, int deltaY, int initialY, LinkedList<Cardinal> result, boolean up) {
        int amountMovedVertically = 0;
        Cardinal direction = up ? Cardinal.North : Cardinal.South;

        // move the sprite deltaY distance (up/down) in steps of s.getHeight()
        while(tryMoveVertically(s, other,
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
     * @param other The other sprite to avoid
     * @param deltaY The y distance to move before checking if it can move horizontally
     * @param initialX The initial X position to return to
     * @param result The current path
     * @param up If the vertical direction of movement is up
     * @param right If the horizontal direction of movement is right
     * @return The final path or null if it gets stuck
     */
    private LinkedList<Cardinal> moveVerticallyUntilCanRealignHorizontally(Sprite s, Sprite other, int deltaY, int initialX, LinkedList<Cardinal> result, boolean up, boolean right) {
        final int MAX_ATTEMPTS = 5;
        int counter = 0;
        int prevY = s.getY();
        do {
            int newY;
            attemptMoveVertically(s, other, deltaY, s.getY(), result, up);
            newY = s.getY();

            if(prevY == newY) { // the sprite did not move, so it collided with something.
                Cardinal direction = up ? Cardinal.North : Cardinal.South;

                while(!enemyCollision(s, other)) {
                    if(up) {
                        s.modifyY(-s.getDy());
                    } else {
                        s.modifyY(s.getDy());
                    }
                }

                Sprite collision = getCollisionObstacle(s, other);

                if(up) {
                    s.modifyY(s.getDy());
                } else {
                    s.modifyY(-s.getDy());
                }

                if(collision == null) {
                    return result;
                }

                if(Math.abs(s.getX() - player.getX()) < 50 && Math.abs(s.getY() - player.getY()) < 50) { // TODO eh
                    return new LinkedList<>();
                }

                result.addAll(pathAroundSprite(s, other, collision, direction));
                return result;

            } else { // no collision
                prevY = newY;
            }

            counter++;
            if(counter == MAX_ATTEMPTS) {
                return null; // path failed
            }
        } while(!attemptMoveHorizontally(s, other, Math.abs(s.getX() - initialX), s.getX(), result, right)); // until you can move horizontally keep moving vertically

        return result; // SUCCESS RETURN THAT SHITTTTTTT
    }

    /**
     * Moves the Sprite vertically in the given direction until it is able to realign to its initial x position without collision.
     * @param s The sprite to move
     * @param other The other sprite to avoid
     * @param deltaX The x distance to move before checking if it can move vertically
     * @param initialY The initial Y position to return to
     * @param result The current path
     * @param right If the vertical direction of movement is right
     * @param up If the horizontal direction of movement is up
     * @return The final path or null if it gets stuck
     */
    private LinkedList<Cardinal> moveHorizontallyUntilCanRealignVertically(Sprite s, Sprite other, int deltaX, int initialY, LinkedList<Cardinal> result, boolean right, boolean up) {
        final int MAX_ATTEMPTS = 5;
        int counter = 0;
        int prevX = s.getX();
        do {
            int newX;
            attemptMoveHorizontally(s, other, deltaX, s.getX(), result, right);
            newX = s.getX();

            if(prevX == newX) { // the sprite did not move, it collided with something
                Cardinal direction = right ? Cardinal.East : Cardinal.West;

                while(!enemyCollision(s, other)) {
                    if(right) {
                        s.modifyX(s.getDx());
                    } else {
                        s.modifyX(-s.getDx());
                    }
                }

                Sprite collision = getCollisionObstacle(s, other);

                if(right) {
                    s.modifyX(-s.getDx());
                } else {
                    s.modifyX(s.getDx());
                }

                if(collision == null) {
                    return result;
                }

                if(Math.abs(s.getX() - player.getX()) < 50 && Math.abs(s.getY() - player.getY()) < 50) { // TODO eh
                    return new LinkedList<>();
                }

                result.addAll(pathAroundSprite(s, other, collision, direction));
                return result;

            } else { // no collision
                prevX = newX;
            }

            counter++;
            if(counter == MAX_ATTEMPTS) {
                return null; // path failed
            }
        } while(!attemptMoveVertically(s, other, Math.abs(s.getY() - initialY), s.getY(), result, up)); // until you can move vertically keep moving horizontally

        return result; // SUCCESS RETURN THAT SHITTTTTTT
    }

    /**
     * Attempts to move the sprite delta units veritcally.
     * If movement is unsuccessful the sprite is moved back.
     * @param s The sprite to move
     * @param other The other sprite to avoid
     * @param delta The amount to be moved
     * @param up If the movement is up (false -> down)
     * @return If the movement is successful
     */
    private boolean tryMoveVertically(Sprite s, Sprite other, int delta, boolean up) {
        if(up) {
            s.modifyY(-delta);
        } else {
            s.modifyY(delta);
        }

        boolean temp = enemyCollision(s, other);
        if(temp) {
            if (up) {
                s.modifyY(delta);
            } else {
                s.modifyY(-delta);
            }
        }

        return !temp;
    }

    /**
     * Attempts to move the sprite delta units veritcally.
     * If movement is unsuccessful the sprite is moved back.
     * @param s The sprite to move
     * @param other The other sprite to avoid
     * @param delta The amount to be moved
     * @param right If the movement is up (false -> down)
     * @return If the movement is successful
     */
    private boolean tryMoveHorizontally(Sprite s, Sprite other, int delta, boolean right) {
        if(right) {
            s.modifyX(delta);
        } else {
            s.modifyX(-delta);
        }

        boolean temp = enemyCollision(s, other);
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
     * Determines if a Sprite collides with anything on screen except itself or another given Sprite.
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
     * Determines if a Sprite collides with anything on screen except itself or another given Sprite.
     * @param s The sprite to test
     * @param otherAvoid The other Sprite to avoid
     * @return if the sprite collides with an object (besides itself)
     */
    private boolean enemyCollision(Sprite s, Sprite otherAvoid) {
        if(s.intersects(player)) {
            return true;
        }

        for(Sprite obstacle : map.getCollisions()) {
            if(!s.equals(obstacle) && s.getBounds().intersects(obstacle.getBounds()) && !obstacle.equals(otherAvoid)) {
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
     * Gets the Sprite that the given Sprite is colliding with, except avoid
     * @param s The Sprite to test
     * @param avoid The sprite to avoid
     * @return The obstacle that s is colliding with (besides itself or avoid)
     */
    private Sprite getCollisionObstacle(Sprite s, Sprite avoid) {
        for(Sprite obstacle : map.getCollisions()) {
            if(!s.equals(obstacle) && !avoid.equals(obstacle) && s.getBounds().intersects(obstacle.getBounds())) {
                return obstacle;
            }
        }
        return null;
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
            uiManager.displayMessagePane("You have picked up a " + item.getItem().getSimpleName() + ".");
            return true;
        } else {
            uiManager.displayMessagePane("You don't currently have the inventory space to carry that!");
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
                uiManager.displayMessagePane(obstacle.getMessage(), (NPC) obstacle);
            } else {
                //TODO inefficient/pointless, find better way to do this
                ArrayList<Enemy> enemy = new ArrayList<>();
                enemy.add((Enemy) ((NPC) obstacle).getNPC());
            }
        } else if (((NPC) obstacle).getNPC() instanceof Neutral) {
            ((NPC) obstacle).questInteraction();
            uiManager.displayMessagePane(obstacle.getMessage(), (NPC) obstacle);
        }

    }

    private void initCollections() {
        try {
            String mapLoc = saveDir + "Maps\\Map1-1.json";
            map = new MapContainer(player, mapLoc);
            this.setId(map.getIdName());
            setCurrentMapFile(mapLoc);
            fillEnemies();
        }
        catch(Exception e) {
            GameStage.logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * Fills the enemies list with all the enemies in the current map.
     */
    public void fillEnemies() {
        this.enemies.clear();
        this.enemyHealthBars.clear();
        AudioManager.getInstance().stopAllAudio();
        this.removeAllEnemyHPBars();
        boolean enemyFound = false;
        ArrayList<Sprite> temp = map.getMapItems();
        for (Sprite s : temp) {
            if(s instanceof NPC) {
                NPC np = (NPC) s;
                if(np.getNPC() instanceof Enemy) {
                    enemyFound = true;
                    if(!((Enemy) np.getNPC()).getCustomMusic().isEmpty()) {
                        AudioManager.getInstance().loopBackgroundMusic(((Enemy) np.getNPC()).getCustomMusic());
                    }
                    enemies.add((NPC) s);
                    StatusPeekPane spp = new StatusPeekPane(this, StatusPeekPane.ENEMY, (NPC) s);
                    enemyHealthBars.add(spp);
                    this.getChildren().add(spp);
                }
            }
        }

        if(!enemyFound) {
            //TODO change to previous track when we actually have background music, instead of just stopping audio to return to silence
            AudioManager.getInstance().stopAllAudio();
        }
    }

    public void drawHealth() {
        this.removeAllEnemyHPBars();
        enemies.forEach(enemy -> {
            StatusPeekPane pane = new StatusPeekPane(this, StatusPeekPane.ENEMY, enemy);
            enemyHealthBars.add(pane);
            this.getChildren().add(pane);
        });
        if(enemies.size() == 0) {
            AudioManager.getInstance().stopAllAudio();
        }
    }

    /**
     * Removes the enemy from the current enemies when it is killed.
     * @param e The killed enemy
     */
    public void enemyKilled(Enemy e) { // TODO REMOVE ENEMY HEALTH BARS ON DEATH
        Iterator<NPC> it = enemies.iterator();
        Enemy enemy;
        Sprite s;
        while(it.hasNext()) {
            s = it.next();
            enemy = ((Enemy) ((NPC)s).getNPC());
            if(e.equals(enemy)) {
                it.remove(); // remove the enemy, it is dead
                // TODO play death animation then drop items
                AudioManager.getInstance().playSound("Sounds\\Death\\Scream " + GameStage.getRandom(20) + ".mp3");
                map.removeSprite(s);
                drawLayers(gc);
                drawHealth();
                return; // break the method
            }
        }
    }

    private void drawLayers(GraphicsContext gc) {
        map.getUnderLayer().forEach(sprite -> sprite.render(gc));
        updatePlayer(gc);
        updateProjectiles(gc);
        updateEnemies(gc);
        map.getOverLayer().forEach(sprite -> sprite.render(gc));
    }

    void updatePlayer(GraphicsContext gc) {
        if(!uiManager.engaged()) move(player);
        if(player.isVisible()) player.render(gc);
        //as.render(gc);
    }

    /**
     * Move the sprite its given dx and dy.
     * Check collision after each movement direction so it is easy to determine which one is causing the trouble.
     * @param sprite The sprite to move
     */
    void move(Sprite sprite) {
        sprite.modifyX(sprite.getDx());
        //If the player tries moving out of bounds to the left.
        if (sprite.getX() < 1) sprite.setX(1);
        //If the player tries moving out of bounds to the right.
        if (sprite.getX() > GameStage.WINDOW_WIDTH - 35) sprite.setX(GameStage.WINDOW_WIDTH - 35);
        if(collision()) {
            sprite.modifyX(-sprite.getDx());
        }

        sprite.modifyY(sprite.getDy());
        //If the player tries moving out of bounds upwards.
        if (sprite.getY() < 1) sprite.setY(1);
        //If the player tries moving out of bounds downwards.
        if (sprite.getY() > GameStage.WINDOW_HEIGHT - 70) sprite.setY(GameStage.WINDOW_HEIGHT - 70);
        if(collision()) {
            sprite.modifyY(-sprite.getDy());
        }
    }

    /**
     * Checks for player collisions upon movement.
     * @return
     */
    private boolean collision() {
        for (Sprite obstacle : map.getCollisions()) {
            if (player.intersects(obstacle)) {
                if(obstacle instanceof Exit) {
                    exitHandle((Exit) obstacle);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates map items when the player moves to the next region of the map.
     * @param exit The invisible Exit sprite that contains the next map and next player coordinates.
     */
    private void exitHandle(Exit exit) {
        player.setX(exit.getNextX()); //Sets the player to the appropriate coordinates in the new area.
        player.setY(exit.getNextY());
        if(exit.getNextMapLocation().equals("random")) {
            try {
                this.despawnPlayerProjectiles();
                String defaultDir = saveDir + "tempMap.json";
                map.randomize(defaultDir, currentMapFile, exit);
                exit.setNextMapLocation(defaultDir);
                fillEnemies();
            } catch (Exception e) {
                System.out.println("Failed to generate dungeon.");
                GameStage.logger.error(e);
                e.printStackTrace();
            }
        } else if(exit.getNextMapLocation().equals("dungeon")) {
            try {
                this.despawnPlayerProjectiles();
                String defaultDungeonStart = saveDir + "CurrentDungeon\\cell_S.json";
                map.startDungeon(defaultDungeonStart, currentMapFile, exit, saveDir);
                exit.setNextMapLocation(defaultDungeonStart);
                fillEnemies();
            } catch (Exception e) {
                System.out.println("Failed to generate random map.");
                GameStage.logger.error(e);
                e.printStackTrace();
            }
        } else {
            String nextMap = exit.getNextMapLocation();
            this.despawnPlayerProjectiles();
            map.loadNewFile(nextMap);
            setCurrentMapFile(nextMap); //Sets the current map file and map items to the new map.
            this.setId(map.getIdName());
            fillEnemies();
        }
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

    public String getDefaultHealthAccentColor(Character chara) {
        double diff = (chara.getCurrentHP()+0.0) / (chara.getMaxHP()+0.0);
        if(diff > .75) {
            return STYLE_HIGH_HP;
        } else if (diff <= .75 && diff > .35) {
            return STYLE_MEDIUM_HP;
        } else {
            return STYLE_LOW_HP;
        }
    }

    public String getPlayerHealthAccentColor() {
        Player p = player.getPlayer();
        double diff = (p.getCurrentHP()+0.0) / (p.getMaxHP()+0.0);
        if(diff > .75) {
            return STYLE_HIGH_HP;
        } else if (diff <= .75 && diff > .35) {
            return STYLE_MEDIUM_HP;
        } else {
            return STYLE_LOW_HP;
        }
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
