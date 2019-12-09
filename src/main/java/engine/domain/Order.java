package engine.domain;

public abstract class Order {
    private int uid;
    private int qty;
    private short price;
    private OrderSide orderSide;

    public OrderSide getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(char orderSide) {
        this.orderSide = OrderSide.fromId(orderSide);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public abstract OrderBookEntry makeOrderBookEntry();

    @Override
    public String toString() {
        return "Order{" +
                "uid=" + uid +
                ", qty=" + qty +
                ", price=" + price +
                ", orderSide=" + orderSide +
                '}';
    }
}
