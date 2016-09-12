package main;

import characters.Character;
import characters.Enemy;
import characters.Neutral;
import items.Armor.*;
import items.Consumables.Potion;
import items.Consumables.PotionType;
import items.Item;
import items.Rarity;
import items.Weapons.*;
import javafx.geometry.Rectangle2D;
import sprites.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import static java.lang.Integer.parseInt;

/**
 * MapParser class to read through each custom .map file to produce custom map.
 * Also can produce custom map from existing collection of items.
 * @author Matthew Gimbut
 *
 */
public class MapParser {
	
	private  int currentHouses = 0;
	private  Random rand;
	private  final int MAX_NPC_MESSAGE_SIZE = 20;
	private  final int NUM_GENERIC_DIALOGUE = 4;
    private  final String DELIMITER_STRING = "|";
	public  String background;
	private MapContainer container;
	private PlayerSprite player;

	public MapParser(PlayerSprite player, MapContainer container) {
		this.container = container;
		this.player = player;
        int tempMaps = 0;
		rand = new Random();
	}

	/**
	 * Only method is a public  method that takes two parameters to determine the map file.
	 * Different values read from the file are separated by a comma and loaded into a string array.
	 * info[0] is always the string containing the type of sprite.
	 * info[1] is the x coordinate of the sprite and info[2] is the y coordinate.
	 * Depending on the type of sprite, the length of the info array and values will change.
	 * @return An ArrayList<Sprite> that contains all of the sprite objects to be displayed on the map.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ArrayList<Sprite> parseMap(String mapLocation) throws IOException {
		ArrayList<Sprite> objects = new ArrayList<>();
		int lineCounter = 1;
		try(BufferedReader br = new BufferedReader(new FileReader(mapLocation))) {
			String line = br.readLine();
			while (line != null) {
				String[] info = line.split("\\|");
				switch(info[0]) {

				case "loot":
					int x = parseInt(info[1]);
					int y = parseInt(info[2]);
					String fileLocation = info[3];
					LinkedList<Item> items = new LinkedList<>();
					line = br.readLine();
					lineCounter++;
					while (!(line.equals("endloot"))) {
						info = line.split("\\|");
						switch (info[0]) {
							case "item":
								items.add(parseItem(info));
								break;
							case "random":
								items.add(Item.generateRandomItem());
								break;
							default:
								System.out.println("Non-item tagged on line " + lineCounter + " in loot loop.");
								break;
						}
						line = br.readLine();
						lineCounter++;
					}
					Lootable container = new Lootable(x, y, fileLocation, items);
					objects.add(container);
					break;

				case "save":
					objects.add(new Save(parseInt(info[1]), parseInt(info[2])));
					break;

				case "customObst":
					//info[3] is the file location of the image
					objects.add(new GenericObstacle(parseInt(info[1]), parseInt(info[2]), info[3]));
					break;

				case "enemy":
					//info[3] is the name, info[4] is the level, info[5] is the health.
					//info[6] is the mana, info[7] is the atk, info[8] is the magic.
					//info[9] is the def, info[10] is the spd.
					//info[11] is the image location, info[12] is the music location.
					//info[18]+ is any additional dialogue that the enemy has before fighting.
					//IGNORE ALL THAT I GOTTA FIX AFTER I FIGURE OUT WHATS GOING ON
					//TODO FIGURE OUT WHATS GOING ON
					String[] enemyMessage = new String[MAX_NPC_MESSAGE_SIZE];
					int i = 18;
					int enemyMessageCounter = 0;
					while(i < info.length) {
						enemyMessage[enemyMessageCounter] = info[i];
						i++;
						enemyMessageCounter++;
					}
					objects.add(new NPC(parseInt(info[1]), parseInt(info[2]),
							new Enemy(info[3], parseInt(info[4]), parseInt(info[5]),
									parseInt(info[6]), parseInt(info[7]),
									parseInt(info[8]), parseInt(info[9]),
									parseInt(info[10]), parseInt(info[11]), parseInt(info[12]),
									info[13], info[14], info[15], info[16],
									info[17]), enemyMessage));
					break;

				case "neutral":
					//info[5]+ is any additional dialogue that the enemy has before fighting.
					String[] neutralMessage = new String[MAX_NPC_MESSAGE_SIZE];
					int j = 8;
					int neutralMessageCounter = 0;
					while(j < info.length) { //Copies all array locations that contain dialogue into a new array to send to the NPC
						neutralMessage[neutralMessageCounter] = info[j];
						j++;
						neutralMessageCounter++;
					}
					objects.add(new NPC(parseInt(info[1]), parseInt(info[2]),
							new Neutral(info[3], info[4], info[5], info[6], info[7]), neutralMessage));
					break;

				case "item":
					objects.add(new DisplayItem(parseInt(info[2]), parseInt(info[3]), parseItem(info)));
					break;

				case "exit":
					//info[1] and info[2] are coordinates of exit placement on map.
					//info[3] and info [4] are the coordinates of where the player will be placed in the next area
					//info[5] is the part of the map where the exit is placed
					//info[6] is the file location of the next map cell
                        objects.add(new Exit(parseInt(info[1]), parseInt(info[2]),
                                parseInt(info[3]), parseInt(info[4]), determineCardinal(info[5]), info[6]));
					break;

				case "over":
					objects.add(new UpperLayer(parseInt(info[1]), parseInt(info[2]), info[3]));
					break;

				case "under":
					objects.add(new LowerLayer(parseInt(info[1]), parseInt(info[2]), info[3]));
					break;

				case "background":
					this.container.setIdName(info[1]);
					break;

				case "hostile":
					this.container.setHostile(true);
					break;

				case "nonhostile":
					this.container.setHostile(false);
					break;

				case "structure":
					ArrayList<Sprite> struct = parseStructure(info[1], parseInt(info[2]), parseInt(info[3]));
                    objects.addAll(struct);
					break;

				case "":
					break;

				default:
					System.out.println("Non-sprite tag " + info[0] + " at line " + lineCounter + ".");
					break;
				}

				line = br.readLine();
				lineCounter++;
			}
		} catch(NullPointerException e) {
			System.out.println("Something not initialized in map at line " + lineCounter + "!");
			e.printStackTrace();
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Array index error in map at line " + lineCounter + "! Are you missing a portion of the .map file?");
            e.printStackTrace();
		} catch(Exception e) {
			System.out.println("Map error occurred at line " + lineCounter + "! Check for typos in the current .map file!" + mapLocation);
			e.printStackTrace();
		}
		return objects;
	}

    /**
     * Writes a randomly generated map to a temporary map file. This file is then parsed normally and the map is displayed.
     * @param newFileLocation The map file location that the random map should be written to.
     *                     This should be contained in the Exit object intersected on the ViewPanel,
     *                     and sent in when the exit is triggered.
     * @param sourceExit   The Exit that the player came from. This needs to link back to the starting area.
     */
    public void generateRandomMap(String newFileLocation, String oldFileLocation, Exit sourceExit) throws IOException {
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
		ArrayList<Sprite> temp = parseMap("MapFragments\\TreeBorder.map");
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
		for(int i = 0; i < 30; i++) {
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
		writeToFile(newFileLocation, mapItems);
    }

    /**
     * Determines the exit direction for the new object based on the current one
     * @param oldPlacement The type being checked
     * @return The new exit direction placement
     */
	private  Cardinal determineNewPlacement(Cardinal oldPlacement) {
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
     * Writes an existing collection of sprites to a map file
     * @param fileLocation The location of the file to be written to
     * @param sprites The collection of sprites to write
     */
    private  void writeToFile(String fileLocation, ArrayList<Sprite> sprites) { //TODO Extend this method to be able to write any type of item
        File f = new File(fileLocation);
		try { 	//TODO REMOVE THIS LATER THIS IS ONLY FOR TESTING
			PrintWriter writer = new PrintWriter(f);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f, true), "ISO-8859-1")) {

			for (Sprite sprite : sprites) {
				switch (sprite.getClass().getSimpleName()) {
					case "Exit":
						Exit exit = (Exit) sprite;
						osw.append("exit" + DELIMITER_STRING + exit.getX() + DELIMITER_STRING + exit.getY() + DELIMITER_STRING + exit.getNextX() + DELIMITER_STRING
								+ exit.getNextY() + DELIMITER_STRING + determineCardinalTag(exit.getPlacement()) + exit.getNextMapLocation() + "\n");
						break;

					case "GenericObstacle":
						GenericObstacle go = (GenericObstacle) sprite;
						osw.append("customObst" + DELIMITER_STRING + go.getX()
                                + DELIMITER_STRING + go.getY() + DELIMITER_STRING + go.getImageLocation() + "\n");
						break;

					case "UpperLayer":
						UpperLayer o = (UpperLayer) sprite;
						osw.append("over"+ DELIMITER_STRING +o.getX()+ DELIMITER_STRING +o.getY()+ DELIMITER_STRING +o.getImageLocation()+"\n");
						break;

					case "LowerLayer":
						LowerLayer u = (LowerLayer) sprite;
						osw.append("under"+ DELIMITER_STRING +u.getX()+ DELIMITER_STRING +u.getY()+ DELIMITER_STRING +u.getImageLocation()+"\n");
						break;

					case "Lootable":
						Lootable l = (Lootable) sprite;
						osw.append("loot"+ DELIMITER_STRING +l.getX()+ DELIMITER_STRING +l.getY()+ DELIMITER_STRING +l.getImageLocation()+"\n");
						for(Item i : l.getItems()) {
							osw.append(getItemString(i));
						}
						osw.append("endloot\n");
						break;

					case "DisplayItem":
						DisplayItem di = (DisplayItem) sprite;
						osw.append(getDisplayItemString(di));
						break;

					case "NPC":
						NPC npc = (NPC) sprite;
						if (npc.getNPC() instanceof Enemy) {
							Enemy enemy = (Enemy) npc.getNPC();
							osw.append("enemy" + DELIMITER_STRING + npc.getX() + DELIMITER_STRING + npc.getY() + DELIMITER_STRING +
                                    enemy.getName() + DELIMITER_STRING + enemy.getLvl() + DELIMITER_STRING + enemy.getCurrentHP() + DELIMITER_STRING +
                                    enemy.getMaxHP() + DELIMITER_STRING + enemy.getCurrentMana() + DELIMITER_STRING + enemy.getMaxMana() +
                                    DELIMITER_STRING + enemy.getAtk() + DELIMITER_STRING + enemy.getMagic() + DELIMITER_STRING + enemy.getDef() +
                                    DELIMITER_STRING + enemy.getSpeed() + DELIMITER_STRING + npc.getNPC().getNorthImage() + DELIMITER_STRING +
									npc.getNPC().getSouthImage() + DELIMITER_STRING +  npc.getNPC().getEastImage() + DELIMITER_STRING +
									npc.getNPC().getWestImage() + DELIMITER_STRING + enemy.getCustomMusic());
							for (String dialogue : npc.getMessage()) {
								osw.append(DELIMITER_STRING + dialogue);
							}
						} else { //If not enemy, neutral
							osw.append("neutral|" + npc.getX() + DELIMITER_STRING + npc.getY() + DELIMITER_STRING + npc.getNPC().getName()
									+ DELIMITER_STRING + Character.GENERIC_NEUTRAL_NORTH + DELIMITER_STRING + Character.GENERIC_NEUTRAL_SOUTH  + DELIMITER_STRING +
									Character.GENERIC_NEUTRAL_EAST  + DELIMITER_STRING + Character.GENERIC_NEUTRAL_WEST );
							for (String dialogue : npc.getMessage()) {
								osw.append(DELIMITER_STRING + dialogue);
							}
						}
						osw.append("\n");
						break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
     * Removes a single line from a text file,
     * findind it by searching for the tag and corresponding x and y coordinates
     * @param tag The map file tag that the item has
     * @param currentMapFile The file to be searched and modified
     * @param x The x coordinate of the item
     * @param y The y coordinate of the item
     * @throws IOException
     */
	public  void removeItem(String tag, String currentMapFile, int x, int y) throws IOException {
		boolean found = false;
		int charCounter = 0;
		try(RandomAccessFile raf = new RandomAccessFile(new File(currentMapFile), "rw")) {
			String line = raf.readLine();
			while (line != null && !found) {
				String[] info = line.split("\\|");
				if(info[0].equals(tag) && parseInt(info[1]) == x && parseInt(info[2]) == y) {
					found = true;
					for(String string : info) {
						charCounter += string.length();
					}
					raf.seek(raf.getFilePointer()-(charCounter+info.length));
					raf.writeChars("r");
				} else {
					line = raf.readLine();
				}
			}
		}
	}

	public void removeQuestTrigger(String tag, String currentMapFile, String NPC) throws IOException {
		//TODO STUFF
		//WAIT UNTIL ACTUALLY ADDING THE TAGS TO THE MAP FILES
	}

    /**
     * Creates random NPCs to be placed in a random map
     * @return An ArrayList of type Sprite that contains the random NPCs
     * @throws IOException
     */
	private  ArrayList<Sprite> getRandomNPCS() throws IOException {
		ArrayList<Sprite> npcs = new ArrayList<>();
		switch (rand.nextInt(2)) {
			case 0:
				npcs.add(new NPC(rand.nextInt(980), rand.nextInt(650), new Neutral(GameStage.getRandomName()),
						parseDialogueArray("Dialogue\\GenericNeutralDialogue\\Generic" + rand.nextInt(NUM_GENERIC_DIALOGUE) + ".dialogue")));
				break;
			case 1:

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
		temp.add(new Lootable(rand.nextInt(980), rand.nextInt(650), Item.generateRandomItem(3)));
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
					temp = parseStructure("MapFragments\\SmallHouse.map", rand.nextInt(400) + 50 , rand.nextInt(400) + 50);
					int extraX = rand.nextInt(400);
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
		ArrayList<Sprite> struct = parseMap(mapLoc);
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
                return true;
            }
        }
        return false;
    }

	/**
	 * Separate method for parsing items because this could get messy kids.
     * info[4] is atk, info[5] is magic, info[6] is def, info[7] is speedmod, info[8] is weight,
     * info[9] is hpboost, info[10] is manaboost, info[11] is value, info[12] is rarity,
     * info[13] is weapon/armor type, info[14] is image location, info[15] is the item's name,
     * info[16] is the item tooltop.
     * Or they're all null. Who the fuck knows.
	 * info[2] and [3] will not matter if it is parsing an item to be placed into a chest.
	 * For consistency, when writing the map files, any items that are to be placed in a chest
	 * should have an x and y of 0.
	 * @param info
	 */
	private  Item parseItem(String[] info) {
		Item item = null;
		switch (info[1]) { //info[1] is the item type, 2 and 3 are x and y
		case "sword":
			item = new Sword(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
					parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
					parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
					determineWeaponType(info[13]), info[14], info[15], info[16]);
			break;
		case "axe":
            item = new Axe(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineWeaponType(info[13]), info[14], info[15], info[16]);
			break;
		case "dagger":
            item = new Dagger(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineWeaponType(info[13]), info[14], info[15], info[16]);
			break;
		case "mace":
            item = new Mace(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineWeaponType(info[13]), info[14], info[15], info[16]);
			break;
		case "spear":
            item = new Spear(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineWeaponType(info[13]), info[14], info[15], info[16]);
			break;
		case "boots":
            item = new Boots(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineArmorType(info[13]), info[14], info[15], info[16]);
			break;
		case "chest":
            item = new ChestPiece(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineArmorType(info[13]), info[14], info[15], info[16]);
			break;
		case "gloves":
            item = new Gloves(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineArmorType(info[13]), info[14], info[15], info[16]);
			break;
		case "helmet":
            item = new Helmet(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineArmorType(info[13]), info[14], info[15], info[16]);
			break;
		case "legs":
            item = new Legs(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineArmorType(info[13]), info[14], info[15], info[16]);
			break;
		case "shield":
            item = new Shield(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determineArmorType(info[13]), info[14], info[15], info[16]);
			break;
        case "potion":
            item = new Potion(parseInt(info[4]), parseInt(info[5]), parseInt(info[6]),
                    parseInt(info[7]), Double.parseDouble(info[8]), parseInt(info[9]),
                    parseInt(info[10]), parseInt(info[11]), determineRarity(info[12]),
                    determinePotionType(info[13]), parseInt(info[14]), info[15]);
            break;
		case "random":
			item = Item.generateRandomItem();
			break;
		}
		return item;
	}

    /**
     * Takes an Item and determines the String that should be written to the map file
     * @param i The Item to be written
     * @return The String being written to the map file
     */
	private  String getItemString(Item i) {
		StringBuilder sb = new StringBuilder();
		sb.append("item|");
		sb.append(determineItemTag(i));
        sb.append("0|0|").append(i.getAtk()).append(DELIMITER_STRING).append(i.getMagic()).append(DELIMITER_STRING).append(i.getDef())
                .append(DELIMITER_STRING).append(i.getSpeedModifier()).append(DELIMITER_STRING).append(i.getWeight()).append(DELIMITER_STRING).append(i.getHpBoost())
                .append(DELIMITER_STRING).append(i.getManaBoost()).append(DELIMITER_STRING).append(i.getValue()).append(DELIMITER_STRING);
		sb.append(determineRarityTag(i));

		if(i instanceof Weapon) {
            sb.append(determineWeaponTag((Weapon) i)).append(i.getImageLocation()).append(DELIMITER_STRING).append(i.getSimpleName())
                    .append(DELIMITER_STRING).append(i.getItemToolTipText());
		} else if(i instanceof Potion) {
            sb.append(determinePotionTypeTag((Potion) i)).append(((Potion) i).getAmount()).append(DELIMITER_STRING).append(i.getImageLocation());
		} else if(i instanceof Armor) {
            sb.append(determineArmorTag((Armor) i)).append(i.getImageLocation()).append(DELIMITER_STRING).append(i.getSimpleName())
                    .append(DELIMITER_STRING).append(i.getItemToolTipText());
		}
		sb.append("\n");
		return new String(sb);
	}

    /**
     * Takes a DisplayItem object and determines the String that should be written to the map file
     * @param di The DisplayItem to be written
     * @return The DisplayItem being written to the map file
     */
	private  String getDisplayItemString(DisplayItem di) {
        //Who said duplicate code was a bad thing
        StringBuilder sb = new StringBuilder();
		Item i = di.getItem();
		sb.append("item|");
		sb.append(determineItemTag(i));
		sb.append(di.getX()).append(DELIMITER_STRING).append(di.getY()).append(DELIMITER_STRING).append(i.getAtk()).append(DELIMITER_STRING)
                .append(i.getMagic()).append(DELIMITER_STRING).append(i.getDef()).append(DELIMITER_STRING).append(i.getSpeedModifier())
                .append(DELIMITER_STRING).append(i.getWeight()).append(DELIMITER_STRING).append(i.getHpBoost()).append(DELIMITER_STRING)
                .append(i.getManaBoost()).append(DELIMITER_STRING).append(i.getValue()).append(DELIMITER_STRING);
		sb.append(determineRarityTag(i));

		if(i instanceof Weapon) {
            sb.append(determineWeaponTag((Weapon) i)).append(i.getImageLocation()).append(DELIMITER_STRING)
                    .append(i.getSimpleName()).append(DELIMITER_STRING).append(i.getItemToolTipText());
		} else if(i instanceof Potion) {
            sb.append(determinePotionTypeTag((Potion) i)).append(((Potion) i).getAmount()).append(DELIMITER_STRING).append(i.getImageLocation());
		} else if(i instanceof Armor) {
            sb.append(determineArmorTag((Armor) i)).append(i.getImageLocation()).append(DELIMITER_STRING)
                    .append(i.getSimpleName()).append(DELIMITER_STRING).append(i.getItemToolTipText());
		}
		sb.append("\n");
		return new String(sb);
	}

    /**
     * Determines the armor tag for the map file based on armor type
     * @param a The armor being checked
     * @return The tag to be written to the map file
     */
	private  String determineArmorTag(Armor a) {
		switch(a.getArmorType()) {
			case wood:
				return "wood|";
			case leather:
				return "leather|";
			case cloth:
				return "cloth|";
			case bronze:
				return "bronze|";
			case iron:
				return "iron|";
			case steel:
				return "steel|";
			default:
				return null;
		}
	}

    /**
     * Determines the weapon tag for the map file based on weapon type
     * @param w The weapon being checked
     * @return The tag to be written to the map file
     */
	private  String determineWeaponTag(Weapon w) {
		switch(w.getWeaponType()) {
			case wood:
				return "wood|";
			case stone:
				return "stone|";
			case bronze:
				return "bronze|";
			case iron:
				return "iron|";
			case steel:
				return "steel|";
			default:
				return null;
		}
    }

    /**
     * Determines the rarity tag for the map file based on rarity type
     * @param i The item being checked
     * @return The tag to be written to the map file
     */
	private  String determineRarityTag(Item i) {
		switch(i.getHowRare()) {
			case JUNK:
				return "junk|";
			case COMMON:
				return "common|";
			case UNCOMMON:
				return "uncommon|";
			case RARE:
				return "rare|";
			case VERY_RARE:
				return "very_rare|";
			case LEGENDARY:
				return "legendary|";
			default:
				return null;
			}
	}

    /**
     * Determines the potion tag for the map file based on potion type.
     * @param p The potion being checked
     * @return The tag to be written to the map file
     */
	private  String determinePotionTypeTag(Potion p) {
		switch(p.getType()) {
			case Health:
				return "health|";
			case Mana:
				return "mana|";
			case Attack:
				return "attack|";
			case Defense:
				return "defense|";
			case Agility:
				return "agility|";
			default:
				return null;
		}
	}

    /**
     * Determines the generic item tag for the map file based on item type
     * @param i The item being checked
     * @return The tag to be written to the map file
     */
	private  String determineItemTag(Item i) {
		switch(i.getClass().getSimpleName()) {
			case "Boots" :
				return("boots|");
			case "ChestPiece":
				return("chest|");
			case "Gloves":
				return("gloves|");
			case "Helmet":
				return("helmet|");
			case "Legs":
				return("legs|");
			case "Shield":
				return("shield|");
			case "Potion":
				return("potion|");
			case "Axe":
				return("axe|");
			case "Dagger":
				return("dagger|");
			case "Mace":
				return("mace|");
			case "Spear":
				return("spear|");
			case "Sword":
				return("sword|");
		}
		return "";
	}

    /**
     * Determines the potion type for the object based on potion tag
     * @param potionType The type being checked
     * @return The tag to be written to the object
     */
    private  PotionType determinePotionType(String potionType) {
        switch(potionType) {
            case "health" :
                return PotionType.Health;
            case "mana":
                return PotionType.Mana;
            case "attack":
                return PotionType.Attack;
            case "defense":
                return PotionType.Defense;
            case "agility":
                return PotionType.Agility;
            default:
                return null;
        }
    }

    /**
     * Determines the armor type for the object based on armor tag
     * @param armorType The type being checked
     * @return The tag to be written to the object
     */
    private  ArmorType determineArmorType(String armorType) {
        switch(armorType) {
            case "wood":
                return ArmorType.wood;
            case "leather":
                return ArmorType.leather;
            case "cloth":
                return ArmorType.cloth;
            case "bronze":
                return ArmorType.bronze;
            case "iron":
                return ArmorType.iron;
            case "steel":
                return ArmorType.steel;
            default:
                return null;
        }
    }

    /**
     * Determines the rarity for the object based on rarity tag
     * @param howRare The type being checked
     * @return The tag to be written to the object
     */
	private  Rarity determineRarity(String howRare) {
		switch(howRare) {
		case "junk":
			return Rarity.JUNK;
		case "common":
			return Rarity.COMMON;
		case "uncommon":
			return Rarity.UNCOMMON;
		case "rare":
			return Rarity.RARE;
		case "very_rare":
			return Rarity.VERY_RARE;
		case "legendary":
			return Rarity.LEGENDARY;
		default:
			return null;		
		}
	}

    /**
     * Determines the weapon type for the object based on weapon type tag
     * @param weaponType The type being checked
     * @return The tag to be written to the object
     */
	private  WeaponType determineWeaponType(String weaponType) {
		switch(weaponType) {
		case "wood":
			return WeaponType.wood;
		case "stone":
			return WeaponType.stone;
		case "bronze":
			return WeaponType.bronze;
		case "iron":
			return WeaponType.iron;
		case "steel":
			return WeaponType.steel;
		default:
			return null;		
		}
	}

    /**
     * Determines the exit direction for the object based on exit direction tag
     * @param cardinal The type being checked
     * @return The tag to be written to the object
     */
	private  Cardinal determineCardinal(String cardinal) {
		switch(cardinal) {
			case "north":
				return Cardinal.North;
			case "south":
				return Cardinal.South;
			case "east":
				return Cardinal.East;
			case "west":
				return Cardinal.West;
			default:
				return null;
		}
	}

    /**
     * Determines the exit direction tag for the map file based on exit direction
     * @param cardinal The type being checked
     * @return The tag to be written to the map file
     */
	private  String determineCardinalTag(Cardinal cardinal) {
		switch(cardinal) {
			case North:
				return "north|";
			case South:
				return "south|";
			case East:
				return "east|";
			case West:
				return "west|";
			default:
				return null;
		}
	}

    /**
     * Adds an item to the map
     * @param tag The tag of the item to be added
     * @param x The x coordinate of the item
     * @param y The y coordinate of the item
     */
	public  void addItem(String tag, int x, int y) {
		//TODO Something
	}
}
