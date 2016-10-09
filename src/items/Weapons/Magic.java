package items.Weapons;

import items.Rarity;
import items.SpellType;

import java.util.Random;

public abstract class Magic extends Weapon implements Projectile{

    protected SpellType spellType;
    protected int projectileSpeed;

    public Magic(int atk, int magic, int speedModifier, double weight, int manaBoost, int value, Rarity howRare, WeaponType weaponType, SpellType spellType) {
        super(atk, magic, 0, speedModifier, weight, 0, manaBoost, value, howRare, weaponType);
        this.spellType = spellType;
        this.projectileSpeed = spellType.getBaseProjectileSpeed();
        this.setCooldown((int) (this.getCooldown() * spellType.getCooldownModifier()));
    }

    public static SpellType getRandomSpellType() {
        Random randy = new Random();
        SpellType st = null;
        switch(randy.nextInt(5)) {
            case 0:
                st = SpellType.Fire001;
                break;
            case 1:
                st = SpellType.Ice001;
                break;
            case 2:
                st = SpellType.Earth001;
                break;
            case 3:
                st = SpellType.Lightning001;
                break;
            case 4:
                st = SpellType.Water001;
                break;
        }
        return st;
    }

    public int getProjectileSpeed() {
        return this.projectileSpeed;
    }

    public SpellType getSpellType() {
        return this.spellType;
    }
}
