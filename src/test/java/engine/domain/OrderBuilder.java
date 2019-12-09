package engine.domain;

public class OrderBuilder {
    private short price;
    private int qty;
    private char orderSide = 'Z';
    private int uid;
    private int peak;
    private Order order;

    public Order build () {
        if (price == 0 || orderSide == 'Z' || uid == 0 || qty == 0) {
            throw new IllegalStateException("The order is not completely built");
        }
        if (peak > 0) {
            order = new IcebergOrder();
            ((IcebergOrder)order).setPeak(peak);
//            ((IcebergOrder)order).setOriginalQty(qty);
        }
        else {
            order = new LimitOrder();
        }
        order.setOrderSide(orderSide);
        order.setPrice(price);
        order.setQty(qty);
        order.setUid(uid);
        return order;
    }

    public void withPrice(short price) {
        this.price = price;
    }

    public void withQty(int qty) {
        this.qty = qty;
    }

    public void withOrderSide(char orderSide) {
        this.orderSide = orderSide;
    }

    public void withUid(int uid) {
        this.uid = uid;
    }

    public void withPeak(int peak) {
        this.peak = peak;
    }
}
