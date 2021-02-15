package application;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {

    private AtomicLong value = new AtomicLong(0);

    public Counter() {}

    public Long postIncrement() {
        return value.incrementAndGet();
    }

    public Long deleteDecrement(Long decrementValue) {
        return value.addAndGet(-decrementValue);
    }

    public Long clear() {
        value.set(0);
        return value.get();
    }

    public Long getValue() {
        return value.get();
    }
}
