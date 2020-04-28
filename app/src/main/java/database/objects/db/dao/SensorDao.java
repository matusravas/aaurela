package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import database.objects.db.entity.Sensor;
import database.objects.db.pojo.DashboardSensor;
import database.objects.db.pojo.FavouriteSensor;
import database.objects.db.pojo.GraphSensor;
import io.reactivex.Maybe;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity sensor.
 */
@Dao
public interface SensorDao {
    /**
     * Vlozenie vsetkych zaznamov do tabulky sensor.
     *
     * @param sensors zoznam objektov {@link Sensor} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllSensors(List<Sensor> sensors);

    /**
     * Vyber z DB zoznamu ID senzorov na zaklade ID zoznamu sektorov.
     *
     * @param sectorIDs zoznam ID sektorov
     * @return ID zoznam senzorov vo forme Listu
     */
    @Query("Select sensor.id from sensor,sensor_sector where sensor.id=sensor_sector.sensor_id and " +
            "sensor_sector.sector_id in (:sectorIDs)")
    Maybe<List<Integer>> getSensorInSectorIDs(List<Integer> sectorIDs);

    /**
     * Vyber z DB zoznamu ID senzorov na zaklade ID pouzivatela.
     *
     * @param userID ID pouzivatela, pre ktoreho sa vybera z DB zoznam ID oblubenych senzorov
     * @return ID zoznam oblubenych senzorov vo forme Listu
     */
    @Query("select sensor.id from sensor, important_view where sensor.id=important_view.sensor_id " +
            "and important_view.user_id=:userID order by important_view.id desc")
    Maybe<List<Integer>> getFavouriteSensorIDs(int userID);


    /**
     * Vyber z DB zoznamu POJO {@link DashboardSensor}.
     * Obsahuje atributy nazov senzoru, id senzoru, min., max. hodnotu senzoru, min.,max. alarmy,
     * id sektoru, nazov sektoru, id arei, nazov arei
     *
     * @param sensorIDs zoznam ID senzorov
     * @return zoznam POJO {@link DashboardSensor} vo forme Listu
     */
    @Query("Select sensor.name as sensorName, sensor.id as sensorID, sensor.min_value as minValue, sensor.max_value as maxValue," +
            "sensor_properties.level_lo as levelLowest, sensor_properties.level_hi as levelHighest," +
            "sector.id as sectorID, sector.name as sectorName, area.name as areaName, area.id as areaID, sensor.uid as uid " +
            "from sensor,sensor_sector,sector,sensor_properties, area " +
            "where sensor.id=sensor_sector.sensor_id and area.id=sector.area_id and " +
            "sector.id=sensor_sector.sector_id and sensor.id=sensor_properties.sensor_id and sensor.id in " +
            "(:sensorIDs) order by sector.name desc")
    Maybe<List<DashboardSensor>> getAllDashboardSensors(List<Integer> sensorIDs);

    /**
     * Vyber z DB zoznamu POJO {@link FavouriteSensor}.
     * Obsahuje atributy nazov senzoru, id senzoru, min., max. hodnotu senzoru, min.,max. alarmy
     *
     * @param sensorIDs zoznam ID senzorov, ktore su dostupne pre daneho pouzivatela
     * @param userID    id pouzivatela, pre ktoreho ma byt zoznam vytiahnuty
     * @return zoznam POJO {@link FavouriteSensor} vo forme Listu
     */
    @Query("Select sensor.name as sensorName, sensor.id as sensorID, sensor.min_value as minValue, sensor.max_value as maxValue," +
            "sensor_properties.level_lo as levelLowest, sensor_properties.level_hi as levelHighest, sensor.uid as uid " +
            "from sensor,important_view,sensor_properties " +
            "where sensor.id in (:sensorIDs) and sensor.id=important_view.sensor_id " +
            "and sensor.id=sensor_properties.sensor_id  and important_view.user_id=:userID order by important_view.id desc")
    Maybe<List<FavouriteSensor>> getAllFavouriteSensors(List<Integer> sensorIDs, int userID);

    /**
     * Vyber z DB zoznamu POJO {@link GraphSensor}.
     * Obsahuje atributy nazov senzoru, id senzoru, min., max. hodnotu senzoru, min.,max. alarmy,
     * id sektoru, nazov sektoru
     *
     * @param sensorIDs zoznam ID senzorov
     * @return zoznam POJO {@link GraphSensor} vo forme Listu
     */
    @Query("Select sensor.name as sensorName, " +
            "sensor_properties.level_lo as levelLowest, sensor_properties.level_hi as levelHighest, sensor.uid as uid " +
            "from sensor,sensor_properties " +
            "where sensor.id in (:sensorIDs) and sensor.id=sensor_properties.sensor_id")
    Maybe<GraphSensor> getGraphSensor(List<Integer> sensorIDs);

}
