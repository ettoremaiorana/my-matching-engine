package engine.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderBookEntryIteratorTest {

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyIteratorTest() {
        var it = new OrderBookEntryIterator(new PriceLevel[]{}, 0, -1);
        assertFalse(it.hasNext());
        it.next(); //exception
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void nonEmptyOnlyLimitOrdersIterator() {
        PriceLevel[] priceLevels = newPriceLevels();
        var it = new OrderBookEntryIterator(priceLevels, 100, 102);
        assertTrue(it.hasNext());
        assertEquals(123456, it.next().getUid());
        assertTrue(it.hasNext());
        assertEquals(123457, it.next().getUid());
        assertTrue(it.hasNext());
        assertEquals(123455, it.next().getUid());
        assertFalse(it.hasNext());
        it.next(); //throws exception
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void throwingReversedIterator() {
        PriceLevel[] priceLevels = newPriceLevels();
        var it = new ReversedOrderBookEntryIterator(priceLevels, 100, 102);
        assertFalse(it.hasNext());
        it.next(); //throws exception
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void nonEmptyReversedIterator() {
        PriceLevel[] priceLevels = newPriceLevels();
        var it = new ReversedOrderBookEntryIterator(priceLevels, 102, 100);
        assertTrue(it.hasNext());
        assertEquals(123455, it.next().getUid());
        assertTrue(it.hasNext());
        assertEquals(123457, it.next().getUid());
        assertTrue(it.hasNext());
        assertEquals(123456, it.next().getUid());
        assertFalse(it.hasNext());
        it.next(); //throws exception

    }


    protected PriceLevel[] newPriceLevels() {
        var priceLevels = new PriceLevel[2];
        priceLevels[0] = new PriceLevel((short)100);
        priceLevels[0].add(new OrderBookEntryBuilder()
                .withId(123456)
                .withPrice((short) 100)
                .withVolume(1000)
                .build());
        priceLevels[1] = new PriceLevel((short)102);
        priceLevels[1].add(new OrderBookEntryBuilder()
                .withId(123455)
                .withPrice((short) 102)
                .withVolume(2000)
                .build());
        priceLevels[0].add(new OrderBookEntryBuilder()
                .withId(123457)
                .withPrice((short) 100)
                .withVolume(2000)
                .build());
        return priceLevels;
    }


}
