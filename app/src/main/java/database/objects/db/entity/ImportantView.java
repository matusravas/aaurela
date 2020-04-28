package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy important_view.
 */
@Entity(tableName = "important_view")
public class ImportantView {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_id")
    private
    int userID;
    @ColumnInfo(name = "sensor_id")
    private
    int sensorID;

    public void setId(int id) {
        this.id = id;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }

    public int getId() {
        return id;
    }

    public int getUserID() {
        return userID;
    }

    public int getSensorID() {
        return sensorID;
    }
}
