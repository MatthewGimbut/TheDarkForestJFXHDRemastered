package items.accessories;

import items.Item;
import items.Rarity;

public abstract class Accessory extends Item {

    /**
     * Constructor for item that takes all the stats for the specific item, usually set in the subclass
     *
     * @param atk
     * @param magic
     * @param def
     * @param cooldownReduction
     * @param weight
     * @param hpBoost
     * @param manaBoost
     * @param value
     * @param howRare
     */
    public Accessory(int atk, int magic, int def, int cooldownReduction, double weight, int hpBoost, int manaBoost, int value, Rarity howRare) {
        super(atk, magic, def, cooldownReduction, weight, hpBoost, manaBoost, value, howRare);
    }


}
