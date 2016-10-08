package characters;

import items.ammunition.Ammunition;
import items.Armor.*;
import items.Consumables.Consumable;
import items.Consumables.Potion;
import items.Item;
import items.Secondary;
import items.TwoHanded;
import items.Weapons.Weapon;
import main.Records;

import quests.Quest;
import quests.QuestHandler;

import java.util.List;
import java.util.LinkedList;

public class Player extends Character {
    public static final String FACING_NORTH = "file:Images\\Player\\PlayerNorth.png";
    public static final String FACING_SOUTH = "file:Images\\Player\\PlayerSouth.png";
    public static final String FACING_EAST = "file:Images\\Player\\PlayerEast.png";
    public static final String FACING_WEST = "file:Images\\Player\\PlayerWest.png";
    private final static int STARTING_LEVEL = 1;
    private final static int STARTING_MAX_HP = 100;
    private final static int STARTING_MAX_MANA = 100;
    private final static int STARTING_ATK = 50;
    private final static int STARTING_MAGIC = 10;
    private final static int STARTING_DEF = 25;
    private final static int STARTING_SPD = 30;
    private final static int STARTING_MAX_CARRY = 150;
    public final static int MAX_XP = 100;
    private LinkedList<Item> inventory;
    private int xp;
    private int gold;
    private Weapon weaponHandR;
    private Secondary leftHand;
    private ChestPiece chestPiece;
    private Legs leggings;
    private Boots boots;
    private Gloves gloves;
    private Helmet helmet;
    private Records records;
    private Ammunition ammo;

    private List<Quest> activeQuestsSer;
    private List<Quest> completeQuestsSer;
    private List<Quest> inActiveQuestsSer;

    //Player settings
    private int textScrollingSpeed;

    /**
     * Constructor for new players
     * @param username A String for the player's username
     */
    public Player(String username) {
        super(username , STARTING_LEVEL, STARTING_MAX_HP, STARTING_MAX_HP, STARTING_MAX_MANA, STARTING_MAX_MANA, STARTING_ATK, STARTING_MAGIC, STARTING_DEF, STARTING_SPD, 0, STARTING_MAX_CARRY);
        xp = 0;
        gold = 0;
        textScrollingSpeed = 45; //Milliseconds
        inventory = new LinkedList<Item>();
        records = new Records();
    }

    public Weapon getWeaponHandR() {
        return weaponHandR;
    }

    public Secondary getLeftHand() {
        return leftHand;
    }

    public ChestPiece getChestPiece() {
        return chestPiece;
    }

    public Legs getLeggings() {
        return leggings;
    }

    public Boots getBoots() {
        return boots;
    }

    public Gloves getGloves() {
        return gloves;
    }


    public Helmet getHelmet() {
        return helmet;
    }

    public Ammunition getAmmo() { return this.ammo; }

    public void setAmmo(Ammunition ammo) { this.ammo = ammo; }

    /**
     * Unequips an item from the player.
     * Checks to see what type the item is and changes things accordingly.
     * @param i The item to unequip
     */
    public void unequip(Item i) {
        if (i instanceof Secondary) {
            leftHand = null;
        } else if (i instanceof Ammunition) {
            ammo = null;
        } else if (i instanceof Weapon) {
            weaponHandR = null;
        } else if (i instanceof Helmet) {
            helmet = null;
        } else if (i instanceof ChestPiece) {
            chestPiece = null;
        } else if (i instanceof Legs) {
            leggings = null;
        } else if (i instanceof Gloves) {
            gloves = null;
        } else if (i instanceof Boots) {
            boots = null;
        }
        i.setCurrentlyEquipped(false);
        unequipUpdateStats(i);
    }

    /**
     * Method to equip Weapons
     * @param w The weapon to equip
     */
    public void equip(Weapon w) {
        if(!w.isCurrentlyEquipped()) {
            if(w instanceof TwoHanded) { //Two handed Weapons
                if(leftHand != null) {
                    unequipUpdateStats((Item) leftHand);
                    ((Item) leftHand).setCurrentlyEquipped(false);
                    leftHand = null;
                }
                if(this.weaponHandR != null) {
                    unequipUpdateStats(weaponHandR);
                    weaponHandR.setCurrentlyEquipped(false);
                    weaponHandR = null;
                }
                weaponHandR = w;
                w.setCurrentlyEquipped(true);
                equipUpdateStats(w);
            } else if (w instanceof Ammunition) {
                if(this.ammo == null) {
                    ammo = (Ammunition) w;
                } else {
                    unequipUpdateStats(this.ammo);
                    this.ammo.setCurrentlyEquipped(false);
                    this.ammo = (Ammunition) w;
                }
                w.setCurrentlyEquipped(true);
                equipUpdateStats(w);
            } else { //One handed Weapons
                if(this.weaponHandR == null) {
                    weaponHandR = w;
                } else {
                    unequipUpdateStats(this.weaponHandR);
                    this.weaponHandR.setCurrentlyEquipped(false);
                    weaponHandR = w;
                }
                w.setCurrentlyEquipped(true);
                equipUpdateStats(w);
            }
        }
    }

