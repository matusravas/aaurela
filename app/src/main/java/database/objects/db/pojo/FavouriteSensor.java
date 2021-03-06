package database.objects.db.pojo;

import android.arch.persistence.room.Ignore;

/**
 * Trieda, ktora obsahuje premenne potrebne pre zobrazenie dat v {@link project.mtf.nsoric.aaurela.FavouritesActivity}.
 */
public class FavouriteSensor {
    private String sensorName;
    private int sensorID;
    private double maxValue;
    private double minValue;
    private double levelHighest;
    private double levelLowest;
    private String uid;
    @Ignore
    private
    String unit;
    @Ignore
    private
    double value;
    @Ignore
    private
    double avgValue;
    @Ignore
    private
    String date;
    @Ignore
    private
    String avgText;

    @Ignore
    private int trend;
    @Ignore
    private double trendValue;


    /**
     * Nastavuje nazov senzoru.
     *
     * @param sensorName nazov senzoru
     */
    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    /**
     * Nastavuje ID senzoru.
     *
     * @param sensorID ID senzoru
     */
    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }

    /**
     * Nastavuje maximalnu hodnotu merania daneho senzoru.
     *
     * @param maxValue max. hodnota
     */
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Nastavuje minimalnu hodnotu merania daneho senzoru.
     *
     * @param minValue min. hodnota
     */
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    /**
     * Nastavuje maximalnu pripustnu hranicu merania daneho senzoru.
     *
     * @param levelHighest max. hranica
     */
    public void setLevelHighest(double levelHighest) {
        this.levelHighest = levelHighest;
    }

    /**
     * Nastavuje minimalnu pripustnu hranicu merania daneho senzoru.
     *
     * @param levelLowest min. hranica
     */
    public void setLevelLowest(double levelLowest) {
        this.levelLowest = levelLowest;
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
     * Nastavuje priemernu hodnotu.
     *
     * @param avgValue priemerna hodnota
     */
    public void setAvgValue(double avgValue) {
        this.avgValue = avgValue;
    }

    /**
     * Nastavuje datum merania na senzore.
     *
     * @param date datum merania
     */
    public void setDate(String date) {
        this.date = date;
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
     * Nastavuje UID senzoru.
     *
     * @param uid UID senzoru
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Nastavuje text zobrazeny pri priemernej hodnote
     *
     * @param avgText text
     */
    public void setAvgText(String avgText) {
        this.avgText = avgText;
    }

    /**
     * Nastavenie hodnoty typu trendu a jeho hodnoty.
     * * Typ nadobuda hodnoty:
     * <p>
     * -1=> ak senzor nemeral (nema ziadny trend)
     * <p>
     * 0=> ak je trend mensi ako hodnota 0.15
     * <p>
     * 1=> ak je trend konstantny
     * <p>
     * 2=> ak je trend vacsi ako 0.15 (rastie)
     *
     * @param trend typ trendu
     * @param value hodnota trendu
     */
    public void setTrend(int trend, double value) {
        this.trend = trend;
        this.trendValue = value;
    }

    /**
     * Vracia nazov senzoru.
     *
     * @return nazov senzoru
     */
    public String getSensorName() {
        return sensorName;
    }

    /**
     * Vracia ID senzoru.
     *
     * @return ID senzoru
     */
    public int getSensorID() {
        return sensorID;
    }

    /**
     * Vracia max. hodnotu, ktoru mozno na senzore namerat.
     *
     * @return max. hodnota
     */
    public double getMaxValue() {
        return maxValue;
    }

    /**
     * Vracia min. hodnotu, ktoru mozno na senzore namerat.
     *
     * @return min. hodnota
     */
    public double getMinValue() {
        return minValue;
    }

    /**
     * Vracia hodnotu max. alarmu pre senzor.
     *
     * @return max. alarm
     */
    public double getLevelHighest() {
        return levelHighest;
    }

    /**
     * Vracia hodnotu min. alarmu pre senzor.
     *
     * @return min. alarm
     */
    public double getLevelLowest() {
        return levelLowest;
    }

    /**
     * Vracia nameranu hodnotu.
     *
     * @return namerana hodnota
     */
    public double getValue() {
        return value;
    }

    /**
     * Vracia priemernu hodnotu
     *
     * @return
     */
    public double getAvgValue() {
        return avgValue;
    }

    /**
     * Vracia datum merania
     *
     * @return datum
     */
    public String getDate() {
        return date;
    }

    /**
     * Vracia fyzikalnu jednotku merania.
     *
     * @return jednotka merania
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Vracia UID senzoru.
     *
     * @return UID senzoru
     */
    public String getUid() {
        return uid;
    }

    /**
     * Vracia text zobrazeny pri priemernej meranej hodnote.
     *
     * @return text
     */
    public String getAvgText() {
        return avgText;
    }

    /**
     * Vracia typ trendu.
     * * Typ nadobuda hodnoty:
     * <p>
     * -1=> ak senzor nemeral (nema ziadny trend)
     * <p>
     * 0=> ak je trend mensi ako hodnota 0.15
     * <p>
     * 1=> ak je trend konstantny
     * <p>
     * 2=> ak je trend vacsi ako 0.15 (rastie)
     * @return trend typ trendu
     */
    public int getTrend() {
        return trend;
    }

    /**
     * Vracia hodnotu trendu.
     * @return hodnota trendu
     */
    public double getTrendValue() {return trendValue; }
}
