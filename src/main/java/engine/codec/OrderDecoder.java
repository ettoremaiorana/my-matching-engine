package engine.codec;

import engine.domain.Order;
import engine.domain.OrderFactory;

public class OrderDecoder {

    public static Order decode(String buffer) {
        var tokens = buffer.split(",");
        char orderSide = tokens[0].charAt(0);
        int uid = Integer.parseInt(tokens[1]);
        short price = Short.parseShort(tokens[2]);
        int qty = Integer.parseInt(tokens[3]);
        if (tokens.length == 4) {
            //B,100322,5103,7500
            return OrderFactory.makeLimitOrder(orderSide, uid, price, qty);
        }
        else { //if (tokens.length == 5) {
            //S,100345,5103,100000,10000
            int peak = Integer.parseInt(tokens[4]);
            return OrderFactory.makeIcebergOrder(orderSide, uid, price, qty, peak);
        }
    }
}
