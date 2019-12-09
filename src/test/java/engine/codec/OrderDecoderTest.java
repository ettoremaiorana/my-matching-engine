package engine.codec;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderDecoderTest {

    private static final String LIMIT_ORDER = "B,100322,5103,7500";
    private static final String ICEBERG_ORDER = "S,100345,5103,100000,10000";

    @Test
    public void decodeLimitOrderTest() {
        var order = OrderDecoder.decode(LIMIT_ORDER);
        assertEquals("Limit order id 100322: Buy 7500 at 5103p", order.toString());
    }

    @Test
    public void decodeIcebergOrderTest() {
        var order = OrderDecoder.decode(ICEBERG_ORDER);
        assertEquals("Iceberg order id 100345: Sell 100000 at 5103p, with a peak size of 10000", order.toString());
    }
}
