package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import database.objects.db.entity.ImportantView;
import io.reactivex.Maybe;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity group_view.
 */
@Dao
public interface ImportanatViewDao {

    /**
     * Vlozenie jedneho zaznamu do tabulky important_view.
     *
     * @param importantView objekt {@link ImportantView} (polozka) vkladana do DB
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImportantView(ImportantView importantView);

    /**
     * Vybera z DB zoznam vsetkych oblubenych senzorov pre daneho pouzivatela.
     * @param userID id pouzivatela
     * @return zoznam objektov oblubenych {@link ImportantView} vo forme Listu
     */
    @Query("Select * from important_view where important_view.user_id=:userID order by important_view.id asc")
    Maybe<List<ImportantView>> getAllImportantViewsForUser(int userID);


    /**
     * Zmazanie jedneho zaznamu z tabulky important_view.
     * @param sensorID id senzora, ktory bude odstraneny zo zoznamu dolezitych
     * @param userID id pouzivatela, pre ktoreho ma byt senozr zmazany
     */
    @Query("Delete from important_view where important_view.sensor_id=:sensorID and important_view.user_id=:userID")
    void deleteImportantView(int sensorID, int userID);

}
