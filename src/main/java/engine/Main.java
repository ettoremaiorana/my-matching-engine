package engine;

import engine.codec.OrderBookEncoder;
import engine.codec.OrderDecoder;
import engine.codec.TradeEncoder;
import engine.domain.Order;
import engine.domain.OrderBook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static String input;

    public static void main(String[] args) throws IOException {
	    OrderBook orderBook = new OrderBook();
	    while (hasNextOrder()) {
            Order order = OrderDecoder.decode(input);
            var trades = MatchingEngine.match(order, orderBook);
            trades.stream()
                  .map(trade -> TradeEncoder.encode(trade)) //encode
                  .forEach(System.out::println); //print out
            System.out.println(OrderBookEncoder.encode(orderBook));
        }
	    reader.close();
    }

    private static boolean hasNextOrder() throws IOException {
        return (input = reader.readLine()) != "exit";
    }
}
