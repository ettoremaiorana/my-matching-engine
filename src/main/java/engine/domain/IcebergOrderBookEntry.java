package engine.domain;

public class IcebergOrderBookEntry extends OrderBookEntry {
    private int peak;
    private int originalQty;

    @Override
    public void onPop(OrderBookEntryIterator it) {
        originalQty -= peak;
        if (originalQty > 0) {
            it.add(copy());
        }
    }

    private OrderBookEntry copy() {
        IcebergOrderBookEntry entry = new IcebergOrderBookEntry();
        entry.setPrice(getPrice());
        entry.setUid(getUid());
        entry.setQty(Math.min(peak, originalQty));
        entry.setOriginalQty(originalQty);
        entry.setPeak(peak);
        return entry;
    }

    public void setPeak(int peak) {
        this.peak = peak;
    }

    public void setOriginalQty(int originalQty) {
        this.originalQty = originalQty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IcebergOrderBookEntry that = (IcebergOrderBookEntry) o;
        return getUid() == that.getUid() &&
                getQty() == that.getQty() &&
                getPrice() == that.getPrice();
    }

}
