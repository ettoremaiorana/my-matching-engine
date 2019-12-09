package engine.domain;

public class OrderBookEntry {

    private int uid;
    private int qty;
    private short price;

    public short getPrice() {
        return price;
    }

    public int getUid() {
        return uid;
    }

    public int getQty() {
        return qty;
    }

    public void setPrice(short price) {
        this.price = price;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof OrderBookEntry)) return false;
        OrderBookEntry that = (OrderBookEntry) o;
        return uid == that.uid &&
                qty == that.qty &&
                price == that.price;
    }

    @Override
    public String toString() {
        return "OrderBookEntry{" +
                "uid=" + uid +
                ", qty=" + qty +
                ", price=" + price +
                '}';
    }

    public void onPop(OrderBookEntryIterator it) {
        //nothing to do
    }
}
