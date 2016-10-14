package items.accessories;

import items.Item;
import items.Rarity;
import main.GameStage;

public class Accessory extends Item {

    protected double cooldownReduction;
    protected int manaRegenBoost;
    protected int hpRegenBoost;
    protected int staminaRegenBoost;

    /**
     * Constructor for item that takes all the stats for the specific item, usually set in the subclass
     *
     * @param weight
     * @param hpBoost
     * @param manaBoost
     * @param value
     * @param howRare
     */
    public Accessory(int cooldownReduction, int manaRegenBoost, int hpRegenBoost, int staminaRegenBoost,
                     double weight, int hpBoost, int manaBoost, int value, Rarity howRare) {
        super(0, 0, 0, 0, weight, hpBoost, manaBoost, value, howRare);
        this.cooldownReduction = cooldownReduction;
        this.manaRegenBoost = manaRegenBoost;
        this.hpRegenBoost = hpRegenBoost;
        this.staminaRegenBoost = staminaRegenBoost;
    }

    public double getCooldownReduction() {
        return cooldownReduction;
    }

    public void setCooldownReduction(double cooldownReduction) {
        this.cooldownReduction = cooldownReduction;
    }

    public int getManaRegenBoost() {
        return manaRegenBoost;
    }

    public void setManaRegenBoost(int manaRegenBoost) {
        this.manaRegenBoost = manaRegenBoost;
    }

    public int getHpRegenBoost() {
        return hpRegenBoost;
    }

    public void setHpRegenBoost(int hpRegenBoost) {
        this.hpRegenBoost = hpRegenBoost;
    }

    public int getStaminaRegenBoost() {
        return staminaRegenBoost;
    }

    public void setStaminaRegenBoost(int staminaRegenBoost) {
        this.staminaRegenBoost = staminaRegenBoost;
    }
}
