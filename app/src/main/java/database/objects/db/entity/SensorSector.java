package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy sensor_sector.
 */
@Entity(tableName = "sensor_sector")
public class SensorSector {

    @PrimaryKey
    int id;
    @ColumnInfo(name = "sensor_id")
    private int sensorID;
    @ColumnInfo(name = "sector_id")
    private int sectorID;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }

    public void setSectorID(int sectorID) {
        this.sectorID = sectorID;
    }

    public int getSensorID() {
        return sensorID;
    }

    public int getSectorID() {
        return sectorID;
    }


}
