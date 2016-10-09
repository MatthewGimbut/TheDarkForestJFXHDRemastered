package items.ammunition;

import items.Rarity;
import items.Weapons.Weapon;
import items.Weapons.WeaponType;
import main.GameStage;

import java.text.DecimalFormat;

public abstract class Ammunition extends Weapon implements Stackable {

    protected int ammoCount;
    protected int damage;
    protected double individualWeight;
    public static final String AMMO_BASE_LOC = "file:Images\\Weapons\\";

    /**
     * Constructor for item that takes all the stats for the specific item, usually set in the subclass
     *
     * @param atk
     * @param cooldown
     * @param weight
     * @param value
     * @param howRare
     */
    public Ammunition(int atk, int cooldown, double weight, int value, Rarity howRare, WeaponType weaponType, int count, int damage) {
        super(0, 0, 0, cooldown, weight, 0, 0, value, howRare, weaponType);
        this.ammoCount = count;
        this.damage = damage;
        this.individualWeight = weight;
    }

    public int getCount() {
        return this.ammoCount;
    }

    public void decrementAmmoCount() throws OutOfAmmoException {
        if(this.ammoCount > 0) {
            this.ammoCount--;
        } else {
            this.ammoCount = 0;
            throw new OutOfAmmoException("Player out of ammo!");
        }
    }

    public void modifyAmmoCount(int mod) {
        this.ammoCount += mod;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public double getWeight() {
        return Double.parseDouble(GameStage.df.format(this.individualWeight * this.ammoCount));
    }

    @Override
    public int getValue() {
        return this.value * ammoCount;
    }

    @Override
    public void addToStack(int add) {
        this.ammoCount += add;
    }

    @Override
    public void removeFromStack(int remove) {
        this.ammoCount -= remove;
    }

    @Override
    public void combine(Stackable s) {
        this.ammoCount += s.getCount();
        this.setInfo(this.getImageLocation(), null, null);
    }

    public abstract void setInfo(String imgLoc, String name, String tooltip);
    public abstract String northLaunchImageLocation();
    public abstract String southLaunchImageLocation();
    public abstract String eastLaunchImageLocation();
    public abstract String westLaunchImageLocation();

}
