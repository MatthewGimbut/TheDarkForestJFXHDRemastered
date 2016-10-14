package items.accessories;

import items.Rarity;

public class Ring extends Accessory {

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
    public Ring(int cooldownReduction, int manaRegenBoost, int hpRegenBoost, int staminaRegenBoost, double weight, int hpBoost, int manaBoost, int value, Rarity howRare) {
        super(cooldownReduction, manaRegenBoost, hpRegenBoost, staminaRegenBoost, weight, hpBoost, manaBoost, value, howRare);
        setInfo(null, null, null);
    }

    public Ring() {
        super(0, 0, 0, 0, 2.3, 15, 15, 100, Rarity.UNCOMMON);
        this.cooldownReduction = -25;
        this.manaRegenBoost = -50;
        this.hpRegenBoost = -225;
        this.staminaRegenBoost = -100;
        setInfo(null, null, null);
    }

    private void setInfo(String imgLoc, String name, String tooltip) {
        if(imgLoc == null) {
            this.setImageLocation("file:Images\\Accessories\\ring.png");
        } else {
            this.setImageLocation(imgLoc);
        }
        if(name == null) {
            this.setSimpleName("test ring");
        } else {
            this.setSimpleName(name);
        }
        if(tooltip == null) {
            this.setItemToolTipText("test ring");
        } else {
            this.setItemToolTipText(tooltip);
        }
    }
}
