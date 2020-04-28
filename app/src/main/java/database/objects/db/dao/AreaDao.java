package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import java.util.List;

import database.objects.db.entity.Area;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity area.
 */
@Dao
public interface AreaDao {

    /**
     * Vlozenie vsetkych zaznamov do tabulky area.
     *
     * @param areas zoznam objektov {@link Area} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllAreas(List<Area> areas);


}
