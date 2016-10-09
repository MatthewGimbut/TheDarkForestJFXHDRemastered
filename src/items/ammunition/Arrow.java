package items.ammunition;

import items.Item;
import items.Rarity;
import items.Weapons.Weapon;
import items.Weapons.WeaponType;
import main.GameStage;

public class Arrow extends Ammunition {

    /**
     * Constructor for item that takes all the stats for the specific item, usually set in the subclass
     *
     * @param cooldown
     * @param value
     * @param howRare
     * @param weaponType
     * @param count
     * @param damage
     */
    public Arrow(int cooldown, int value, Rarity howRare, WeaponType weaponType, int count, int damage) {
        super(0, cooldown, 0.15, value, howRare, weaponType, count, damage);
        this.ammoCount = count;
        this.damage = damage;
        setInfo(null, null, null);
    }

    public Arrow() {
        super(0, 20, 0.15, 20, Rarity.COMMON, Weapon.getRandomWeaponType(), GameStage.getRandom() + 2, 5); //TODO Fix this so it scales
        setInfo(null, null, null);
    }

    public Arrow(int cooldown, int value, Rarity howRare, WeaponType weaponType, int count, int damage, String imgLoc, String name, String tooltip) {
        super(0, cooldown, 0.15, value, howRare, weaponType, count, damage);
        this.ammoCount = count;
        this.damage = damage;
        setInfo(imgLoc, name, tooltip);
    }

    public void decrementAmmoCount() throws OutOfAmmoException {
        super.decrementAmmoCount();
        this.setSimpleName(this.getWeaponType().toString() + " arrows (" + this.ammoCount + ")");
        this.setItemToolTipText(this.getWeaponType().toString() + " arrows (" + this.ammoCount + ")");
    }

    @Override
    public void setInfo(String imgLoc, String name, String tooltip) {
        if(imgLoc == null) {
            this.setImageLocation("file:Images\\Weapons\\Arrow.png");
        } else {
            this.setImageLocation(imgLoc);
        }
        if(name == null) {
            this.setSimpleName(this.getWeaponType().toString() + " arrows (" + this.ammoCount + ")");
        } else {
            this.setSimpleName(name);
        }
        if(tooltip == null) {
            this.setItemToolTipText(this.getWeaponType().toString() + " arrows (" + this.ammoCount + ")");
        } else {
            this.setItemToolTipText(tooltip);
        }
    }

    @Override
    public String northLaunchImageLocation() {
        return AMMO_BASE_LOC + this.getWeaponType().toString() + "\\Arrow\\north.png";
    }

    @Override
    public String southLaunchImageLocation() {
        return AMMO_BASE_LOC + this.getWeaponType().toString() + "\\Arrow\\south.png";
    }

    @Override
    public String eastLaunchImageLocation() {
        return AMMO_BASE_LOC + this.getWeaponType().toString() + "\\Arrow\\east.png";
    }

    @Override
    public String westLaunchImageLocation() {
        return AMMO_BASE_LOC + this.getWeaponType().toString() + "\\Arrow\\west.png";
    }


}
