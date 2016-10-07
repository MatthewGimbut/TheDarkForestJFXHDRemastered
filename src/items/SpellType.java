package items;


public enum SpellType {

    Fire001 {
        public String northCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Fire\\north.png";
        }

        public String southCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Fire\\south.png";
        }
        
        public String eastCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Fire\\east.png";
        }

        public String westCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Fire\\west.png";
        }

        public int getBaseProjectileSpeed() {
            return 6;
        }

        public SpellType[] getStrengths() {
            return new SpellType[] { Ice001 };
        }

        public SpellType[] getWeaknesses() {
            return new SpellType[] { Water001 };
        }

        public String toString() {
            return "Fire";
        }

    },

    Water001 {

        public String northCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Water\\north.png";
        }

        public String southCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Water\\south.png";
        }

        public String eastCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Water\\east.png";
        }

        public String westCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Water\\west.png";
        }


        public int getBaseProjectileSpeed() {
            return 7;
        }

         
        public SpellType[] getStrengths() {
            return new SpellType[] { Fire001 };
        }

         
        public SpellType[] getWeaknesses() {
            return new SpellType[] { Ice001 };
        }


        public String toString() {
            return "Water";
        }
    },

    Ice001 {

        public String northCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Ice\\north.png";
        }

        public String southCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Ice\\south.png";
        }

        public String eastCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Ice\\east.png";
        }

        public String westCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Ice\\west.png";
        }

         
        public int getBaseProjectileSpeed() {
            return 6;
        }

         
        public SpellType[] getStrengths() {
            return new SpellType[] { Earth001 };
        }

         
        public SpellType[] getWeaknesses() {
            return new SpellType[] { Fire001 };
        }


        public String toString() {
            return "Ice";
        }
    },

    Earth001 {
        public String northCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Earth\\north.png";
        }

        public String southCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Earth\\south.png";
        }

        public String eastCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Earth\\east.png";
        }

        public String westCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Earth\\west.png";
        }
        public int getBaseProjectileSpeed() {
            return 4;
        }
        public SpellType[] getStrengths() {
            return new SpellType[] { Fire001 };
        }
        public SpellType[] getWeaknesses() {
            return new SpellType[] { Water001 };
        }
        public String toString() {
            return "Earth";
        }
    },

    Lightning001 {
        public String northCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Lightning\\north.png";
        }

        public String southCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Lightning\\south.png";
        }

        public String eastCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Lightning\\east.png";
        }

        public String westCastImageLocation() {
            return Item.SPELL_BASE_LOC + "Basic\\Lightning\\west.png";
        }
        public int getBaseProjectileSpeed() {
            return 8;
        }
        public SpellType[] getStrengths() {
            return new SpellType[] { Water001 };
        }
        public SpellType[] getWeaknesses() {
            return new SpellType[] { Earth001 };
        }
        public String toString() { return "Lightning"; }
    };

    public abstract String northCastImageLocation();
    public abstract String southCastImageLocation();
    public abstract String eastCastImageLocation();
    public abstract String westCastImageLocation();
    public abstract int getBaseProjectileSpeed();
    public abstract SpellType[] getStrengths();
    public abstract SpellType[] getWeaknesses();
    public abstract String toString();
}
