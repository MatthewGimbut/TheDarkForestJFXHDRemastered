package mapping;

import characters.Character;
import characters.Enemy;
import characters.Neutral;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import items.Item;
import items.SpellType;
import javafx.geometry.Rectangle2D;
import main.GameStage;
import quests.master.MasterQuests;
import quests.trigger.Trigger;
import sprites.*;

import javax.smartcardio.Card;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * Replacement class for the old MapParser. This one generates and reads JSON files.
 * This class does all the dirty work for parsing, writing, and creating maps.
 *
 * @author Matthew Gimbut
 *
 */

public class JSONMapParser {

    private int currentHouses = 0;
    private Random rand;
    private final int NUM_GENERIC_DIALOGUE = 4;
    private PlayerSprite player;
    private int numIterations;
    private final boolean NO_ENEMIES = false;
    private final boolean YES_ENEMIES = true;

    /**
     * Constructor for the JSONMapParser class.
     * @param player The current player object in the game.
     */
    public JSONMapParser(PlayerSprite player) {
        this.player = player;
        rand = new Random();
        numIterations = 0;
    }

    /**
     * Parses a map from JSON given a file location.
     * @param loc The location of the JSON file.
     * @return A JSONMapTemplate object containing the map items, file location, and CSS ID property.
     */
    public JSONMapTemplate parseMap(String loc) {
        loc = loc.replace(".map", ".json"); //TODO Remove this workaround, only here temporarily in case and files are still incorrectly named with ".map" after the switch
        JSONMapTemplate m = null;
        try {
            String json = new Scanner(new File(loc)).useDelimiter("\\Z").next(); //"\\Z" Delimiter is the end of file character, loading the entire file into the String with one call to next().

            //Creates a new GsonBuilder objects and sets all required properties right away.
            GsonBuilder gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .setLenient()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(Item.class, new InterfaceAdapter<Item>())
                    .registerTypeAdapter(Character.class, new InterfaceAdapter<Character>());

            m = gson.create().fromJson(json, JSONMapTemplate.class);

            /*
                Refreshes some important information that cannot/is not serialized properly by the JSON format.
                This includes proper escaping for file locations and the quest triggers.
             */
            m.getMapItems().forEach(sprite -> {
                sprite.setImage(sprite.getImageLocation().replaceAll("(.+(MapBuilder\\\\))", "file:"));
                if(sprite.getImageLocation().contains("\\\\")) sprite.setImage(sprite.getImageLocation().replace("\\\\", "\\"));
                if(sprite instanceof NPC) loadNPC((NPC) sprite);
            });

            return m;
        } catch (IOException e) {
            System.out.println("Error loading JSON!");
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Reloads any quest trigger related material in the JSON for an npc.
     * @param npc
     */
    private void loadNPC(NPC npc) {
        LinkedList<Trigger> activationTriggerTemp = new LinkedList<>();
        LinkedList<Trigger> questTriggerTemp = new LinkedList<>();

        for(Trigger t : npc.getQuestActivationTriggers()) {
            MasterQuests master = MasterQuests.valueOf(t.getAssociatedWith());
            if(master != null) {
                activationTriggerTemp.add(master.getQuest().getQuestAcceptanceTrigger());
            } else {
                System.out.println("Failed to parse quest " + t.getAssociatedWith());
            }
        }

        for(Trigger t : npc.getQuestTriggers()) {
            String[] data = t.getAssociatedWith().split("_");
            MasterQuests master = MasterQuests.valueOf(data[0]);
            int taskNum = Integer.parseInt(data[1], 10);
            taskNum--;
            if(master != null) {
                questTriggerTemp.add(master.getQuest().getAllTasks().get(taskNum).getTrigger());
            } else {
                System.out.println("Failed to parse quest " + t.getAssociatedWith());
            }
        }

        npc.getQuestActivationTriggers().clear();
        npc.getQuestTriggers().clear();

        npc.setQuestActivationTriggers(activationTriggerTemp);
        npc.setQuestTriggers(questTriggerTemp);

        npc.removeUsedTriggers();
    }

    /**
     * Responsible for parsing "simple" maps. This includes structures that do not require a background change or anything.
     * Examples include the tree border around maps or the simple buildings you can place.
     * @param loc The file location of the simple structure's JSON.
     * @return An ArrayList containing the Sprites for the structure.
     */
    public ArrayList<Sprite> parseSimpleMap(String loc) {
        ArrayList<Sprite> sprites = new ArrayList<>();
        try {
            String json = new Scanner(new File(loc)).useDelimiter("\\Z").next(); //"\\Z" Delimiter is the end of file character, loading the entire file into the String with one call to next().

            GsonBuilder gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .setLenient()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(Item.class, new InterfaceAdapter<Item>())
                    .registerTypeAdapter(Character.class, new InterfaceAdapter<Character>());

            JSONMapTemplate m = gson.create().fromJson(json, JSONMapTemplate.class);

            m.getMapItems().forEach(sprite -> {
                sprite.setImage(sprite.getImageLocation().replaceAll("(.+(MapBuilder\\\\))", "file:"));
                if(sprite.getImageLocation().contains("\\\\")) sprite.setImage(sprite.getImageLocation().replace("\\\\", "\\"));
                if(sprite instanceof NPC) loadNPC((NPC) sprite);
                sprites.add(sprite);
            });
        } catch (IOException e) {
            System.out.println("Error loading JSON!");
            System.out.println(e.getMessage());
        }
        return sprites;
    }

    /**
     * Clears any information currently in the files, then writes the JSON based on the information given.
     * @param file The file being written to.
     * @param mapItems The Sprite objects to write.
     * @param background The CSS ID for the background image of the map area.
     */
    public void writeMap(String file, ArrayList<Sprite> mapItems, String background) {
        JSONMapTemplate m = new JSONMapTemplate(mapItems, background, file);

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            GameStage.logger.error(e);
            e.printStackTrace();
        }

        try(BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
            GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(Item.class, new InterfaceAdapter<Item>())
            .registerTypeAdapter(Character.class, new InterfaceAdapter<Character>())
            .setPrettyPrinting();


            Gson gson = gsonBuilder.create();
            String json = gson.toJson(m);
            br.write(json);
        } catch (IOException e) {
            System.out.println("Error writing JSON!");
            GameStage.logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * Writes a randomly generated map to a temporary map file. This file is then parsed normally and the map is displayed.
     * @param newFileLocation The map file location that the random map should be written to.
     *                     This should be contained in the Exit object intersected on the ViewPanel,
     *                     and sent in when the exit is triggered.
     * @param sourceExit   The Exit that the player came from. This needs to link back to the starting area.
     */
    public JSONMapTemplate generateRandomMap(String newFileLocation, String oldFileLocation, Exit sourceExit) throws IOException {
        ArrayList<Sprite> mapItems = new ArrayList<>();
        currentHouses = 0;

        //Return exit brings you back to the original map before the random generation started
        //Each random map must have at least this one exit

        Cardinal newPlacement = determineNewPlacement(sourceExit.getPlacement());

        mapItems.add(getReturnExit(newPlacement, sourceExit, oldFileLocation));

        addTreeBorder(mapItems);

        addStructures(mapItems);

        //Places El Rato on the map
        if(rand.nextInt(10) < 2) {
            String[] message = {"This is the balding rat!", "You will bald prematurely if you don't say: ", "hello rato"};
            String elRatoImageLocation = "file:Images\\Characters\\ElRato.png";
            Enemy elRato = new Enemy("El Rato", 10, 200, 100, 20, 20, 20, 20,
                    elRatoImageLocation, elRatoImageLocation, elRatoImageLocation, elRatoImageLocation, "file:Music\\InLightOfDarkness.mp3");
            NPC tedCruz = new NPC(350, 350, elRato, message);
            if(!(intersectsObstacle(mapItems, tedCruz))) {
                mapItems.add(tedCruz);
            }
        }

        addRandomObstacles(mapItems, 20);

        addRandomLoot(mapItems, 4);

        addRandomNPCs(mapItems, YES_ENEMIES, 4);

        //Writes the new randomly generated map to a temp map file.
        writeMap(newFileLocation, mapItems, "grass");

        return new JSONMapTemplate(mapItems, "grass", newFileLocation);
    }

    /**
     * Determines the exit direction for the new object based on the current one
     * @param oldPlacement The type being checked
     * @return The new exit direction placement
     */
    private static Cardinal determineNewPlacement(Cardinal oldPlacement) {
        switch(oldPlacement) {
            case North:
                return Cardinal.South;
            case South:
                return Cardinal.North;
            case East:
                return Cardinal.West;
            case West:
                return Cardinal.East;
            default:
                return null;
        }
    }

    /**
     * Parses through a text file that contains dialogue separated by '|'
     * and turns it into an array of Strings in order to be read by the MessageGUI
     * @param fileLocation The location of the text file that contains the dialogue
     * @return An array of Strings, each index containing a single line of dialogue
     * @throws IOException
     */
    private  String[] parseDialogueArray(String fileLocation) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileLocation));
        String line = br.readLine();
        String[] temp = line.split("\\|");
        String[] dialogue = new String[temp.length];
        for(int i = 0; i < temp.length; i++) {
            dialogue[i] = temp[i];
        }
        return dialogue;
    }

