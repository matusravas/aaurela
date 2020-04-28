package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
/**
 * Trieda reprezentujuca entitu databazy sensor.
 */
@Entity(tableName = "sensor")
public class Sensor {
    @PrimaryKey
    private int id;
    private String name;
    @ColumnInfo(name = "uid")
    private String uid;
    @ColumnInfo(name = "min_value")
    private double minValue;
    @ColumnInfo(name = "max_value")
    private double maxValue;
    @ColumnInfo(name = "type_id")
    private int typeID;
    private int level;
    @ColumnInfo(name = "broadcast_group")
    private int broadcastGroup;
    @ColumnInfo(name = "storage_class")
    private int storageClass;


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setBroadcastGroup(int broadcastGroup) {
        this.broadcastGroup = broadcastGroup;
    }

    public void setStorageClass(int storageClass) {
        this.storageClass = storageClass;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public int getTypeID() {
        return typeID;
    }

    public int getLevel() {
        return level;
    }

    public int getBroadcastGroup() {
        return broadcastGroup;
    }

    public int getStorageClass() {
        return storageClass;
    }

}
