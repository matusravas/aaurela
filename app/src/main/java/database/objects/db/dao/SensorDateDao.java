package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import database.objects.db.entity.SensorDate;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity sensor_date.
 */
@Dao
public interface SensorDateDao {

    /**
     * Vlozenie vsetkych zaznamov do tabulky sensor_date.
     *
     * @param sensorDates zoznam objektov {@link SensorDate} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllSensorDates(List<SensorDate> sensorDates);

    /**
     * Zmazanie udajov z tabulky sensor_date.
     * Zmazu sa vsetky polozky v DB v tabulke sensor_date, ktore maju hodnodnotu atributu datumu nizsiu (starsiu) ako datum prijaty ako parameter
     *
     * @param date datum vo formate retazca
     */
    @Query("Delete from sensor_date where sensor_date.meas_date < date(:date)")
    void deleteOldSensorDates(String date);
}
