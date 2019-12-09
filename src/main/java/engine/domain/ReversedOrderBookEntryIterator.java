package engine.domain;

public class ReversedOrderBookEntryIterator extends OrderBookEntryIterator {

    public ReversedOrderBookEntryIterator(PriceLevel[] buckets, int startIndex, int endIndex) {
        super(buckets, startIndex, endIndex);
    }

    public boolean hasMoreCursors() {
        return index >= endIndex;
    }

    protected void doMoveCursor() {
        index--;
    }
}
