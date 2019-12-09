package engine.codec;

import engine.domain.Trade;

public class TradeEncoder {

    public static String encode(Trade trade) {
        StringBuilder sb = new StringBuilder(67);
        sb.setLength(0);
        sb.append(trade.getBuyUid());
        sb.append(",");
        sb.append(trade.getSellUid());
        sb.append(",");
        sb.append(trade.getPrice());
        sb.append(",");
        sb.append(trade.getQty());
        return sb.toString();
    }

}
