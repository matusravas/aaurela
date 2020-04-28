package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import java.util.List;

import database.objects.db.entity.SensorProperties;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity sensor_properties.
 */
@Dao
public interface SensorPropertiesDao {
    /**
     * Vlozenie vsetkych zaznamov do tabulky sensor_properties.
     *
     * @param sensorProperties zoznam {@link SensorProperties} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllSensorProperties(List<SensorProperties> sensorProperties);


}
