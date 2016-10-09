package items.Weapons;

import items.Rarity;
import items.SpellType;
import items.TwoHanded;
import main.GameStage;

public class Staff extends Magic implements TwoHanded {

    public Staff(int atk, int magic, int cooldown, double weight, int manaBoost, int value, Rarity howRare, WeaponType weaponType, SpellType spellType, int manaCost) {
        super(atk, magic, cooldown, weight, manaBoost, value, howRare, weaponType, spellType, manaCost);
        setInfo(null, null, null);
    }

    public Staff(int atk, int magic, int cooldown, double weight, int manaBoost, int value, Rarity howRare, WeaponType weaponType,
                 SpellType spellType, int manaCost, String imgLoc, String name, String tooltip) {
        super(atk, magic, cooldown, weight, manaBoost, value, howRare, weaponType, spellType, manaCost);
        setInfo(imgLoc, name, tooltip);
    }

    public Staff() {
        super(10, 15, 500, 10.4, 40, 300, randomRareness(), getRandomWeaponType(), getRandomSpellType(), GameStage.getRandom(14) + 1);
        setInfo(null, null, null);
    }

    private void setInfo(String imgLoc, String name, String tooltip) {
        if(imgLoc == null) {
            this.setImageLocation("file:Images\\Weapons\\" + this.getWeaponType().toString() + "\\Staff.png");
        } else {
            this.setImageLocation(imgLoc);
        }
        if(name == null) {
            this.setSimpleName(this.spellType + " staff");
        } else {
            this.setSimpleName(name);
        }
        if(tooltip == null) {
            this.setItemToolTipText("A " + this.spellType + " staff");
        } else {
            this.setItemToolTipText(tooltip);
        }
    }
}
