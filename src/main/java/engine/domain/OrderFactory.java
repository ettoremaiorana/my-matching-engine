package engine.domain;

public class OrderFactory {
    public static LimitOrder makeLimitOrder(char orderSide,
                                     int uid,
                                     short price,
                                     int qty) {
        LimitOrder order = new LimitOrder();
        order.setOrderSide(orderSide);
        order.setUid(uid);
        order.setPrice(price);
        order.setQty(qty);
        return order;
    }

    public static IcebergOrder makeIcebergOrder(char orderSide,
                                     int uid,
                                     short price,
                                     int qty,
                                     int peak) {
        IcebergOrder order = new IcebergOrder();
        order.setPeak(peak);
        order.setOrderSide(orderSide);
        order.setUid(uid);
        order.setPrice(price);
        order.setQty(qty);
        return order;
    }
}
