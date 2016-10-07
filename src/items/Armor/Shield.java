package items.Armor;

import items.Item;
import items.Rarity;
import items.Secondary;

/**
 * Shield armor class
 * @author Matthew Gimbut
 *
 */
public class Shield extends Armor implements Secondary {

	public Shield(int atk, int magic, int def, int cooldown, double weight, int hpBoost, int manaBoost, int value, Rarity howRare, ArmorType armorType) {
		super(atk, magic, def, weight, hpBoost, manaBoost, value, howRare, armorType);
		this.setCooldown(cooldown);
		this.setImageLocation("Images\\Armors\\" + this.getArmorType().toString() + "\\Shield.png");
		this.setSimpleName(this.getArmorType() + " shield");
		this.setItemToolTipText("A " + this.getArmorType() + " shield.");
	}
	
	public Shield(int atk, int magic, int def, int cooldown, double weight, int hpBoost, int manaBoost, int value,
			Rarity howRare, ArmorType weaponType, String uniqueImage, String uniqueName, String uniqueToolTip) {
		super(atk, magic, def, weight, hpBoost, manaBoost, value, howRare, weaponType);
		this.setCooldown(cooldown);
		this.setImageLocation(uniqueImage);
		this.setSimpleName(uniqueName);
		this.setItemToolTipText(uniqueToolTip);
	}
	
	public Shield() {
		super(2, 0, 6, 4, 0, 0, 100, Item.randomRareness(), Armor.getRandomArmorType());
		this.setCooldown(2);
		this.setImageLocation("Images\\Armors\\" + this.getArmorType().toString() +  "\\Shield.png");
		this.setSimpleName(this.getArmorType() + " shield");
		this.setItemToolTipText("A " + this.getArmorType() + " shield");
	}
}
