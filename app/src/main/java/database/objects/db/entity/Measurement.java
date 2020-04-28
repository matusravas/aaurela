package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy measurement.
 */
@Entity(tableName = "measurement")
public class Measurement {
    @PrimaryKey
    private int id;
    String name;
    @ColumnInfo(name = "meas_type")
    private int measurementType;
    private int sid;


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMeasurementType(int measurementType) {
        this.measurementType = measurementType;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getSid() {
        return sid;
    }

    public int getId() {
        return id;
    }

    public int getMeasurementType() {
        return measurementType;
    }

    public String getName() {
        return name;
    }
}
