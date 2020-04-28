package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy sensor_value.
 */
@Entity(tableName = "sensor_value")
public class SensorValue {
    @PrimaryKey
    private int id;
    private int did;
    private double value;
    @ColumnInfo(name = "measurement_id")
    private int measurementID;

    public int getMeasurementID() {
        return measurementID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setMeasurementID(int measurementID) {
        this.measurementID = measurementID;
    }

    public int getId() {
        return id;
    }

    public int getDid() {
        return did;
    }

    public double getValue() {
        return value;
    }
}
