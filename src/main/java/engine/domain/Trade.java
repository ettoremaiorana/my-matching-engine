package engine.domain;

public class Trade {
    private int buyUid;
    private int sellUid;
    private int qty;
    private short price;

    public int getBuyUid() {
        return buyUid;
    }

    public void setBuyUid(int buyUid) {
        this.buyUid = buyUid;
    }

    public int getSellUid() {
        return sellUid;
    }

    public void setSellUid(int sellUid) {
        this.sellUid = sellUid;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public short getPrice() {
        return price;
    }

    public void setPrice(short price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return buyUid == trade.buyUid &&
                sellUid == trade.sellUid &&
                qty == trade.qty &&
                price == trade.price;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "buyUid=" + buyUid +
                ", sellUid=" + sellUid +
                ", qty=" + qty +
                ", price=" + price +
                '}';
    }
}
