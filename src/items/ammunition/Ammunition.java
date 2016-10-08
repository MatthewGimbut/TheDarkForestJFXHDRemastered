package items.ammunition;

import items.Rarity;
import items.Weapons.Weapon;
import items.Weapons.WeaponType;

public abstract class Ammunition extends Weapon {

    protected int ammoCount;
    protected int damage;
    protected double individualWeight;

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

    public int getAmmoCount() {
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
        return this.individualWeight * this.ammoCount;
    }
}
