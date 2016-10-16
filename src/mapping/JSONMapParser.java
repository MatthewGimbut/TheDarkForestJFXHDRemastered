package mapping;

import characters.Character;
import characters.Enemy;
import characters.Neutral;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import items.Item;
import javafx.geometry.Rectangle2D;
import main.GameStage;
import quests.master.MasterQuests;
import quests.trigger.Trigger;
import sprites.*;

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
    private final int MAX_NPC_MESSAGE_SIZE = 20;
    private final int NUM_GENERIC_DIALOGUE = 4;
    private final String DELIMITER_STRING = "|";
    public String background;
    private PlayerSprite player;

    /**
     * Constructor for the JSONMapParser class.
     * @param player The current player object in the game.
     */
    public JSONMapParser(PlayerSprite player) {
        this.player = player;
        rand = new Random();
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
        Exit returnExit;

        if(newPlacement.equals(Cardinal.South))
            returnExit = new Exit((int) sourceExit.getX(), 665, (int) sourceExit.getX(), 50, newPlacement, oldFileLocation);
        else if(newPlacement.equals(Cardinal.North))
            returnExit = new Exit((int) sourceExit.getX(), 50, (int) sourceExit.getX(), 665, newPlacement, oldFileLocation);
        else if(newPlacement.equals(Cardinal.East))
            returnExit = new Exit(1020, (int) sourceExit.getY(), 5, (int) sourceExit.getY(), newPlacement, oldFileLocation);
        else  //West
            returnExit = new Exit(5, (int) sourceExit.getY(),  5, (int) sourceExit.getY(), newPlacement, oldFileLocation);

        mapItems.add(returnExit);



        //Temporary ArrayList that contains default tree border to be loaded into the new map.
        //Checks to see which trees intersect the exits and only places the ones that don't.
        ArrayList<Sprite> temp = parseMap("MapFragments\\TreeBorder.json").getMapItems();
        temp.forEach(sprite -> {
            if(!(intersectsObstacle(mapItems, sprite))) {
                mapItems.add(sprite);
            }
        });

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

        //Adds random obstacles
        for(int i = 0; i < 20; i++) {
            ArrayList<Sprite> sprite = getRandomObstacles();
            sprite.forEach(currentSprite -> {
                if(!(intersectsObstacle(mapItems, currentSprite))) {
                    mapItems.add(currentSprite);
                }
            });
        }

        //Adds random loot chests
        for(int i = 0; i < 4; i++) {
            ArrayList<Sprite> sprite = getRandomLoot();
            sprite.forEach(currentSprite -> {
                if(!(intersectsObstacle(mapItems, currentSprite))) {
                    mapItems.add(currentSprite);
                }
            });
        }

        //Adds random generic npcs
        for(int i = 0; i < 4; i++) {
            ArrayList<Sprite> sprite = getRandomNPCS();
            sprite.forEach(currentSprite -> {
                if(!(intersectsObstacle(mapItems, currentSprite))) {
                    mapItems.add(currentSprite);
                }
            });
        }

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
    private  ArrayList<Sprite> getRandomNPCS() throws IOException {
        String randomDialogue = "Dialogue\\GenericNeutralDialogue\\Generic" + rand.nextInt(NUM_GENERIC_DIALOGUE) + ".dialogue";
        ArrayList<Sprite> npcs = new ArrayList<>();
        switch (rand.nextInt(3)) {
            case 0:
                npcs.add(new NPC(rand.nextInt(980), rand.nextInt(650), new Neutral(GameStage.getRandomName()),
                        parseDialogueArray(randomDialogue)));
                break;
            case 1:
            case 2:
                npcs.add(new NPC(rand.nextInt(980), rand.nextInt(650), new Enemy(GameStage.getRandomName()),
                        parseDialogueArray(randomDialogue)));
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
        switch(rand.nextInt(10)) {
            case 1:
                LinkedList<Item> items = Item.generateRandomItem(1);
                DisplayItem di = new DisplayItem(rand.nextInt(980), rand.nextInt(650), items.get(0));
                temp.add(di);
                break;

            case 2:
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

}
