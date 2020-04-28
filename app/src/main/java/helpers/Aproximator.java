package helpers;

/**
 * Trieda pre aproximovanie nameranych hodnot a vypocet trendu.
 */
class Aproximator {

    /**
     * Vypocet hodnoty trendu.
     *
     * @param array hodnoty z FIFO zasobnika {@link FixedFifoQueue}
     * @return hodnota trendu
     */
    static double aproximateTrend(Number[] array) {
        int size = array.length;
        Double sx, sy, sxx, sxy;
        sx = sxx = sxy = sy = 0.0;
        Double dx = 0.0;
        for (int i = 0; i < size; i++, dx += 1 / 6.0) {
            sx += dx;
            sy += (Double) array[i];
            sxx += dx * dx;
            sxy += dx * (Double) array[i];
        }
        return (size * sxy - sx * sy) / (size * sxx - sx * sx);

    }
}
