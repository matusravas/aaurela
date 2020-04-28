package database.objects.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import database.objects.db.entity.Type;
import database.objects.db.entity.TypeGroup;
import database.objects.db.pojo.SensorType;
import io.reactivex.Maybe;
/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity type.
 */
@Dao
public interface TypeDao {
    /**
     * Vlozenie vsetkych zaznamov do tabulky type.
     *
     * @param typeList zoznam objektov {@link Type} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTypes(List<Type> typeList);

    /**
     * Vlozenie vsetkych zaznamov do tabulky type_group.
     *
     * @param typeGroupList zoznam objektov {@link TypeGroup} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTypeGroups(List<TypeGroup> typeGroupList);

    /**
     * Vyber fyzikalnej jednotky merania daneho senzoru z DB.
     * Vyberie sa merana fyz. velicina a id senzoru ku ktoremu patri
     * @param sensorIDs ID zoznam senzorov, pre ktore sa vybera z DB
     * @return zoznam objektov {@link SensorType} vo forme listu
     */
    @Query("Select type_group.unit as unit,sensor.id as sensorID from type_group,type,sensor where type.type_group_id=type_group.id" +
            " and sensor.type_id=type.id and sensor.id in (:sensorIDs)")
    Maybe<List<SensorType>> getTypesForSensors(List<Integer> sensorIDs);
}
