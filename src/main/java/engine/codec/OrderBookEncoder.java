package engine.codec;

import engine.domain.OrderBook;
import engine.domain.OrderBookEntry;
import engine.domain.OrderBookEntryIterator;

public class OrderBookEncoder {

    public static final String HEADER =
                            """
                            +-----------------------------------------------------------------+
                            | BUY                            | SELL                           |
                            | Id       | Volume      | Price | Price | Volume      | Id       |
                            +----------+-------------+-------+-------+-------------+----------+
                            """;
    public static final String FOOTER = "+-----------------------------------------------------------------+";
    public static final String EMPTY_BUY_ENTRY =  "|          |             |       ";
    public static final String EMPTY_SELL_ENTRY = "|       |             |          |";

    private OrderBookEncoder(){}

    public static String encode(OrderBook orderBook) {
        return encode(orderBook.getAllBidEntries(), orderBook.getAllAskEntries());
    }

    public static String encode(OrderBookEntryIterator buyIterator, OrderBookEntryIterator sellIterator) {
        StringBuilder buffer = new StringBuilder();
        encodeHeader(buffer);
        while (hasRows(buyIterator, sellIterator)) {
            encodeRow(buffer, buyIterator, sellIterator);
            buffer.append('\n');
        }
        encodeFooter(buffer);
        buffer.append('\n');
        return buffer.toString();
    }

    private static void encodeRow(StringBuilder buffer, OrderBookEntryIterator buyIterator, OrderBookEntryIterator sellIterator) {
        encodeBuyOrderBookEntry(buyIterator, buffer);
        encodeSellOrderBookEntry(sellIterator, buffer);
    }

    private static void encodeSellOrderBookEntry(OrderBookEntryIterator sellIterator, StringBuilder buffer) {
        if (sellIterator.hasNext()) {
            final OrderBookEntry sellEntry = sellIterator.next();
            buffer.append("|");
            encodePrice(buffer, sellEntry);
            buffer.append("|");
            encodeVolume(buffer, sellEntry);
            buffer.append("|");
            encodeId(buffer, sellEntry);
            buffer.append("|");
        }
        else {
            buffer.append(EMPTY_SELL_ENTRY);
        }
    }

    private static void encodeBuyOrderBookEntry(OrderBookEntryIterator buyIterator, StringBuilder buffer) {
        if (buyIterator.hasNext()) {
            final OrderBookEntry buyEntry = buyIterator.next();
            buffer.append("|");
            encodeId(buffer, buyEntry);
            buffer.append("|");
            encodeVolume(buffer, buyEntry);
            buffer.append("|");
            encodePrice(buffer, buyEntry);
        }
        else {
            buffer.append(EMPTY_BUY_ENTRY);
        }
    }

    private static void encodePrice(StringBuilder buffer, OrderBookEntry sellEntry) {
        buffer.append(padLeft(commas(sellEntry.getPrice()), 7));
    }

    private static void encodeVolume(StringBuilder buffer, OrderBookEntry sellEntry) {
        buffer.append(padLeft(commas(sellEntry.getQty()), 13));
    }

    private static void encodeId(StringBuilder buffer, OrderBookEntry sellEntry) {
        buffer.append(padLeft(Integer.toString(sellEntry.getUid()), 10));
    }

    private static boolean hasRows(OrderBookEntryIterator buyIterator, OrderBookEntryIterator sellIterator) {
        return buyIterator.hasNext() || sellIterator.hasNext();
    }

    private static void reset(StringBuilder buffer) {
        buffer.setLength(0);
    }

    private static void encodeHeader(StringBuilder buffer) {
        buffer.append(HEADER);
    }

    private static void encodeFooter(StringBuilder buffer) {
        buffer.append(FOOTER);
    }

    private static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    private static String commas(int n) {
        return String.format("%,d", n);
    }
}
