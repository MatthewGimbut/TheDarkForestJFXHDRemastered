package characters;

public enum MerchantType {

    Crafting {
        public String[] getGenericDialog() {
            return new String[] { "I sell miscellaneous junk items that haven't been added yet." };
        }
    },

    Weapons {
        @Override
        public String[] getGenericDialog() {
            return new String[] { "I sell weapons!", "Would you like to see my wares?" };
        }
    },

    Armor {
        @Override
        public String[] getGenericDialog() {
            return new String[] { "I sell armors!", "Please, look around." };
        }
    },

    Magic {
        @Override
        public String[] getGenericDialog() {
            return new String[] { "I sell magic items such as staves and spell tomes.", "Please buy my stuff my wife and kids are starving." };
        }
    },

    Potions {
        @Override
        public String[] getGenericDialog() {
            return new String[] { "I sell potions.", "Would you like to take a look?" };
        }
    },

    Archery {
        @Override
        public String[] getGenericDialog() {
            return new String[] { "I sell bows and crossbows and shit.", "\uD83D\uDDFF\uD83D\uDD2B"};
        }
    };

    public abstract String[] getGenericDialog();
}
