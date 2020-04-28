package database.objects.db.pojo;

/**
 * Pomocna trieda pre priradenie fyzikalnej jendotky merania ku konkretnemu senzoru.
 */
public class SensorType {
    private int sensorID;
    private String unit;

    /**
     * Nastavneie ID senzora.
     *
     * @param sensorID ID senzora
     */
    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }

    /**
     * Nastavuje fyzikalnu jednotku, v ktorej senzor meria.
     *
     * @param unit fyzikalna jednotka merania
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Vracia ID senzora.
     *
     * @return ID senzora
     */
    public int getSensorID() {
        return sensorID;
    }

    /**
     * Vracia fyzikalnu jednotku merania.
     *
     * @return fyz. jednotka.
     */
    public String getUnit() {
        return unit;
    }
}
