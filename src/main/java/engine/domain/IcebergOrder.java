package engine.domain;

/***
 * Field Index	Type		Description
 * 0		char		‘B’ for a buy order, ‘S’ for a sell order
 * 1		int		unique order identifier (>0)
 * 2		short		price in pence (>0)
 * 3		int		quantity of order (>0)
 * 4		int		peak size (>0)
 *
 * Example:
 *
 * S,100345,5103,100000,10000
 *
 * Description:
 * Iceberg order id 100345: Sell 100,000 at 5103p, with a peak size of 10,000
 **/
public class IcebergOrder extends Order {
    private int peak;

    public int getPeak() {
        return peak;
    }

    public void setPeak(int peak) {
        this.peak = peak;
    }

    @Override
    public OrderBookEntry makeOrderBookEntry() {
        short price = getPrice();
        IcebergOrderBookEntry entry = new IcebergOrderBookEntry();
        entry.setPrice(price);
        entry.setUid(getUid());
        entry.setQty(Math.min(peak, getQty()));
        entry.setPeak(getPeak());
        entry.setOriginalQty(getQty());
        return entry;
    }

    @Override
    public String toString() {
        return String.format("Iceberg order id %d: %s %d at %dp, with a peak size of %d",
                getUid(),
                getOrderSide() == OrderSide.BUY ? "Buy" : "Sell",
                getQty(),
                getPrice(),
                getPeak());
    }
}
