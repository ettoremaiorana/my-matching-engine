package engine.domain;

import engine.codec.OrderBookEncoder;

public class OrderBook {

    private final PriceLevel[] bidBuckets = new PriceLevel[Short.MAX_VALUE];
    private final PriceLevel[] askBuckets = new PriceLevel[Short.MAX_VALUE];

    private short askMin = Short.MAX_VALUE;
    private short askMax = Short.MIN_VALUE;
    private short bidMin = Short.MAX_VALUE;
    private short bidMax = Short.MIN_VALUE;

    public OrderBook() {
        for(int i = 0; i < bidBuckets.length; i++) {
            bidBuckets[i] = new PriceLevel((short) i);
            askBuckets[i] = new PriceLevel((short) i);
        }
    }

    public OrderBookEntryIterator getAllBidEntries() {
        return new ReversedOrderBookEntryIterator(bidBuckets, bidMax, bidMin);
    }

    public OrderBookEntryIterator getAllAskEntries() {
        return new OrderBookEntryIterator(askBuckets, askMin, askMax);
    }

    public void add(Order order) {
        short price = order.getPrice();
        OrderBookEntry entry = order.makeOrderBookEntry();

        if (order.getOrderSide() == OrderSide.BUY) {
            bidBuckets[price].add(entry);
            bidMax = (short) Math.max(bidMax, price);
            bidMin = (short) Math.min(bidMin, price);
        }
        else if(order.getOrderSide() == OrderSide.SELL) {
            askBuckets[price].add(entry);
            askMin = (short) Math.min(askMin, price);
            askMax = (short) Math.max(askMax, price);
        }
    }

    public void onPop(char orderSide) {
        if (orderSide == 'B') {
            if (!bidBuckets[bidMax].iterator().hasNext()) {
                findNextBidMax();
            }
        }
        else {
            if (!askBuckets[askMin].iterator().hasNext()) {
                findNextAskMin();
            }
        }
    }

    private void findNextAskMin() {
        while (askMin <= askMax) {
            askMin++;
            if (askBuckets[askMin].iterator().hasNext())
                return;
        }
        //sell side is empty
        askMin = Short.MAX_VALUE;
        askMax = Short.MIN_VALUE;
    }

    private void findNextBidMax() {
        while (bidMax >= bidMin) {
            bidMax--;
            if (bidBuckets[bidMax].iterator().hasNext())
                return;
        }
        //buy side is empty
        bidMin = Short.MAX_VALUE;
        bidMax = Short.MIN_VALUE;
    }

    @Override
    public String toString() {
        return OrderBookEncoder.encode(this.getAllBidEntries(), this.getAllAskEntries());
    }
}