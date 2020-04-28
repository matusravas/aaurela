package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import database.objects.db.entity.Sector;
import io.reactivex.Maybe;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity sector.
 */
@Dao
public interface SectorDao {
    /**
     * Vlozenie vsetkych zaznamov do tabulky sector.
     *
     * @param sectors zoznam objektov {@link Sector} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllSectors(List<Sector> sectors);

    /**
     * Vyber zoznamu ID sektorov pre dostupne areii pre pouzivatela.
     * @param areaIDs zoznam ID areii
     * @return zoznam ID sektorov vo forme Listu
     */
    @Query("Select sector.id from sector where sector.area_id in (:areaIDs) order by id")
    Maybe<List<Integer>> getAllSectorsInAreas(List<Integer> areaIDs);

}
