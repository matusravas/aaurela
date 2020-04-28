package database.objects.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import database.objects.db.entity.User;
import io.reactivex.Maybe;

/**
 * Rozhranie obsahuje Dao metody sluziace pre vkladanie/vyber udajov z/do entity user.
 */
@Dao
public interface UserDao {
    /**
     * Vlozenie vsetkych zaznamov do tabulky user.
     *
     * @param users zoznam objektov {@link User} vo forme Listu
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllUsers(List<User> users);

    @Query("Delete from users")
    void deleteAllUsers();

    /**
     * Vyber z DB zoznamu vsetkych dostupnych pouzivatelov.
     *
     * @return zoznam vsetkych dostupnych pouzivatelov
     */
    @Query("Select * from users")
    Maybe<List<User>> getAllUsers();
}
