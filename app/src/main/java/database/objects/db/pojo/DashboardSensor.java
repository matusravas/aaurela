package database.objects.db.pojo;

import android.arch.persistence.room.Ignore;

/**
 * Trieda, ktora obsahuje premenne potrebne pre zobrazenie dat v {@link project.mtf.nsoric.aaurela.DashboardActivity}.
 */
public class DashboardSensor {
    private String sensorName;
    private int sensorID;
    private int sectorID;
    private int areaID;
    private String uid;
    private double maxValue;
    private double minValue;
    private double levelHighest;
    private double levelLowest;
    private String sectorName;
    private String areaName;

    @Ignore
    private int trend;
    @Ignore
    private double trendValue;

    @Ignore
    private
    int typeForAdapter;

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
     * Nastavuje ID sektoru.
     *
     * @param sectorID ID sektoru
     */
    public void setSectorID(int sectorID) {
        this.sectorID = sectorID;
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
     * Nastavuje nazov sektoru.
     *
     * @param sectorName nazov sektoru
     */
    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
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
     * Nastavuje nameranu hodnotu na senzore.
     *
     * @param value namerana hodnota
     */
    public void setValue(double value) {
        this.value = value;
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
     * Nastavuje ID oblasti.
     *
     * @param areaID ID oblasti
     */
    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }

    /**
     * Nastavuje nazov oblasti.
     *
     * @param areaName nazov oblasti
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * Nastavuje typ, podla ktoreho sa vykresli prislusna karticka v {@link adapters.DashboardAdapter}.
     * <p>
     * Typ nadobuda hodnoty:
     * <p>
     * -1=> oblast
     * <p>
     * 0=> sektor
     * <p>
     * 1=> senzor
     *
     * @param typeForAdapter typ
     */
    public void setTypeForAdapter(int typeForAdapter) {
        this.typeForAdapter = typeForAdapter;
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
     * Nastavuje UID senzoru.
     *
     * @param uid UID senzoru
     */
    public void setUid(String uid) {
        this.uid = uid;
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
     * Vracia ID sektoru.
     *
     * @return ID sektoru
     */
    public int getSectorID() {
        return sectorID;
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
     * Vracia datum merania
     *
     * @return datum
     */
    public String getDate() {
        return date;
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
     * @return priemerna hodnota
     */
    public double getAvgValue() {
        return avgValue;
    }

    /**
     * Vracia nazov sektoru.
     *
     * @return nazov sektoru
     */
    public String getSectorName() {
        return sectorName;
    }

    /**
     * Vracia ID oblasti.
     *
     * @return ID oblasti
     */
    public int getAreaID() {
        return areaID;
    }

    /**
     * Vracia nazov oblasti.
     *
     * @return nazov oblasti
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * Vracia typ pre vykreslenie prislusnej karticky.
     * Vracia:
     * * <p>
     * * -1=> oblast
     * * <p>
     * * 0=> sektor
     * * <p>
     * * 1=> senzor
     *
     * @return typ
     */
    public int getTypeForAdapter() {
        return typeForAdapter;
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
     * Typ nadobuda hodnoty:
     * <p>
     * -1=> ak senzor nemeral (nema ziadny trend)
     * <p>
     * 0=> ak je trend mensi ako hodnota 0.15
     * <p>
     * 1=> ak je trend konstantny
     * <p>
     * 2=> ak je trend vacsi ako 0.15 (rastie)
     *
     * @return trend typ trendu
     */
    public int getTrend() {
        return trend;
    }

    /**
     * Vracia hodnotu trendu.
     *
     * @return hodnota trendu
     */
    public double getTrendValue() {
        return trendValue;
    }
}
