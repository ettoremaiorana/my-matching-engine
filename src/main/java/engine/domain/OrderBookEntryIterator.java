package engine.domain;

import java.util.Iterator;

public class OrderBookEntryIterator implements Iterator<OrderBookEntry> {
    protected final PriceLevel[] buckets;
    protected final int endIndex;
    protected int index;
    protected PriceLevel.PriceLevelIt currentPriceLevel;

    public OrderBookEntryIterator(PriceLevel[] buckets, int startIndex, int endIndex) {
        this.buckets = buckets;
        this.endIndex = endIndex;
        this.index = startIndex;
    }

    public boolean hasMoreCursors() {
        return index <= endIndex;
    }

    @Override
    public boolean hasNext() {
        return hasMoreCursors() || (currentPriceLevel != null && currentPriceLevel.hasNext());
    }

    private void moveCursor() {
        do {
            doMoveCursor();
        } while(hasMoreCursors() && !buckets[index].iterator().hasNext());
    }

    protected void doMoveCursor() {
        index++;
    }

    @Override
    public OrderBookEntry next() {
        if (!hasNext()) {
            throw new IndexOutOfBoundsException();
        }
        if (currentPriceLevel == null || !currentPriceLevel.hasNext()) {
            currentPriceLevel = buckets[index].iterator();
            moveCursor();
        }
        return currentPriceLevel.next();
    }

    @Override
    public void remove() {
        currentPriceLevel.remove();
    }

    public void add(OrderBookEntry entry) {
        currentPriceLevel.add(entry);
    }

}
