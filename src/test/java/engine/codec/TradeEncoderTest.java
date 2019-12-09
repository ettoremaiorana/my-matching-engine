package engine.codec;

import engine.domain.Trade;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TradeEncoderTest {

    private static final String SIMPLE_TRADE = "100322,100345,5103,7500";
    @Test
    public void simpleTradeEncodingTest() {
        var trade = new Trade();
        trade.setBuyUid(100322);
        trade.setSellUid(100345);
        trade.setPrice((short) 5103);
        trade.setQty(7500);
        var buffer = TradeEncoder.encode(trade);
        assertEquals(SIMPLE_TRADE, buffer);
    }
}
