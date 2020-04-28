package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import database.objects.db.entity.GroupView;
import io.reactivex.Maybe;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity group_view.
 */
@Dao
public interface GroupViewDao {

    /**
     * Vlozenie vsetkych zaznamov do tabulky group_view.
     *
     * @param groupViews zoznam objektov {@link GroupView} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllGroupViews(List<GroupView> groupViews);

    /**
     * Vybera z DB id vsetkych areii, ktore ma pravo dany pouzivatel prehliadat.
     *
     * @param userID id pouzivatela
     * @return zoznam ID areii vo forme Listu
     */
    @Query("select group_view.area_id from group_view where group_view.user_id=:userID")
    Maybe<List<Integer>> getAllAreaIDsforUser(int userID);

}
