package items.Weapons;

import items.Item;
import items.Rarity;
import items.Secondary;
import items.SpellType;
import items.Weapons.Weapon;

public class SpellTome extends Magic implements Secondary {

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
        super(0, magic, cooldown, weight, manaBoost, value, howRare, null, spellType);
        this.projectileSpeed = spellType.getBaseProjectileSpeed() + speedMod;
        setInfo();
    }

    public SpellTome() {
        super(0, 5, 200, 5.4, 10, 100, Item.randomRareness(), null, Magic.getRandomSpellType());
        setInfo();
    }

    private void setInfo() {
        this.setImageLocation("file:Images\\Weapons\\bookbase.png");
        this.setSimpleName(this.spellType + " tome");
        this.setItemToolTipText("A " + this.spellType + " spell tome");
        this.atk = 0;
    }
}
