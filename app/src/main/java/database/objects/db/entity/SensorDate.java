package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy sensor_date.
 */
@Entity(tableName = "sensor_date")
public class SensorDate {
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "meas_date")
    private String measurementDate;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setMeasurementDate(String measurementDate) {
        this.measurementDate = measurementDate;
    }

    public String getMeasurementDate() {
        return measurementDate;
    }
}
