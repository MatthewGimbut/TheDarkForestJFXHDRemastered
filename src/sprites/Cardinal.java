package sprites;

public enum Cardinal {
    North {
        public Cardinal getOpposite() { return South; }
        public String toString() { return "North"; }
    }, South {
        public Cardinal getOpposite() { return North; }
        public String toString() { return "South"; }
    }, East {
        public Cardinal getOpposite() { return East; }
        public String toString() { return "East"; }
    }, West {
        public Cardinal getOpposite() { return West; }
        public String toString() { return "West"; }
    };

    public abstract Cardinal getOpposite();
}