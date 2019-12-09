package engine.domain;

public class OrderBookEntryBuilder {
    private final OrderBookEntry entry;

    public OrderBookEntryBuilder() {
        entry = new OrderBookEntry();
    }

    public OrderBookEntry build() {
        return entry;
    }

    public OrderBookEntryBuilder withVolume(int volume) {
        entry.setQty(volume);
        return this;
    }

    public OrderBookEntryBuilder withId(int id) {
        entry.setUid(id);
        return this;
    }

    public OrderBookEntryBuilder withPrice(short price) {
        entry.setPrice(price);
        return this;
    }
}
