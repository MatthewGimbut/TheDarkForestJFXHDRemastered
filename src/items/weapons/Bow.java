package items.Weapons;

import items.Item;
import items.Rarity;
import items.TwoHanded;

public class Bow extends Weapon implements Projectile, TwoHanded {

    private static final int BASE_BOW_ROF = 750;

    public Bow(int atk, double weight, int value, Rarity howRare, WeaponType weaponType) {
        super(atk, 0, 0, BASE_BOW_ROF, weight, 0, 0, value, howRare, weaponType);
        setInfo(null, null, null);
    }

    public Bow() {
        super(8, 0, 0, BASE_BOW_ROF, 9.2, 0, 0, 254, Item.randomRareness(), Weapon.getRandomWeaponType());
        setInfo(null, null, null);
    }

    public Bow(int atk, double weight, int value, Rarity howRare, WeaponType weaponType, String imgLoc, String name, String tooltip) {
        super(atk, 0, 0, BASE_BOW_ROF, weight, 0, 0, value, howRare, weaponType);
        setInfo(imgLoc, name, tooltip);
    }

    @Override
    public int getProjectileSpeed() {
        return 13;
    }

    @Override
    public int getStaminaCost() { return 10; }

    private void setInfo(String imgLoc, String name, String tooltip) {
        if(imgLoc == null) {
            this.setImageLocation("file:Images\\Weapons\\Bow.png");
        } else {
            this.setImageLocation(imgLoc);
        }
        if(name == null) {
            this.setSimpleName(this.getWeaponType().toString() + " bow");
        } else {
            this.setSimpleName(name);
        }
        if(tooltip == null) {
            this.setItemToolTipText("A " + this.getWeaponType().toString() + " bow");
        } else {
            this.setItemToolTipText(tooltip);
        }
    }
}
