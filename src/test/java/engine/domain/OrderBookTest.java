package engine.domain;

import engine.codec.OrderBookEncoder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OrderBookTest {

    private static final String SIMPLE_ORDER_BOOK = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            |1234567890|1,234,567,890| 32,503| 32,504|1,234,567,890|1234567891|
            |      1138|        7,500| 31,502| 32,505|        7,777|      6808|
            |          |             |       | 32,507|        3,000|     42100|
            +-----------------------------------------------------------------+
            """;

    private OrderBook canonicalOrderBook;

    @Before
    public void setup() {
        canonicalOrderBook = new OrderBook();
        Order orderB1 = customOrder('B', 0, (short)32503, 1234567890, 1234567890);
        Order orderB2 = customOrder('B', 0, (short)31502, 7500, 1138);
        Order orderS1 = customOrder('S', 0, (short)32504, 1234567890, 1234567891);
        Order orderS2 = customOrder('S', 0, (short)32505, 7777, 6808);
        Order orderS3 = customOrder('S', 0, (short)32507, 3000, 42100);
        canonicalOrderBook.add(orderS2);
        canonicalOrderBook.add(orderS1);
        canonicalOrderBook.add(orderB2);
        canonicalOrderBook.add(orderS3);
        canonicalOrderBook.add(orderB1);
    }
    @Test
    public void canonicalOrderBookTest() {
        assertEquals(SIMPLE_ORDER_BOOK, OrderBookEncoder.encode(
                canonicalOrderBook.getAllBidEntries(), canonicalOrderBook.getAllAskEntries()).toString());
    }

    @Test
    public void testAddSellEntryToEmptyOrderBook() {
        OrderBook orderBook = new OrderBook();
        Order order = simpleOrder('S', 0);
        orderBook.add(order);
        assertEquals(new OrderBookEntryBuilder()
                .withId(123456)
                .withPrice((short)100)
                .withVolume(1000)
                .build(), orderBook.getAllAskEntries().next());
        assertFalse(orderBook.getAllBidEntries().hasNext());
    }

    @Test
    public void testBuyEntryToEmptyOrderBook() {
        OrderBook orderBook = new OrderBook();
        Order order = simpleOrder('B', 0);
        orderBook.add(order);
        assertEquals(new OrderBookEntryBuilder()
                .withId(123456)
                .withPrice((short)100)
                .withVolume(1000)
                .build(), orderBook.getAllBidEntries().next());
        assertFalse(orderBook.getAllAskEntries().hasNext());
    }

    @Test
    public void testTwoSidedOrderBook() {
        OrderBook orderBook = new OrderBook();
        Order orderB1 = simpleOrder('B', 0);
        Order orderB2 = customOrder('B', 0, (short)85, 3000, 123458);
        Order orderS1 = customOrder('S', 0, (short)105, 1000, 123457);
        Order orderS2 = customOrder('S', 0, (short)110, 2000, 123467);
        orderBook.add(orderS2);
        orderBook.add(orderS1);
        orderBook.add(orderB2);
        orderBook.add(orderB1);

        var buyIt = orderBook.getAllBidEntries();
        assertEquals(new OrderBookEntryBuilder()
                .withId(123456)
                .withPrice((short)100)
                .withVolume(1000)
                .build(), buyIt.next());
        assertEquals(new OrderBookEntryBuilder()
                .withId(123458)
                .withPrice((short)85)
                .withVolume(3000)
                .build(), buyIt.next());
        assertFalse(buyIt.hasNext());

        var sellIt = orderBook.getAllAskEntries();
        assertEquals(new OrderBookEntryBuilder()
                .withId(123457)
                .withPrice((short)105)
                .withVolume(1000)
                .build(), sellIt.next());
        assertEquals(new OrderBookEntryBuilder()
                .withId(123467)
                .withPrice((short)110)
                .withVolume(2000)
                .build(), sellIt.next());
        assertFalse(sellIt.hasNext());
    }

    @Test
    public void popSellTest() {
        OrderBook orderBook = new OrderBook();
        Order order = simpleOrder('S', 0);
        Order orderS1 = customOrder('S', 0, (short)105, 1000, 123457);
        Order orderS2 = customOrder('S', 0, (short)110, 2000, 123467);
        orderBook.add(order);
        orderBook.add(orderS1);
        orderBook.add(orderS2);

        //
        var entry = new OrderBookEntryBuilder()
                .withId(123456)
                .withPrice((short)100)
                .withVolume(1000)
                .build();

        var z = orderBook.getAllAskEntries();
        var temp = z.next();
        z.remove();
        orderBook.onPop('S');

        var it = orderBook.getAllAskEntries();
        assertEquals(new OrderBookEntryBuilder()
                .withId(123457)
                .withPrice((short)105)
                .withVolume(1000)
                .build(), it.next());
        assertEquals(new OrderBookEntryBuilder()
                .withId(123467)
                .withPrice((short)110)
                .withVolume(2000)
                .build(), it.next());
        assertFalse(it.hasNext());

        //
        Order orderS3 = customOrder('S', 0, (short)105, 2000, 111111);
        orderBook.add(orderS3);
        //
        var entry1 = new OrderBookEntryBuilder()
                .withId(123457)
                .withPrice((short)105)
                .withVolume(1000)
                .build();
        z = orderBook.getAllAskEntries();
        temp = z.next();
        z.remove();

        orderBook.onPop('S');
        var it1 = orderBook.getAllAskEntries();
        assertEquals(new OrderBookEntryBuilder()
                .withId(111111)
                .withPrice((short)105)
                .withVolume(2000)
                .build(), it1.next());
        assertEquals(new OrderBookEntryBuilder()
                .withId(123467)
                .withPrice((short)110)
                .withVolume(2000)
                .build(), it1.next());
        assertFalse(it1.hasNext());

    }


    @Test
    public void popBuyTest() {
        OrderBook orderBook = new OrderBook();
        Order order = simpleOrder('B', 0);
        Order orderS1 = customOrder('B', 0, (short)105, 1000, 123457);
        Order orderS2 = customOrder('B', 0, (short)110, 2000, 123467);
        orderBook.add(order);
        orderBook.add(orderS1);
        orderBook.add(orderS2);

        //
        var entry = new OrderBookEntryBuilder()
                .withId(123467)
                .withPrice((short)110)
                .withVolume(2000)
                .build();

        var z = orderBook.getAllBidEntries();
        var temp = z.next();
        z.remove();

        orderBook.onPop('B');
        var it = orderBook.getAllBidEntries();
        assertEquals(new OrderBookEntryBuilder()
                .withId(123457)
                .withPrice((short)105)
                .withVolume(1000)
                .build(), it.next());
        assertEquals(new OrderBookEntryBuilder()
                .withId(123456)
                .withPrice((short)100)
                .withVolume(1000)
                .build(), it.next());
        assertFalse(it.hasNext());

        //
        Order orderS3 = customOrder('B', 0, (short)105, 2000, 111111);
        orderBook.add(orderS3);
        //
        var entry1 = new OrderBookEntryBuilder()
                .withId(123457)
                .withPrice((short)105)
                .withVolume(1000)
                .build();
        z = orderBook.getAllBidEntries();
        temp = z.next();
        z.remove();

        orderBook.onPop('B');
        var it1 = orderBook.getAllBidEntries();
        assertEquals(new OrderBookEntryBuilder()
                .withId(111111)
                .withPrice((short)105)
                .withVolume(2000)
                .build(), it1.next());
        assertEquals(new OrderBookEntryBuilder()
                .withId(123456)
                .withPrice((short)100)
                .withVolume(1000)
                .build(), it1.next());
        assertFalse(it1.hasNext());
    }

    private Order simpleOrder(char s, int peak) {
        OrderBuilder ob = new OrderBuilder();
        ob.withOrderSide(s);
        ob.withPeak(peak);
        ob.withPrice((short) 100);
        ob.withQty(1000);
        ob.withUid(123456);
        return ob.build();
    }

    private Order customOrder(char s, int peak, short price, int qty, int uid) {
        OrderBuilder ob = new OrderBuilder();
        ob.withOrderSide(s);
        ob.withPeak(peak);
        ob.withPrice(price);
        ob.withQty(qty);
        ob.withUid(uid);
        return ob.build();
    }
}
