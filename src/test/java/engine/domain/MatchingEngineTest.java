package engine.domain;

import engine.MatchingEngine;
import engine.codec.OrderDecoder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MatchingEngineTest {

    private OrderBook orderBook;

    @Before
    public void setup() {
        /**
         * +-----------------------------------------------------------------+
         * | BUY                            | SELL                           |
         * | Id       | Volume      | Price | Price | Volume      | Id       |
         * +----------+-------------+-------+-------+-------------+----------+
         * |1234567890|1,234,567,890| 32,503| 32,504|1,234,567,890|1234567891|
         * |      1138|        7,500| 31,502| 32,505|        7,777|      6808|
         * |          |             |       | 32,507|        3,000|     42100|
         * +-----------------------------------------------------------------+
         */
        orderBook = new OrderBook();
        orderBook.add(OrderDecoder.decode("B,1234567890,32503,1234567890"));
        orderBook.add(OrderDecoder.decode("B,1138,32502,7500"));
        orderBook.add(OrderDecoder.decode("S,1234567891,32504,1234567890"));
        orderBook.add(OrderDecoder.decode("S,6808,32505,7777"));
        orderBook.add(OrderDecoder.decode("S,42100,32507,3000"));
    }

    @Test
    public void matchBuyHitsLvl2Test() {
        var order = OrderDecoder.decode("B,54321,32505,1234567891");
        var trades = MatchingEngine.match(order, orderBook);
        assertEquals(2, trades.size());
        var it = trades.iterator();
        assertEquals(new TradeBuilder()
                .withBuyUid(54321)
                .withSellUid(1234567891)
                .withQty(1234567890)
                .withPrice(32504)
                .build(), it.next());
        assertEquals(new TradeBuilder()
                .withBuyUid(54321)
                .withSellUid(6808)
                .withQty(1)
                .withPrice(32505)
                .build(), it.next());
        //assert order book has changed
        var orderBookIt = orderBook.getAllAskEntries();
        assertEquals(new OrderBookEntryBuilder()
                .withId(6808)
                .withPrice((short)32505)
                .withVolume(7776)
                .build(), orderBookIt.next());
        assertEquals(new OrderBookEntryBuilder()
                .withId(42100)
                .withPrice((short)32507)
                .withVolume(3000)
                .build(), orderBookIt.next());
        assertFalse(orderBookIt.hasNext());
    }

    @Test
    public void matchBuyHitsLvl1Test() {
        var order = OrderDecoder.decode("B,54321,32504,1");
        var trades = MatchingEngine.match(order, orderBook);
        assertEquals(1, trades.size());
        assertEquals(new TradeBuilder()
                .withBuyUid(54321)
                .withSellUid(1234567891)
                .withQty(1)
                .withPrice(32504)
                .build(), trades.iterator().next());
    }

    @Test
    public void matchBuyHigherQtyPlaceBuyOrderBookEntry() {
        var order = OrderDecoder.decode("B,54321,32504,1234567891");
        var trades = MatchingEngine.match(order, orderBook);
        assertEquals(1, trades.size());
        assertEquals(new TradeBuilder()
                .withBuyUid(54321)
                .withSellUid(1234567891)
                .withQty(1234567890)
                .withPrice(32504)
                .build(), trades.iterator().next());

        var newOrder = orderBook.getAllBidEntries().next();
        assertEquals(new OrderBookEntryBuilder()
                .withVolume(1)
                .withPrice((short) 32504)
                .withId(54321)
                .build(), newOrder);
    }

    @Test
    public void matchBuyExhaustSellSide(){
        var order = OrderDecoder.decode("B,54321,32510,2000000000");
        var trades = MatchingEngine.match(order, orderBook);
        assertEquals(3, trades.size());
        assertFalse(orderBook.getAllAskEntries().hasNext());
    }

    @Test
    public void matchSellHitsLvl2Test() {
        var order = OrderDecoder.decode("S,54321,32500,1234567891");
        var trades = MatchingEngine.match(order, orderBook);
        assertEquals(2, trades.size());
        var it = trades.iterator();
        assertTrue(trades.contains(new TradeBuilder()
                .withBuyUid(1234567890)
                .withSellUid(54321)
                .withQty(1234567890)
                .withPrice(32503)
                .build()));
        assertTrue(trades.contains(new TradeBuilder()
                .withBuyUid(1138)
                .withSellUid(54321)
                .withQty(1)
                .withPrice(32502)
                .build()));
        //assert order book has changed
        var orderBookIt = orderBook.getAllBidEntries();
        assertEquals(new OrderBookEntryBuilder()
                .withId(1138)
                .withPrice((short)32502)
                .withVolume(7499)
                .build(), orderBookIt.next());
        assertFalse(orderBookIt.hasNext());
    }

    @Test
    public void matchSellWithIcebergOrderTest() {
        orderBook = new OrderBook();
        orderBook.add(OrderDecoder.decode("B,1234567890,4,1234567890"));

        orderBook.add(OrderDecoder.decode("B,1,5,10000,4000"));
        orderBook.add(OrderDecoder.decode("B,2,5,10000"));
        var order = OrderDecoder.decode("S,54321,1,19000");
        var trades = MatchingEngine.match(order, orderBook);
        assertEquals(2, trades.size());
        var it = trades.iterator();
        assertTrue(trades.contains(new TradeBuilder()
                .withBuyUid(1)
                .withSellUid(54321)
                .withQty(9000)
                .withPrice(5)
                .build()));
        assertTrue(trades.contains(new TradeBuilder()
                .withBuyUid(2)
                .withSellUid(54321)
                .withQty(10000)
                .withPrice(5)
                .build()));
//        assert order book has changed
        var orderBookIt = orderBook.getAllBidEntries();
        //B,1,32502,10000,4000
        assertEquals(new OrderBookEntryBuilder()
                .withId(1)
                .withPrice((short)5)
                .withVolume(1000)
                .build(), orderBookIt.next());
        assertTrue(orderBookIt.hasNext());
    }

}
