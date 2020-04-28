package database.objects.db.pojo;

/**
 * Pomocna trieda, ktora sluzi k priradeniu nameranej hodnoty a jej datumu merania, ku konkretnemu senzoru.
 */
public class ValuesAndDates {
    private double value;
    private int sensorID;
    private String date;

    /**
     * Vracia nameranu hodnotu.
     *
     * @return namerana hodnota
     */
    public double getValue() {
        return value;
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
     * Vracia datum merania.
     *
     * @return datum merania
     */
    public String getDate() {
        return date;
    }

    /**
     * Nastavuje nameranu hodnotu na senzore.
     *
     * @param value namerana hodnota
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Nastavneie ID senzora.
     *
     * @param sensorID ID senzora
     */
    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }

    /**
     * Nastavuje datum merania na senzore.
     *
     * @param date datum merania
     */
    public void setDate(String date) {
        this.date = date;
    }

}
