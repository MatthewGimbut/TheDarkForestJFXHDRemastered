package items.Weapons;

import items.Item;
import items.Rarity;
import items.Secondary;
import items.SpellType;
import items.Weapons.Weapon;

public class SpellTome extends Weapon implements Secondary {

    private int projectileSpeed;
    private SpellType spellType;

    /**
     * Constructor for item that takes all the stats for the specific item, usually set in the subclass
     *
     * @param magic
     * @param cooldown
     * @param weight
     * @param manaBoost
     * @param value
     * @param howRare
     */
    public SpellTome(int magic, int cooldown, double weight, int manaBoost, int value, Rarity howRare, SpellType spellType, int speedMod) {
        super(0, magic, 0, cooldown, weight, 0, manaBoost, value, howRare, null);
        this.spellType = spellType;
        this.projectileSpeed = spellType.getBaseProjectileSpeed() + speedMod;
        setInfo();
    }

    public SpellTome() {
        super(0, 5, 0, 1, 5.4, 0, 10, 100, Item.randomRareness(), null);
        this.spellType = Item.getRandomSpellType();
        this.projectileSpeed = 5;
        setInfo();
    }

    public int getProjectileSpeed() {
        return this.projectileSpeed;
    }

    private void setInfo() {
        this.setImageLocation("file:Images\\Weapons\\bookbase.png");
        this.setSimpleName(this.spellType + " tome");
        this.setItemToolTipText("A " + this.spellType + "spell tome");
    }

    public SpellType getSpellType() {
        return this.spellType;
    }
}
