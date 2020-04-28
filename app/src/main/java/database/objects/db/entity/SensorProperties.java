package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy sensor_properties.
 */
@Entity(tableName = "sensor_properties")
public class SensorProperties {

    @PrimaryKey
    private int id;
    @ColumnInfo(name = "sensor_id")
    private int sensorID;

    @ColumnInfo(name = "sensor_order")
    private int sensorOrder;

    @ColumnInfo(name = "level_lo")
    private double levelLowest;
    @ColumnInfo(name = "level_hi")
    private double levelHighest;

    public void setId(int id) {
        this.id = id;
    }

    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }

    public void setSensorOrder(int sensorOrder) {
        this.sensorOrder = sensorOrder;
    }

    public void setLevelLowest(double levelLowest) {
        this.levelLowest = levelLowest;
    }

    public void setLevelHighest(double levelHighest) {
        this.levelHighest = levelHighest;
    }

    public int getId() {
        return id;
    }

    public int getSensorID() {
        return sensorID;
    }

    public int getSensorOrder() {
        return sensorOrder;
    }

    public double getLevelLowest() {
        return levelLowest;
    }

    public double getLevelHighest() {
        return levelHighest;
    }


}
