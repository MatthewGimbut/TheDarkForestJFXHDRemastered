package items.Weapons;

import items.Rarity;
import items.Secondary;
import items.SpellType;
import main.GameStage;

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
    public SpellTome(int magic, int cooldown, double weight, int manaBoost, int value, Rarity howRare, SpellType spellType, int speedMod, int manaCost) {
        super(0, magic, cooldown, weight, manaBoost, value, howRare, null, spellType, manaCost);
        this.projectileSpeed = spellType.getBaseProjectileSpeed() + speedMod;
        setInfo(null, null, null);
    }

    public SpellTome(int magic, int cooldown, double weight, int manaBoost, int value, Rarity howRare, SpellType spellType, int speedMod, int manaCost,
                     String imgLoc, String name, String tooltip) {
        super(0, magic, cooldown, weight, manaBoost, value, howRare, null, spellType, manaCost);
        this.projectileSpeed = spellType.getBaseProjectileSpeed() + speedMod;
        setInfo(imgLoc, name, tooltip);    }

    public SpellTome() {
        super(0, 5, 200, 5.4, 10, 100, randomRareness(), null, getRandomSpellType(), GameStage.getRandom(14) + 1);
        setInfo(null, null, null);
    }

    private void setInfo(String imgLoc, String name, String tooltip) {
        if(imgLoc == null) {
            this.setImageLocation("file:Images\\Weapons\\bookbase.png");
        } else {
            this.setImageLocation(imgLoc);
        }
        if(name == null) {
            this.setSimpleName(this.spellType + " tome");
        } else {
            this.setSimpleName(name);
        }
        if(tooltip == null) {
            this.setItemToolTipText("A " + this.spellType + " spell tome");
        } else {
            this.setItemToolTipText(tooltip);
        }
        this.atk = 0;
    }
}
