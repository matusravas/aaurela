package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import database.objects.db.entity.SensorValue;
import database.objects.db.pojo.ValuesAndDates;
import io.reactivex.Maybe;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity sensor_value.
 */
@Dao
public interface SensorValueDao {

    /**
     * Vlozenie vsetkych zaznamov do tabulky sensor_value.
     *
     * @param sensorValues zoznam objektov {@link SensorValue} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllSensorValues(List<SensorValue> sensorValues);

    /**
     * Zmazanie udajov z tabulky sensor_value.
     * Zmazu sa vsetky polozky v DB v tabulke sensor_value, ktore maju hodnodnotu atributu datum nizsiu (starsiu) ako datum prijaty ako parameter
     *
     * @param date datum vo formate retazca
     */
    @Query("Delete from sensor_value where sensor_value.did in(Select sensor_date.id from sensor_date where sensor_date.meas_date" +
            " < date (:date))")
    void deleteOldValues(String date);

    /**
     * Vyber z DB vsetkych meranych hodnot.
     * <p>
     * Vyber sa uskutocnuje pre vsetky hodnoty, ktore splnaju podmienky a patria do zoznamu merani, poslany ako parameter
     * a su novsie ako datum poslany ako parameter.
     * </>
     *
     * @param measIDs zoznam ID merani
     * @param date    stary datum od ktoreho musia byt vyhladavane hodnoty novsie
     * @return zoznam objektov {@link SensorValue} vo forme Listu
     */
    @Query("select sensor_value.value as value, sensor_date.meas_date as date, measurement.sid as sensorID" +
            " from sensor_value,measurement,sensor_date where sensor_value.measurement_id" +
            " in (:measIDs) and sensor_date.id=sensor_value.did and measurement.id=sensor_value.measurement_id" +
            " and sensor_date.meas_date> date (:date) order by measurement.sid and sensor_date.id asc")
    Maybe<List<ValuesAndDates>> getAllValuesAndDates(List<Integer> measIDs, String date);

}
