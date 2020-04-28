package helpers;

/**
 * Fifo zasobnik, pre pocitanie trendu.
 */
public class FixedFifoQueue implements Queue<Number> {
    private Number[] ring;
    private int index;

    private int realSize;

    /**
     * Konstuktor FIFO zasobnika.
     * Najnovsia hodnota je na indexe 0, najstarsia na indexe length-1
     *
     * @param initialValuesNum velkost FIFO
     */
    public FixedFifoQueue(int initialValuesNum) {
        this.ring = new Number[initialValuesNum];
        this.realSize = 0;
        for (int i = 0; i < initialValuesNum; i++) {
            ring[i] = 0.0;
        }
        index = 0;
    }

    @Override
    public boolean add(Number newest) {
        return offer(newest);
    }

    @Override
    public Number element() {
        return ring[getHeadIndex()];
    }

    @Override
    public boolean offer(Number newest) {
        Number oldest = ring[index];
        ring[index] = newest;
        incrIndex();
        return true;
    }

    @Override
    public Number peek() {
        return ring[getHeadIndex()];
    }

    /**
     * Vracia velkost zasobnika.
     *
     * @return velkost zasobnika
     */
    public int size() {
        return this.realSize;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("[");
        for (int i = index, n = 0; n < ring.length; i = nextIndex(i), n++) {
            sb.append(ring[i]);
            if (n + 1 < ring.length) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }

    /**
     * Navysenie aktualneho indexu.
     */
    private void incrIndex() {
        realSize = (realSize > ring.length) ? ring.length : (realSize + 1);
        index = nextIndex(index);
    }

    /**
     * Vracia nasledujuci index.
     *
     * @param current aktualny index
     * @return nasledujuci index
     */
    private int nextIndex(int current) {
        if (current + 1 >= ring.length) {
            return 0;
        } else return current + 1;
    }

    private int getHeadIndex() {
        if (index == 0) {
            return ring.length - 1;
        } else return index - 1;
    }

    /**
     * Vracia hodnotu trendu.
     *
     * @return hodnota tredu
     */
    public Number getTrend() {
        Number[] data = new Number[this.size()];
        int j=0;
        for (int i = index, n = 0; n < realSize; i = nextIndex(i), n++) {
            j=i-realSize;
            if(j<0) j+=realSize;
            data[n] = this.ring[j];
        }
        return Aproximator.aproximateTrend(data);
    }
}
