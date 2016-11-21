package characters;

import items.Weapons.Staff;
import items.accessories.Accessory;
import items.accessories.Necklace;
import items.accessories.Ring;
import items.ammunition.Ammunition;
import items.Armor.*;
import items.Consumables.Consumable;
import items.Consumables.Potion;
import items.Item;
import items.Secondary;
import items.TwoHanded;
import items.Weapons.Weapon;
import items.ammunition.Stackable;
import javafx.animation.PauseTransition;
import main.Records;

import quests.Quest;
import quests.QuestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;

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
    private int xp;
    private int hpRegen; //hpRegen, manaRegen, staminaRegen are in milliseconds
    private int manaRegen;
    private int currentStamina, maxStamina, staminaRegen;
    private Weapon weaponHandR;
    private Secondary leftHand;
    private ChestPiece chestPiece;
    private Legs leggings;
    private Boots boots;
    private Gloves gloves;
    private Helmet helmet;
    private Records records;
    private Ammunition ammo;
    private Necklace necklace;
    private Ring ring1;
    private Ring ring2;

    private List<Quest> activeQuestsSer;
    private List<Quest> completeQuestsSer;
    private List<Quest> inActiveQuestsSer;
    private Quest priorityQuest;

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
        hpRegen = 3500; //Base HP regeneration rate
        manaRegen = 250; //Base mana regeneration rate
        staminaRegen = 150;
        currentStamina = 150;
        maxStamina = 150;
        textScrollingSpeed = 45; //Milliseconds
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
            unequipUpdateStats(i);
        } else if (i instanceof Necklace) {
            necklace = null;
            unequipUpdateStats(i);
            reverseAccessoryStatChange((Accessory) i);
        } else if(i instanceof Ring) {
            if(ring1 == i) {
                ring1 = null;
            } else if(ring2 == i) {
                ring2 = null;
            } else {
                throw new RuntimeException("How the fuck did this get here if neither was equipped to begin with???");
            }
            unequipUpdateStats(i);
            reverseAccessoryStatChange((Accessory) i);
        } else if (i instanceof Ammunition) {
            ammo = null;
        } else if (i instanceof Weapon) {
            weaponHandR = null;
            unequipUpdateStats(i);
        } else if (i instanceof Helmet) {
            helmet = null;
            unequipUpdateStats(i);
        } else if (i instanceof ChestPiece) {
            chestPiece = null;
            unequipUpdateStats(i);
        } else if (i instanceof Legs) {
            leggings = null;
            unequipUpdateStats(i);
        } else if (i instanceof Gloves) {
            gloves = null;
            unequipUpdateStats(i);
        } else if (i instanceof Boots) {
            boots = null;
            unequipUpdateStats(i);
        }
        i.setCurrentlyEquipped(false);
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
                    this.ammo.setCurrentlyEquipped(false);
                    unequip(this.ammo);
                    this.ammo = (Ammunition) w;
                }
                w.setCurrentlyEquipped(true);
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
            } else if(a instanceof  Accessory) {
                equip((Accessory) a);
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

    public void equip(Accessory a) {
        a.setCurrentlyEquipped(true);
        if(a instanceof  Necklace) {
            if(this.necklace == null) {
                this.necklace = (Necklace) a;
            } else {
                this.necklace.setCurrentlyEquipped(false);
                unequipUpdateStats(necklace);
                reverseAccessoryStatChange(necklace);
                this.necklace = (Necklace) a;

            }
        } else if (a instanceof  Ring) {
            if(this.ring1 == null) {
                this.ring1 = (Ring) a;
            } else if(ring2 == null) {
                this.ring2 = (Ring) a;
            } else {
                this.ring2.setCurrentlyEquipped(false);
                unequipUpdateStats(ring2);
                reverseAccessoryStatChange(ring2);
                this.ring2 = (Ring) a;
            }
        } else {
            throw new RuntimeException("User tried to equip an accessory that has not been accounted for!");
        }
        this.speed += a.getCooldownReduction();
        this.manaRegen += a.getManaRegenBoost();
        this.hpRegen += a.getHpRegenBoost();
        this.staminaRegen += a.getStaminaRegenBoost();

        //equipUpdateStats(a);
    }

    private void reverseAccessoryStatChange(Accessory a) {
        this.speed -= a.getCooldownReduction();
        this.manaRegen -= a.getManaRegenBoost();
        this.hpRegen -= a.getHpRegenBoost();
        this.staminaRegen -= a.getStaminaRegenBoost();
    }

    public void consume(Consumable p) {
        removeSingleItem(p);
        modifyCurrentHP(((Potion) p).getAmount());
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
        Random r = new Random();
        int staminaBoost = r.nextInt(7);
        this.currentStamina += staminaBoost;
        this.maxStamina += staminaBoost;
        this.xp = leftoverXP;
    }

    public void prepareSerializeQuests() {
        this.activeQuestsSer = QuestHandler.activeQuests;
        this.completeQuestsSer = QuestHandler.completeQuests;
        this.inActiveQuestsSer = QuestHandler.inActiveQuests;
        this.priorityQuest = QuestHandler.priorityQuest;
    }

    public void deserializeQuests() {
        QuestHandler.activeQuests = this.activeQuestsSer;
        QuestHandler.completeQuests = this.completeQuestsSer;
        QuestHandler.inActiveQuests = this.inActiveQuestsSer;
        if(this.priorityQuest != null) {
            QuestHandler.setPriorityQuest(this.priorityQuest);
        }
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

    public int getHpRegen() {
        return hpRegen;
    }

    public void setHpRegen(int hpRegen) {
        this.hpRegen = hpRegen;
    }

    public int getManaRegen() {
        return manaRegen;
    }

    public void setManaRegen(int manaRegen) {
        this.manaRegen = manaRegen;
    }

    public int getCurrentStamina() {
        return currentStamina;
    }

    public void setCurrentStamina(int currentStamina) {
        if(currentStamina <= maxStamina) {
            this.currentStamina = currentStamina;
        } else {
            this.currentStamina = this.maxStamina;
        }
    }

    public void modifyGold(int gold) {
        super.modifyGold(gold);
        if(gold > 0) {
            records.increaseTotalGold(gold);
        }
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }

    public int getStaminaRegen() {
        return staminaRegen;
    }

    public void setStaminaRegen(int staminaRegen) {
        this.staminaRegen = staminaRegen;
    }

    public void modifyCurrentStamina(int mod) {
        this.currentStamina += mod;
    }

    public Ring getRing2() {
        return ring2;
    }

    public void setRing2(Ring ring2) {
        this.ring2 = ring2;
    }

    public Ring getRing1() {
        return ring1;
    }

    public void setRing1(Ring ring1) {
        this.ring1 = ring1;
    }

    public Necklace getNecklace() {
        return necklace;
    }

    public void setNecklace(Necklace necklace) {
        this.necklace = necklace;
    }


}