    /**
     * Creates random NPCs to be placed in a random map
     * @return An ArrayList of type Sprite that contains the random NPCs
     * @throws IOException
     */
    private ArrayList<Sprite> getRandomNPCS(boolean addEnemies) throws IOException {
        String randomDialogue = "Dialogue\\GenericNeutralDialogue\\Generic" + rand.nextInt(NUM_GENERIC_DIALOGUE) + ".dialogue";
        ArrayList<Sprite> npcs = new ArrayList<>();
        switch (rand.nextInt(3)) {
            case 0:
                npcs.add(new NPC(rand.nextInt(980), rand.nextInt(650), new Neutral(GameStage.getRandomName()),
                        parseDialogueArray(randomDialogue)));
                break;
            case 1:
            case 2:
                if(addEnemies) {
                    npcs.add(new NPC(rand.nextInt(980), rand.nextInt(650), new Enemy(GameStage.getRandomName()),
                            parseDialogueArray(randomDialogue)));
                }
                break;
        }
        return npcs;
    }

    /**
     * Gets random obstacles to be placed on the map
     * As of right now, only returns trees
     * @return An ArrayList of type Sprite that contains the random obstacles
     * @throws IOException
     */
    private  ArrayList<Sprite> getRandomObstacles() throws IOException {
        ArrayList<Sprite> temp = new ArrayList<>();
        switch(rand.nextInt(20)) {
            case 1:
                LinkedList<Item> items = Item.generateRandomItem(1);
                DisplayItem di = new DisplayItem(rand.nextInt(980), rand.nextInt(650), items.get(0));
                temp.add(di);
                break;

            case 2:
            case 3:
            case 4:
            case 5:
                temp.add(new GenericObstacle(rand.nextInt(980), rand.nextInt(650), "file:Images\\Nature\\Tree50x50.png"));

            default:
                temp.add(new GenericObstacle(rand.nextInt(980), rand.nextInt(650), "file:Images\\Nature\\Tree.png"));
        }
        return temp;
    }

