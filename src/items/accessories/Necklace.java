package items.accessories;

import items.Armor.Armor;
import items.Armor.ArmorType;
import items.Rarity;

public class Necklace extends Accessory {

    /**
     * Constructor for item that takes all the stats for the specific item, usually set in the subclass
     *
     * @param cooldownReduction
     * @param manaRegenBoost
     * @param hpRegenBoost
     * @param staminaRegenBoost
     * @param weight
     * @param hpBoost
     * @param manaBoost
     * @param value
     * @param howRare
     */
    public Necklace(int cooldownReduction, int manaRegenBoost, int hpRegenBoost, int staminaRegenBoost, double weight, int hpBoost, int manaBoost, int value, Rarity howRare, ArmorType armorType) {
        super(cooldownReduction, manaRegenBoost, hpRegenBoost, staminaRegenBoost, weight, hpBoost, manaBoost, value, howRare, armorType);
        setInfo(null, null, null);
    }

    public Necklace(int cooldownReduction, int manaRegenBoost, int hpRegenBoost, int staminaRegenBoost, double weight, int hpBoost,
                int manaBoost, int value, Rarity howRare, ArmorType armorType, String imgLoc, String name, String tooltip) {
        super(cooldownReduction, manaRegenBoost, hpRegenBoost, staminaRegenBoost, weight, hpBoost, manaBoost, value, howRare, armorType);
        setInfo(imgLoc, name, tooltip);
    }

    public Necklace() {
        super(0, 0, 0, 0, 5.5, 30, 30, 100, Rarity.UNCOMMON, Armor.getRandomArmorType());
        this.cooldownReduction = -50;
        this.manaRegenBoost = -100;
        this.hpRegenBoost = -450;
        this.staminaRegenBoost = -200;
        setInfo(null, null, null);
    }

    private void setInfo(String imgLoc, String name, String tooltip) {
        if(imgLoc == null) {
            this.setImageLocation("file:Images\\Accessories\\necklace.png");
        } else {
            this.setImageLocation(imgLoc);
        }
        if(name == null) {
            this.setSimpleName(this.getArmorType() + " necklace");
        } else {
            this.setSimpleName(name);
        }
        if(tooltip == null) {
            this.setItemToolTipText("A " + this.getArmorType() + " necklace");
        } else {
            this.setItemToolTipText(tooltip);
        }
    }
}
