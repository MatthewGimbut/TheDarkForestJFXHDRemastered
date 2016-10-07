package items.Weapons;

import items.Item;
import items.Rarity;
import items.SpellType;
import items.TwoHanded;

public class Staff extends Magic implements TwoHanded {

    public Staff(int atk, int magic, int cooldown, double weight, int manaBoost, int value, Rarity howRare, WeaponType weaponType, SpellType spellType) {
        super(atk, magic, cooldown, weight, manaBoost, value, howRare, weaponType, spellType);
        setInfo();
    }

    public Staff() {
        super(10, 0, 500, 10.4, 40, 300, Item.randomRareness(), Weapon.getRandomWeaponType(), Magic.getRandomSpellType());
        setInfo();
    }

    private void setInfo() {
        this.setImageLocation("file:Images\\Weapons\\" + this.getWeaponType().toString() + "\\Staff.png");
        this.setSimpleName(this.spellType + " staff");
        this.setItemToolTipText("A " + this.spellType + " staff");
    }
}