    /**
     * Method to equip armors
     * @param a The Armor to be equipped
     */
    public void equip(Armor a) {
        if(!a.isCurrentlyEquipped()) {
            a.setCurrentlyEquipped(true);
            if (a instanceof Boots) {
                if(boots == null) {
                    boots = (Boots) a;
                } else {
                    boots.setCurrentlyEquipped(false);
                    unequipUpdateStats(boots);
                    boots = (Boots) a;
                }
            } else if (a instanceof ChestPiece) {
                if(chestPiece == null) {
                    chestPiece = (ChestPiece) a;
                } else {
                    chestPiece.setCurrentlyEquipped(false);
                    unequipUpdateStats(chestPiece);
                    chestPiece = (ChestPiece) a;
                }
            } else if (a instanceof Gloves) {
                if(gloves == null) {
                    gloves = (Gloves) a;
                } else {
                    gloves.setCurrentlyEquipped(false);
                    unequipUpdateStats(gloves);
                    gloves = (Gloves) a;
                }
            } else if (a instanceof Helmet) {
                if(helmet == null) {
                    helmet = (Helmet) a;
                } else {
                    helmet.setCurrentlyEquipped(false);
                    unequipUpdateStats(helmet);
                    helmet = (Helmet) a;
                }
            } else if (a instanceof Legs) {
                if(leggings == null) {
                    leggings = (Legs) a;
                } else {
                    leggings.setCurrentlyEquipped(false);
                    unequipUpdateStats(leggings);
                    leggings = (Legs) a;
                }
            } else if(a instanceof Secondary) {
                if(leftHand == null) {
                    leftHand = (Shield) a;
                } else {
                    ((Item) leftHand).setCurrentlyEquipped(false);
                    unequipUpdateStats((Item) leftHand);
                    leftHand = (Shield) a;
                }
            }
            equipUpdateStats(a);
        }
    }

    public void equip(Secondary s) {
        if(!((Item) s).isCurrentlyEquipped()) {
            ((Item) s).setCurrentlyEquipped(true);
            if(weaponHandR != null && weaponHandR instanceof TwoHanded) {
             weaponHandR.setCurrentlyEquipped(false);
                unequipUpdateStats(weaponHandR);
                weaponHandR = null;
            }
            if(leftHand == null) {
                leftHand = s;
            } else {
                ((Item) leftHand).setCurrentlyEquipped(false);
                unequipUpdateStats((Item) leftHand);
                leftHand = s;
            }
            equipUpdateStats((Item) s);
        }
    }

    public void consume(Consumable p) {
        removeSingleItem(p);
        modifyCurrentHP(((Potion) p).getAmount());
    }

    public LinkedList<Item> getInventory() {
        return inventory;
    }

    /**
     * Adds a single item to the inventory
     * @param i The item to be added
     */
    public boolean addItem(Item i) {
        if (i.getWeight() <= (getCarryCap() - getCurrentCarry())) {
            this.modifyCurrentCarry(i.getWeight());
            this.inventory.add(i);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Removes a single item from the inventory
     * @param i The item to be removed
     */
    public void removeSingleItem(Item i) {
        this.inventory.remove(i);
        this.modifyCurrentCarry(-i.getWeight());
    }

    public int getXp() {
        return xp;
    }

    public void increaseXP(int xp) {
        records.increaseTotalXP(xp);
        if(this.xp + xp < 100) {
            this.xp += xp;
        }
        else {
            lvlUp((this.xp + xp) - 100);
        }
    }

    /**
     * Method to level up the character and increase stats
     */
    public void lvlUp(int leftoverXP) {
        super.lvlUp(leftoverXP);
        this.xp = leftoverXP;
    }

    public int getGold() {
        return gold;
    }

    public void modifyGold(int gold) {
        records.increaseTotalGold(gold);
        this.gold += gold;
    }

    public void prepareSerializeQuests() {
        this.activeQuestsSer = QuestHandler.activeQuests;
        this.completeQuestsSer = QuestHandler.completeQuests;
        this.inActiveQuestsSer = QuestHandler.inActiveQuests;
    }

    public void deserializeQuests() {
        QuestHandler.activeQuests = this.activeQuestsSer;
        QuestHandler.completeQuests = this.completeQuestsSer;
        QuestHandler.inActiveQuests = this.inActiveQuestsSer;
    }

    public int getTextScrollingSpeed() {
        return textScrollingSpeed;
    }

    public void setTextScrollingSpeed(int textScrollingSpeed) {
        this.textScrollingSpeed = textScrollingSpeed;
    }

    public Records getRecords() {
        return records;
    }
}