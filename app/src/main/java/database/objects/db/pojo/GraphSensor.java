package database.objects.db.pojo;

import android.arch.persistence.room.Ignore;

/**
 * Trieda, ktora obsahuje premenne potrebne pre zobrazenie dat v {@link project.mtf.nsoric.aaurela.GraphViewActivity}.
 */
public class GraphSensor {
    private String sensorName;
    private double levelHighest;
    private double levelLowest;
    private String uid;
    @Ignore
    private
    double value;
    @Ignore
    private
    String date;
    @Ignore
    private
    double maxValue;
    @Ignore
    private
    double minValue;
    @Ignore
    private
    double averageValue;
    @Ignore
    private
    String maxDate;
    @Ignore
    private
    String minDate;
    @Ignore
    private
    String unit;


    /**
     * Nastavuje nazov senzoru.
     *
     * @param sensorName nazov senzoru
     */
    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
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
     * Nastavuje priemernu hodnotu merania daneho senzoru.
     *
     * @param averageValue priemerna hodnota
     */
    public void setAverageValue(double averageValue) {
        this.averageValue = averageValue;
    }

    /**
     * Nastavuje datum, kedy bola namerana maximalna hodnota na senzore.
     *
     * @param maxDate datum merania
     */
    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * Nastavuje datum, kedy bola namerana minimalna hodnota na senzore.
     *
     * @param minDate datum merania
     */
    public void setMinDate(String minDate) {
        this.minDate = minDate;
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
     * Nastavuje datum merania na senzore.
     *
     * @param date datum merania
     */
    public void setDate(String date) {
        this.date = date;
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
     * Vracia nazov senzoru.
     *
     * @return nazov senzoru
     */
    public String getSensorName() {

        return sensorName;
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
     * Vracia priemernu nameranu hodnotu na senzore.
     *
     * @return priemerna hodnota
     */
    public double getAverageValue() {
        return averageValue;
    }

    /**
     * Vracia datum, kedy bola namerana max. hodnota na senzore.
     *
     * @return datum
     */
    public String getMaxDate() {
        return maxDate;
    }

    /**
     * Vracia datum, kedy bola namerana min. hodnota na senzore.
     *
     * @return datum
     */
    public String getMinDate() {
        return minDate;
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
     * Vracia nameranu hodnotu.
     *
     * @return namerana hodnota
     */
    public double getValue() {
        return value;
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
     * Vracia UID senzoru.
     *
     * @return UID senzoru
     */
    public String getUid() {
        return uid;
    }


}
