package items.Weapons;

import items.Item;
import items.Rarity;
import items.TwoHanded;


public class Crossbow extends Weapon implements Projectile, TwoHanded {

    private static final int BASE_CROSSBOW_ROF = 1000;

    public Crossbow(int atk, double weight, int value, Rarity howRare, WeaponType weaponType) {
        super(atk, 0, 0, BASE_CROSSBOW_ROF, weight, 0, 0, value, howRare, weaponType);
        setInfo(null, null, null);
    }

    public Crossbow() {
        super(10, 0, 0, BASE_CROSSBOW_ROF, 15.6, 0, 0, 250, Item.randomRareness(), Weapon.getRandomWeaponType());
        setInfo(null, null, null);
    }

    public Crossbow(int atk, double weight, int value, Rarity howRare, WeaponType weaponType,
                    String imgLoc, String name, String tooltip) {
        super(atk, 0, 0, BASE_CROSSBOW_ROF, weight, 0, 0, value, howRare, weaponType);
        setInfo(imgLoc, name, tooltip);
    }

    @Override
    public int getProjectileSpeed() {
        return 13;
    }

    @Override
    public int getStaminaCost() {
        return 13;
    }

    private void setInfo(String imgLoc, String name, String tooltip) {
        if(imgLoc == null) {
            this.setImageLocation("file:Images\\Weapons\\Crossbow.png");
        } else {
            this.setImageLocation(imgLoc);
        }
        if(name == null) {
            this.setSimpleName(this.getWeaponType().toString() + " crossbow");
        } else {
            this.setSimpleName(name);
        }
        if(tooltip == null) {
            this.setItemToolTipText("A " + this.getWeaponType().toString() + " crossbow");
        } else {
            this.setItemToolTipText(tooltip);
        }
    }
}
