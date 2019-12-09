package engine;

import engine.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class MatchingEngine {

    public static List<Trade> match(Order order, OrderBook orderBook) {

        /* setup logic that depend on match side */
        OrderBookEntryIterator it;
        BiFunction<Short, Short, Boolean> priceCheck;
        boolean isBuyOrder = order.getOrderSide() == OrderSide.BUY;
        if (isBuyOrder) {
            it = orderBook.getAllAskEntries();
            priceCheck = (tradePrice, entryPrice) -> tradePrice >= entryPrice;
        }
        else {
            it = orderBook.getAllBidEntries();
            priceCheck = (tradePrice, entryPrice) -> tradePrice <= entryPrice;
        }
        /* end*/

        /*variables initialization*/
        short price = order.getPrice();
        int qty = order.getQty();
        List<Trade> tradesMatched = new ArrayList<>();
        boolean canBeMatched = true;
        /*end*/

        /*actual matching logic*/
        while (it.hasNext() && qty > 0 && canBeMatched) {
            canBeMatched = false;
            //empty side
            OrderBookEntry entry = it.next();
            short entryPrice = entry.getPrice();
            if (priceCheck.apply(price, entryPrice)) {
                canBeMatched = true;
                //match
                int orderUid = order.getUid();
                Trade trade = fillTrade(qty, isBuyOrder, orderUid, entry);
                tradesMatched.add(trade);

                qty = Math.max(0, qty - trade.getQty());
                int entryRemainingQty = entry.getQty() - trade.getQty();
                if (entryRemainingQty == 0) {
                    it.remove();
                    entry.onPop(it);
                    orderBook.onPop(isBuyOrder ? 'S' : 'B');
                }
                else {
                    entry.setQty(entryRemainingQty);
                }
            }
        }
        if (qty > 0) {
            order.setQty(qty);
            orderBook.add(order);
        }
        List<Trade> finalResult = groupTrades(tradesMatched, isBuyOrder);
        return finalResult;
    }

    private static List<Trade> groupTrades(List<Trade> tradesMatched, boolean isBuyOrder) {
        if (isBuyOrder) {
            var byId = tradesMatched.stream().collect(Collectors.groupingBy(Trade::getSellUid));
            var res = byId.entrySet().stream().map(e -> {
                int sellId = e.getKey();
                Trade firstTrade = e.getValue().iterator().next();
                Trade trade = new Trade();
                trade.setBuyUid(firstTrade.getBuyUid());
                trade.setSellUid(sellId);
                trade.setPrice(firstTrade.getPrice());
                trade.setQty(e.getValue().stream().map(Trade::getQty).reduce(Integer::sum).get());
                return trade;
            });
            return res.collect(Collectors.toList());
        }
        else {
            var byId = tradesMatched.stream().collect(Collectors.groupingBy(Trade::getBuyUid));
            var res = byId.entrySet().stream().map(e -> {
                int buyId = e.getKey();
                Trade firstTrade = e.getValue().iterator().next();
                Trade trade = new Trade();
                trade.setSellUid(firstTrade.getSellUid());
                trade.setBuyUid(buyId);
                trade.setPrice(firstTrade.getPrice());
                trade.setQty(e.getValue().stream().map(Trade::getQty).reduce(Integer::sum).get());
                return trade;
            });
            return res.collect(Collectors.toList());
        }
    }

    private static Trade fillTrade(int remainingQty, boolean isBuyOrder, int orderUid, OrderBookEntry entry) {
        Trade trade = new Trade();
        trade.setBuyUid(isBuyOrder ? orderUid : entry.getUid());
        trade.setSellUid(isBuyOrder ? entry.getUid() : orderUid);
        trade.setPrice(entry.getPrice());
        trade.setQty(Math.min(remainingQty, entry.getQty()));
        return trade;
    }
}