    /**
     * Gets random loots chests to be placed on the map
     * @return An ArrayList of type Sprite that contains the random lootable objects
     * @throws IOException
     */
    private  ArrayList<Sprite> getRandomLoot() throws IOException {
        Random rand = new Random();
        ArrayList<Sprite> temp = new ArrayList<>();
        temp.add(new Lootable(rand.nextInt(980), rand.nextInt(650), Item.generateRandomItem(8)));
        return temp;
    }

    /**
     * Gets a random pre-made structure to be placed on the map
     * @return An ArrayList of type Sprite that contains the random structure
     * @throws IOException
     */
    private  ArrayList<Sprite> getRandomStructures() throws IOException {
        ArrayList<Sprite> temp = new ArrayList<>();
        switch(rand.nextInt(10)) {
            default:
                final int MAX_HOUSES = 1;
                if(currentHouses < MAX_HOUSES) {
                    temp = parseStructure("MapFragments\\SmallHouse.json", rand.nextInt(400) + 50 , rand.nextInt(400) + 50);
                    int extraX = rand.nextInt(300);
                    for (Sprite sprite : temp) {
                        sprite.setX(sprite.getX() + extraX);
                    }
                    currentHouses++;
                }
                break;
        }
        return temp;
    }

    /**
     * Parses through a map file to create a pre-made structure
     * @param mapLoc The file location of the structure
     * @param xOffset The x offset to determine where it is placed on the map
     * @param yOffset The y offset to determine where it is placed on the map
     * @return An ArrayList of type Sprite that contains the random structure
     * @throws IOException
     */
    private  ArrayList<Sprite> parseStructure(String mapLoc, int xOffset, int yOffset) throws IOException {
        ArrayList<Sprite> struct = parseSimpleMap(mapLoc);
        for(Sprite sprite : struct) {
            sprite.setX(sprite.getX() + xOffset);
            sprite.setY(sprite.getY() + yOffset);
        }
        return struct;
    }
    /**
     * Used when adding objects to a random map, this method ensures that there
     * are no conflicts when placing items on the map
     * @param mapItems The ArrayList containing all current items on the map
     * @param testSprite The sprite that is being compared to the collection to make sure there are no collisions.
     * @return True or false depending on whether or not there are collisions.
     */
    private  boolean intersectsObstacle(ArrayList<Sprite> mapItems, Sprite testSprite) {
        Rectangle2D playerBounds = player.getBounds();
        for(Sprite sprite : mapItems) {
            if(testSprite.getBounds().intersects(sprite.getBounds()) || testSprite.getBounds().intersects(playerBounds)) {
                return !(testSprite instanceof LowerLayer);
            }
        }
        return false;
    }

