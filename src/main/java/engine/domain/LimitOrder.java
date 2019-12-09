package engine.domain;

/***
 *This message contained within a single line, with comma separated values:
 *
 * Field Index	Type		Description
 * 0		char		‘B’ for a buy order, ‘S’ for a sell order
 * 1		int		unique order identifier
 * 2		short		price in pence (>0)
 * 3		int		quantity of order (>0)
 *
 * Example:
 *
 * B,100322,5103,7500
 *
 * Description:
 * Limit order id 100322: Buy 7,500 at 5,103p,
 ***/
public class LimitOrder extends Order {

    @Override
    public OrderBookEntry makeOrderBookEntry() {
        OrderBookEntry entry = new OrderBookEntry();
        entry.setPrice(getPrice());
        entry.setUid(getUid());
        entry.setQty(getQty());
        return entry;
    }

    @Override
    public String toString() {
        return String.format("Limit order id %d: %s %d at %dp",
                getUid(),
                getOrderSide() == OrderSide.BUY ? "Buy" : "Sell",
                getQty(),
                getPrice());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimitOrder order = (LimitOrder) o;
        return getUid() == order.getUid() &&
                getQty() == order.getQty() &&
                getPrice() == order.getPrice() &&
                getOrderSide() == order.getOrderSide();
    }
}
