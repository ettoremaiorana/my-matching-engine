package engine.codec;

import engine.domain.OrderBookEntryBuilder;
import engine.domain.OrderBookEntryIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookEncoderTest {

    private static final String EMPTY_ORDER_BOOK = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            +-----------------------------------------------------------------+
            """;

    private static final String EMPTY_SELL_SIDE_ORDER_BOOK = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            |1234567890|1,234,567,890| 32,503|       |             |          |
            |      1138|        7,500| 31,502|       |             |          |
            +-----------------------------------------------------------------+
            """;

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

    private static final String EMPTY_BUY_SIDE_ORDER_BOOK = """
            +-----------------------------------------------------------------+
            | BUY                            | SELL                           |
            | Id       | Volume      | Price | Price | Volume      | Id       |
            +----------+-------------+-------+-------+-------------+----------+
            |          |             |       | 32,504|1,234,567,890|1234567891|
            |          |             |       | 32,505|        7,777|      6808|
            |          |             |       | 32,507|        3,000|     42100|
            +-----------------------------------------------------------------+
            """;

    @Test
    public void testSimpleOrderBook() {
        var buyIterator = mockBuyIterator(2);
        var sellIterator = mockSellIterator(3);
        var buffer = OrderBookEncoder.encode(buyIterator, sellIterator);
        assertEquals(SIMPLE_ORDER_BOOK, buffer.toString());
    }

    @Test
    public void testEmptyBuySide() {
        var buyIterator = mockBuyIterator(0);
        var sellIterator = mockSellIterator(3);
        var buffer = OrderBookEncoder.encode(buyIterator, sellIterator);
        assertEquals(EMPTY_BUY_SIDE_ORDER_BOOK, buffer.toString());
    }

    @Test
    public void testEmptySellSide() {
        var buyIterator = mockBuyIterator(2);
        var sellIterator = mockSellIterator(0);
        var buffer = OrderBookEncoder.encode(buyIterator, sellIterator);
        assertEquals(EMPTY_SELL_SIDE_ORDER_BOOK, buffer.toString());
    }

    @Test
    public void testEmptyOrderBook() {
        var buyIterator = mockBuyIterator(0);
        var sellIterator = mockSellIterator(0);
        var buffer = OrderBookEncoder.encode(buyIterator, sellIterator);
        assertEquals(EMPTY_ORDER_BOOK, buffer.toString());
    }

    private OrderBookEntryIterator mockSellIterator(int n) {
        assert n <= 3;
        var sellIterator = Mockito.mock(OrderBookEntryIterator.class);
        var sellCounter = new AtomicInteger();
        when(sellIterator.hasNext()).thenAnswer(inv -> sellCounter.get() < n);
        when(sellIterator.next()).thenAnswer(inv -> {
            var count = sellCounter.incrementAndGet();
            if(count == 1) return new OrderBookEntryBuilder().withId(1234567891).withVolume(1234567890).withPrice((short) 32504).build();
            if(count == 2) return new OrderBookEntryBuilder().withId(6808).withVolume(7777).withPrice((short) 32505).build();
            if(count == 3) return new OrderBookEntryBuilder().withId(42100).withVolume(3000).withPrice((short) 32507).build();
            else throw new RuntimeException("Out of range");
        });
        return sellIterator;
    }

    private OrderBookEntryIterator mockBuyIterator(int n) {
        assert n <= 2;
        var buyIterator = Mockito.mock(OrderBookEntryIterator.class);
        var buyCounter = new AtomicInteger();
        when(buyIterator.hasNext()).thenAnswer(inv -> buyCounter.get() < n);
        when(buyIterator.next()).thenAnswer(inv -> {
            var count = buyCounter.incrementAndGet();
            if(count == 1) return new OrderBookEntryBuilder().withId(1234567890).withVolume(1234567890).withPrice((short) 32503).build();
            if(count == 2) return new OrderBookEntryBuilder().withId(1138).withVolume(7500).withPrice((short) 31502).build();
            else throw new RuntimeException("Out of range");
        });
        return buyIterator;
    }
}
