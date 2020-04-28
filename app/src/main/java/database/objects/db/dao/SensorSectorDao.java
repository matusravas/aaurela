package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import java.util.List;

import database.objects.db.entity.SensorSector;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity sensor_sector.
 */
@Dao
public interface SensorSectorDao {

    /**
     * Vlozenie vsetkych zaznamov do tabulky sensor_sector.
     *
     * @param sensorSectors zoznam {@link SensorSector} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllSensorSectors(List<SensorSector> sensorSectors);

}