    /**
     * Generates a random dungeon, which consists of a chain of map areas linked together, each its own separate file.
     * @param startCellLoc The file location of the starting cell for the dungeon.
     * @param prevFile The previous file before the player entered the dungeon, used to return the player when they leave.
     * @param sourceExit The exit from the previous map that the player entered to get to the dungeon.
     * @param saveDir The directory used to store the new map files.
     * @param maxHeight The max height of the 2D array used to store the dungeon skeleton.
     * @param maxWidth The max width of the 2D array used to store the dungeon skeleton.
     * @param numCells The max number of cells to be created in the skeleton.
     * @return Returns the JSONMapTemplate for the starting area, where the player is placed.
     * @throws IOException
     */
    public JSONMapTemplate generateDungeonChain(String startCellLoc, String prevFile, Exit sourceExit, String saveDir,
                                                int maxHeight, int maxWidth, int numCells) throws IOException {
        String[][] chain = getSkeleton(maxHeight, maxWidth, numCells);
        printMap(chain);
        String halfWay = numCells/2 + "";
        for(int i = 0; i < chain.length; i++) { //Rows
            for(int j = 0; j < chain.length; j++) { //Columns
                if(!chain[i][j].equals(" ")) {
                    if(chain[i][j].equals("S")) { //For start of dungeon chain. No enemies in first one!
                        ArrayList<Sprite> mapItems = new ArrayList<>();
                        Cardinal newPlacement = determineNewPlacement(sourceExit.getPlacement());
                        mapItems.add(getReturnExit(newPlacement, sourceExit, prevFile)); //Adds exit to previous file
                        mapItems.addAll(checkForSurroundingExits(chain, i, j, saveDir));

                        addTreeBorder(mapItems);
                        addRandomObstacles(mapItems, 40);
                        addRandomLoot(mapItems, 1);
                        addRandomNPCs(mapItems, NO_ENEMIES, 2);

                        writeMap(startCellLoc, mapItems, "grass");
                    } else {
                        ArrayList<Sprite> mapItems = new ArrayList<>();
                        mapItems.addAll(checkForSurroundingExits(chain, i, j, saveDir));
                        addTreeBorder(mapItems);

                        if(chain[i][j].equals(halfWay)) {
                            mapItems.add(new Save(rand.nextInt(980), rand.nextInt(650)));
                        }

                        String numCellsToString = "" + numCells;
                        if(chain[i][j].equals(numCellsToString)) { //Final cell of a dungeon has link to shrek house for now to get out
                            addStructures(mapItems);
                        }
                        addRandomObstacles(mapItems, 20);
                        addRandomLoot(mapItems, 2);
                        addRandomNPCs(mapItems, YES_ENEMIES, 2);

                        writeMap(saveDir+"CurrentDungeon\\cell_"+chain[i][j]+".json", mapItems, "grass");
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks all areas surrounding the currently generating cell to see if there are other cells that it should be connected to.
     * Generates exits accordingly.
     * @param chain The skeleton of the dungeon.
     * @param i The current row.
     * @param j The current column.
     * @param saveDir The save directory being used for the dungeon.
     * @return An ArrayList containing exits for all adjacent cells.
     */
    private ArrayList<Sprite> checkForSurroundingExits(String[][] chain, int i, int j, String saveDir) {
        ArrayList<Sprite> exits = new ArrayList<>();

        try {
            String test = chain[i+1][j];
            if(!test.equals(" ")) { //Place south exit
                exits.add(new Exit(550, 665, 550, 65, Cardinal.South, saveDir + "CurrentDungeon\\cell_"+test+".json",
                        "file:Images\\Blank32x48.png"));
                GenericObstacle go = new GenericObstacle(545, 585, "file:Images\\Space48x48.png");
                go.setObstacle(false);
                exits.add(go); //Adds a blank sprite above south exit to prevent anything else from being placed there
                //exits.add(new Sprite(550, 620, "file:Images\\Blank32x48.png"));
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        try {
            String test = chain[i-1][j];
            if(!test.equals(" ")) { //Place north exit
                exits.add(new Exit(550, 5, 550, 620, Cardinal.South, saveDir + "CurrentDungeon\\cell_"+test+".json",
                        "file:Images\\Blank32x48.png"));

                GenericObstacle go = new GenericObstacle(545, 60, "file:Images\\Space48x48.png");
                go.setObstacle(false);
                exits.add(go); //Adds a blank sprite above south exit to prevent anything else from being placed there

                //exits.add(new Sprite(550, 50, "file:Images\\Blank32x48.png"));
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        try {
            String test = chain[i][j+1];
            if(!test.equals(" ")) { //Place east exit
                exits.add(new Exit(1020, 373, 50, 373, Cardinal.South, saveDir + "CurrentDungeon\\cell_"+test+".json",
                        "file:Images\\Blank32x48.png"));

                GenericObstacle go = new GenericObstacle(965, 373, "file:Images\\Space48x48.png");
                go.setObstacle(false);
                exits.add(go); //Adds a blank sprite above south exit to prevent anything else from being placed there

                //exits.add(new Sprite(990, 373, "file:Images\\Blank32x48.png"));
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        try {
            String test = chain[i][j-1];
            if(!test.equals(" ")) { //Place west exit
                exits.add(new Exit(5, 373, 970, 373, Cardinal.South, saveDir + "CurrentDungeon\\cell_"+test+".json",
                        "file:Images\\Blank32x48.png"));

                GenericObstacle go = new GenericObstacle(50, 373, "file:Images\\Space48x48.png");
                go.setObstacle(false);
                exits.add(go); //Adds a blank sprite above south exit to prevent anything else from being placed there

                //exits.add(new Sprite(50, 373, "file:Images\\Blank32x48.png"));
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        return exits;
    }

    private void addTreeBorder(ArrayList<Sprite> mapItems) {
        //Temporary ArrayList that contains default tree border to be loaded into the new map.
        //Checks to see which trees intersect the exits and only places the ones that don't.
        ArrayList<Sprite> temp = parseMap("MapFragments\\TreeBorder.json").getMapItems();
        temp.forEach(sprite -> {
            if(!(intersectsObstacle(mapItems, sprite))) {
                mapItems.add(sprite);
            }
        });
    }

    private void addStructures(ArrayList<Sprite> mapItems) throws IOException {
        //Does the actual work to randomly place objects on the map.
        //First loop places random structures.
        //AKA High priority map items
        for(int i = 0; i < 1; i++) {
            ArrayList<Sprite> sprite = getRandomStructures();
            sprite.forEach(currentSprite -> {
                if(!(intersectsObstacle(mapItems, currentSprite)) || currentSprite instanceof Exit) {
                    mapItems.add(currentSprite);
                }
            });
        }
    }

    private void addRandomObstacles(ArrayList<Sprite> mapItems, int num) throws IOException {
        //Adds random obstacles
        for(int i = 0; i < num; i++) {
            ArrayList<Sprite> sprite = getRandomObstacles();
            sprite.forEach(currentSprite -> {
                if(!(intersectsObstacle(mapItems, currentSprite))) {
                    mapItems.add(currentSprite);
                }
            });
        }
    }

    private void addRandomLoot(ArrayList<Sprite> mapItems, int num) throws IOException {
        //Adds random loot chests
        for(int i = 0; i < num; i++) {
            ArrayList<Sprite> sprite = getRandomLoot();
            sprite.forEach(currentSprite -> {
                if(!(intersectsObstacle(mapItems, currentSprite))) {
                    mapItems.add(currentSprite);
                }
            });
        }
    }

    private void addRandomNPCs(ArrayList<Sprite> mapItems, boolean addEnemies, int num) throws IOException {
        //Adds random generic npcs
        for(int i = 0; i < num; i++) {
            ArrayList<Sprite> sprite = getRandomNPCS(addEnemies);
            sprite.forEach(currentSprite -> {
                if(!(intersectsObstacle(mapItems, currentSprite))) {
                    mapItems.add(currentSprite);
                }
            });
        }
    }

    /**
     * Generates a return exit. Used situationally, normally exit can be deduced without.
     * Used with initial entry of a dungeon chain/single random map generation.
     * @param newPlacement The new direction the player is facing.
     * @param sourceExit The exit that the player entered to get to the new area.
     * @param oldFileLocation The file location of the old map.
     * @return
     */
    private Exit getReturnExit(Cardinal newPlacement, Exit sourceExit, String oldFileLocation) {
        Exit returnExit = null;
        if(newPlacement.equals(Cardinal.South))
            returnExit = new Exit((int) sourceExit.getX(), 665, (int) sourceExit.getX(), 50, newPlacement, oldFileLocation);
        else if(newPlacement.equals(Cardinal.North))
            returnExit = new Exit((int) sourceExit.getX(), 50, (int) sourceExit.getX(), 665, newPlacement, oldFileLocation);
        else if(newPlacement.equals(Cardinal.East))
            returnExit = new Exit(1020, (int) sourceExit.getY(), 5, (int) sourceExit.getY(), newPlacement, oldFileLocation);
        else  //West
            returnExit = new Exit(5, (int) sourceExit.getY(),  5, (int) sourceExit.getY(), newPlacement, oldFileLocation);

        return returnExit;
    }

    /**
     * Gets the skeleton for the dungeon.
     * @param maxWidth The max width of the dungeon.
     * @param maxHeight The max height of the dungeon.
     * @param maxNumCells The max number of cells in the dungeon.
     * @return A 2D String array that contains characters/numbers depending on the type of cell.
     */
    public String[][] getSkeleton(int maxWidth, int maxHeight, int maxNumCells) {
        String[][] map = new String[maxHeight][maxWidth];
        int startingY = maxHeight-1;
        int startingX = maxWidth/2;

        /*
                                                    --KEY--

            S       - Dungeon starting point, the first location of the dungeon that the player arrives in.
            [0-9]*  - Dungeon cell, given a number depending on when in the sequence they were generated.
            More to come!

         */

        /**
         * Initializes the dungeon to be an empty array.
         * Sets the bottom middle location to be the starting point of the dungeon, labeled S.
         */
        for(int i = 0; i < maxHeight; i++) {
            for(int j = 0; j < maxWidth; j++) {
                map[i][j] = " ";
            }
        }
        map[startingY][startingX] = "S";

        addArea(startingY, startingX, map, maxNumCells);

        numIterations = 0;
        return map;
    }

    /**
     * Shitty recursive dungeon generating algorithm I wrote while half asleep.
     * Change this for future dungeons, only makes a cool one half of the time.
     * Also could be more efficient, choice of using strings was completely arbitrary
     * and since order doesn't really matter it may not be a bad idea to use ints.
     * But who cares, this is just a PoC for now to see that I can get exits to link up and an actual dungeon chain working.
     * @param y The currently observed Y coordinate
     * @param x The currently observed X coordinate
     * @param map The current map array
     * @param maxNumCells The max number of cells allowed in the skeleton
     */
    private void addArea(int y, int x, String[][] map, int maxNumCells) {
        int newX;
        int newY;
        numIterations++;
        boolean successful = false;
        int combinationY = 0;
        int combinationX = 0;

        while(!successful) {
            String testX0, testX1, testY0, testY1;
            try {
                testY0 = map[y+1][x];
            } catch(ArrayIndexOutOfBoundsException e) {
                testY0 = "NA";
            }

            try {
                testY1 = map[y-1][x];
            } catch(ArrayIndexOutOfBoundsException e) {
                testY1 = "NA";
            }

            try {
                testX0 = map[y][x+1];
            } catch(ArrayIndexOutOfBoundsException e) {
                testX0 = "NA";
            }

            try {
                testX1 = map[y][x-1];
            } catch(ArrayIndexOutOfBoundsException e) {
                testX1 = "NA";
            }

            if(!(testY0.equals(" ")) && !(testY1.equals(" ")) && !(testX0.equals(" ")) && !(testX1.equals(" "))) { //If there are no more possible movements
                System.out.println(numIterations);
                numIterations = maxNumCells;
                successful = true;
            } else {
                newX = 0;
                newY = 0;
                int random = rand.nextInt(10);
                switch(random) { //Up and right have two possibilities in order to skew the graph. can be modified for different results
                    case 0:
                    case 1:
                        newX = 1;
                        break;
                    case 3:
                    case 5:
                        newX = -1;
                        break;
                    case 4:
                    case 2:
                        newY = 1;
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        newY = -1;
                        break;
                }

                combinationX = x + newX;
                combinationY = y + newY;
                try {
                    String test = map[combinationY][combinationX];
                    if(test.equals(" ")) {
                        map[combinationY][combinationX] = numIterations + "";
                        successful = true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) { }
            }
        }

        if(numIterations < maxNumCells) {
            addArea(combinationY, combinationX, map, maxNumCells);
        }
    }

    public void printMap(String[][] map) {
        for(String[] outer : map) {
            for(String string : outer) {
                System.out.print(string + "\t");
            }
            System.out.println();
        }
    }
}
