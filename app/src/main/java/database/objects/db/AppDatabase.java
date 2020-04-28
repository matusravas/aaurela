package database.objects.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import database.objects.db.dao.AreaDao;
import database.objects.db.dao.GroupViewDao;
import database.objects.db.dao.ImportanatViewDao;
import database.objects.db.dao.MeasurementDao;
import database.objects.db.dao.SectorDao;
import database.objects.db.dao.SensorDao;
import database.objects.db.dao.SensorDateDao;
import database.objects.db.dao.SensorPropertiesDao;
import database.objects.db.dao.SensorSectorDao;
import database.objects.db.dao.SensorValueDao;
import database.objects.db.dao.TypeDao;
import database.objects.db.dao.UserDao;
import database.objects.db.entity.Area;
import database.objects.db.entity.GroupView;
import database.objects.db.entity.ImportantView;
import database.objects.db.entity.Measurement;
import database.objects.db.entity.Sector;
import database.objects.db.entity.Sensor;
import database.objects.db.entity.SensorDate;
import database.objects.db.entity.SensorProperties;
import database.objects.db.entity.SensorSector;
import database.objects.db.entity.SensorValue;
import database.objects.db.entity.Type;
import database.objects.db.entity.TypeGroup;
import database.objects.db.entity.User;
import helpers.Constants;

/**
 * Trieda reprezentujuca lokalnu databazu.
 * <p>
 * Obsahuje entity, reprezentovane jednotlivymi triedami.
 *
 * <p>
 * {{@link Sensor}, {@link SensorSector},
 * {@link Sector}, {@link Area},{@link SensorDate}, {@link Measurement}, {@link SensorValue},
 * {@link SensorProperties}, {@link User}, {@link GroupView}, {@link ImportantView}, {@link Type},
 * {@link TypeGroup}
 * }
 */
@Database(entities = {Sensor.class, SensorSector.class,
        Sector.class, Area.class, SensorDate.class, Measurement.class, SensorValue.class,
        SensorProperties.class, User.class, GroupView.class, ImportantView.class, Type.class, TypeGroup.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static String DATABASE_NAME = "aurela.db";

    public abstract SensorDao sensorDao();

    public abstract SectorDao sectorDao();

    public abstract SensorSectorDao sensorSectorDao();

    public abstract AreaDao areaDao();

    public abstract SensorDateDao sensorDateDao();

    public abstract SensorValueDao sensorValueDao();

    public abstract MeasurementDao measurementDao();

    public abstract SensorPropertiesDao sensorPropertiesDao();

    public abstract UserDao userDao();

    public abstract GroupViewDao groupViewDao();

    public abstract ImportanatViewDao importanatViewDao();

    public abstract TypeDao typeDao();

    private static AppDatabase INSTANCE;

    /**
     * Vracia instanciu triedy {@link AppDatabase}
     *
     * @param context kontext potrebny pre vytvorenie databazy
     * @return instancia tiredy
     */
    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Vytvorenie suboru databazy a objektu tiredy AppDatabase, ktora reprezentuje samotnu databazu.
     * Nazov databazy sa vytvara podla nazvu serveru, na ktory sa pouzivatel pripaja
     *
     * @param context kontext
     * @return objekt triedy AppDatabase
     */
    private static AppDatabase buildDatabase(final Context context) {
        DATABASE_NAME = context.getSharedPreferences(Constants.SHARED_PREF_AAURELA, Context.MODE_PRIVATE).
                getString(Constants.SP_SERVER_NAME_KEY, DATABASE_NAME) + ".db";
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).
                fallbackToDestructiveMigration().build();
    }

    public void closeDatabase() {
        INSTANCE.close();
        INSTANCE = null;
    }
}
