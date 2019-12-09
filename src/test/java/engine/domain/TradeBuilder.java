package engine.domain;

public class TradeBuilder {
    private int qty;
    private int price;
    private int buyUid;
    private int sellUid;

    public Trade build() {
        Trade trade = new Trade();
        trade.setQty(qty);
        trade.setPrice((short) price);
        trade.setBuyUid(buyUid);
        trade.setSellUid(sellUid);
        return trade;
    }

    public TradeBuilder withPrice(int price) {
        this.price = price;
        return this;
    }

    public TradeBuilder withBuyUid(int buyUid) {
        this.buyUid = buyUid;
        return this;
    }

    public TradeBuilder withSellUid(int sellUid) {
        this.sellUid = sellUid;
        return this;
    }

    public TradeBuilder withQty(int qty) {
        this.qty = qty;
        return this;
    }
}
