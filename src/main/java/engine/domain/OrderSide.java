package engine.domain;

public enum OrderSide {
    BUY('B'),
    SELL('S');

    private char identifier;
    OrderSide(char id) {
        this.identifier = id;
    }

    public char getIdentifier() {
        return identifier;
    }

    public static OrderSide fromId(char id) {
        if (id == 'B') return BUY;
        if (id == 'S') return SELL;
        else return null;
    }
}
