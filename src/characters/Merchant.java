package characters;

import items.Item;
import main.GameStage;
import java.util.ArrayList;

public class Merchant extends Character {

    private MerchantType merchantType;
    public static final int NUM_MERCHANT_TYPES = 6;

    public Merchant(String name, int gold, MerchantType merchantType) {
        super(name, 1, 100, 100, 125, 125, 1, 1, 1, 1, 1000, 1000, gold, new ArrayList<>());
        this.merchantType = merchantType;
        addRandomItems(20);
    }

    public Merchant(String name, int gold, ArrayList<Item> inventory, MerchantType merchantType) {
        super(name, 1, 100, 100, 125, 125, 1, 1, 1, 1, 1000, 1000, gold, inventory);
        this.merchantType = merchantType;
    }

    public Merchant(MerchantType merchantType) {
        super(GameStage.getRandomName(), 1, 100, 100, 125, 125, 1, 1, 1, 1, 1000, 1000, GameStage.getRandom(301), new ArrayList<>());
        this.merchantType = merchantType;
        addRandomItems(20);
    }

    public Merchant() {
        super(GameStage.getRandomName(), 1, 100, 100, 125, 125, 1, 1, 1, 1, 1000, 1000, GameStage.getRandom(301), new ArrayList<>());
        this.merchantType = getRandomMerchantType();
        addRandomItems(20);
        this.south = Character.GENERIC_NEUTRAL_SOUTH;
        this.north = Character.GENERIC_NEUTRAL_NORTH;
        this.east = Character.GENERIC_NEUTRAL_EAST;
        this.west = Character.GENERIC_NEUTRAL_WEST;
        this.setImage(south);
    }

    public MerchantType getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(MerchantType merchantType) {
        this.merchantType = merchantType;
    }

    public static MerchantType getRandomMerchantType() {
        switch(GameStage.getRandom(NUM_MERCHANT_TYPES)) {
            case 0:
                return MerchantType.Archery;
            case 1:
                return MerchantType.Armor;
            case 2:
                return MerchantType.Crafting;
            case 3:
                return MerchantType.Magic;
            case 4:
                return MerchantType.Potions;
            case 5:
                return MerchantType.Weapons;
            default:
                return null;
        }
    }

    private void addRandomItems(int number) {
        for(int i = 0; i < number; i++) {
            this.inventory.add(Item.generateRandomItem());
        }
    }
}
