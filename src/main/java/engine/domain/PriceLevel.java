package engine.domain;

import java.util.*;

public class PriceLevel {

    private final LinkedList<OrderBookEntry> entries = new LinkedList<>();
    private final short priceLevel;

    public PriceLevel(short priceLevel) {
        this.priceLevel = priceLevel;
    }

    public void add(OrderBookEntry entry) {
        entries.add(entry);
    }

    public PriceLevelIt iterator() {
        return new PriceLevelIt(entries);
    }

    public static class PriceLevelIt implements Iterator<OrderBookEntry> {
        private final LinkedList<OrderBookEntry> entries;
        private int index = 0;
        private int lastIndex = -1;

        public PriceLevelIt(LinkedList<OrderBookEntry> entries) {
            this.entries = entries;
        }

        @Override
        public boolean hasNext() {
            return index <= entries.size() - 1;
        }

        @Override
        public OrderBookEntry next() {
            OrderBookEntry e = entries.get(index);
            lastIndex = index;
            index++;
            return e;
        }

        public void add(OrderBookEntry e) {
            entries.addLast(e);
        }

        public void remove() {
            entries.remove(lastIndex);
            lastIndex--;
            index--;
        }
    }
}
