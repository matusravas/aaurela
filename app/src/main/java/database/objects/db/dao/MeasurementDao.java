package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import database.objects.db.entity.Measurement;
import io.reactivex.Maybe;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity measurement.
 */
@Dao
public interface MeasurementDao {

    /**
     * Vlozenie vsetkych zaznamov do tabulky measurements.
     *
     * @param measurements zoznam objektov {@link Measurement} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllMeasurements(List<Measurement> measurements);

    /**
     * Vyber ID vsetkych zaznamov z tabulky measurements.
     *
     * @param sensorIDs zoznam ID senzorov, na zaklade, ktorych sa vyberie zoznam ID mearani
     * @return zoznam ID merani vo formate Listu
     */
    @Query("Select measurement.id from measurement where measurement.sid in(:sensorIDs)")
    Maybe<List<Integer>> getMeasurementIDs(List<Integer> sensorIDs);

}
