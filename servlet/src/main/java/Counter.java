public class Counter {

    private Long value = 0L;

    public Counter() {}

    public Counter(Long initialValue) {
        value = initialValue;
    }

    public Long postIncrement() {
        value+=1;
        return value;
    }

    public Long deleteDecrement(Long decrementValue) {
        value = (value >= decrementValue)
                ? value - decrementValue
                : 0;
        return value;
    }

    public Long clear() {
        value = 0L;
        return value;
    }

    public Long getCurrentValue() {
        return value;
    }

}
