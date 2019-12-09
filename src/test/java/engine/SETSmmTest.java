package engine;

import engine.codec.OrderBookEncoder;
import engine.codec.OrderDecoder;
import engine.domain.IcebergOrderBookEntry;
import engine.domain.OrderBook;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SETSmmTest {

    private static final String AGGRESSIVE_ICEBERG_ORDER_ENTRY_BEFORE = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            |      1111|       50,000|     99|    100|       10,000|      2222|
            |      3333|       25,500|     98|    100|        7,500|      4444|
            |          |             |       |    101|       20,000|      5555|
            +-----------------------------------------------------------------+
            """;

    private static final String AGGRESSIVE_ICEBERG_ORDER_ENTRY_AFTER = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            |      6666|       10,000|    100|    101|       20,000|      5555|
            |      1111|       50,000|     99|       |             |          |
            |      3333|       25,500|     98|       |             |          |
            +-----------------------------------------------------------------+
            """;

    private static final String PASSIVE_ICEBERG_ORDER_ENTRY_FIRST = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            |      6666|       10,000|    100|    101|       20,000|      5555|
            |      1111|       50,000|     99|       |             |          |
            |      3333|       25,500|     98|       |             |          |
            +-----------------------------------------------------------------+
            """;

    private static final String PASSIVE_ICEBERG_ORDER_ENTRY_SECOND = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            |      6666|        9,000|    100|    101|       20,000|      5555|
            |      1111|       50,000|     99|       |             |          |
            |      3333|       25,500|     98|       |             |          |
            +-----------------------------------------------------------------+
            """;

    private static final String PASSIVE_ICEBERG_ORDER_ENTRY_NEW_ICEBERG = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            |      6666|        9,000|    100|    101|       20,000|      5555|
            |      7777|       20,000|    100|       |             |          |
            |      1111|       50,000|     99|       |             |          |
            |      3333|       25,500|     98|       |             |          |
            +-----------------------------------------------------------------+
            """;

    private static final String PASSIVE_ICEBERG_ORDER_ENTRY_THIRD = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            |      6666|        4,000|    100|    101|       20,000|      5555|
            |      7777|       20,000|    100|       |             |          |
            |      1111|       50,000|     99|       |             |          |
            |      3333|       25,500|     98|       |             |          |
            +-----------------------------------------------------------------+
            """;


    @Test
    public void aggressiveIcebergOrderEntryTest() {
        //setup
        OrderBook orderBook = new OrderBook();
        orderBook.add(OrderDecoder.decode("B,1111,99,50000"));
        orderBook.add(OrderDecoder.decode("B,3333,98,25500"));
        orderBook.add(OrderDecoder.decode("S,2222,100,10000"));
        orderBook.add(OrderDecoder.decode("S,4444,100,7500"));
        orderBook.add(OrderDecoder.decode("S,5555,101,20000"));
        //end setup
        var trades = MatchingEngine.match(OrderDecoder.decode("B,6666,100,100000,10000"), orderBook);
        assertEquals(AGGRESSIVE_ICEBERG_ORDER_ENTRY_AFTER, OrderBookEncoder.encode(orderBook));
        assertEquals(2, trades.size());

        trades = MatchingEngine.match(OrderDecoder.decode("S,2222,100,10000"), orderBook);
        assertEquals(PASSIVE_ICEBERG_ORDER_ENTRY_FIRST, OrderBookEncoder.encode(orderBook));
        assertEquals(1, trades.size());

        trades = MatchingEngine.match(OrderDecoder.decode("S,2222,1,11000"), orderBook);
        assertEquals(PASSIVE_ICEBERG_ORDER_ENTRY_SECOND, OrderBookEncoder.encode(orderBook));
        assertEquals(1, trades.size());

        trades = MatchingEngine.match(OrderDecoder.decode("B,7777,100,50000,20000"), orderBook);
        assertEquals(PASSIVE_ICEBERG_ORDER_ENTRY_NEW_ICEBERG, OrderBookEncoder.encode(orderBook));
        assertEquals(0, trades.size());

        trades = MatchingEngine.match(OrderDecoder.decode("S,2222,1,35000"), orderBook);
        assertEquals(PASSIVE_ICEBERG_ORDER_ENTRY_THIRD, OrderBookEncoder.encode(orderBook));
        assertEquals(2, trades.size());
    }
}
