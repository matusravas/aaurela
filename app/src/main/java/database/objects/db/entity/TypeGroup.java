package database.objects.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy type.
 */
@Entity(tableName = "type_group")
public class TypeGroup {
    @PrimaryKey
    private int id;
    private String text;
    private String unit;
    private String unitSI;

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setUnitSI(String unitSI) {
        this.unitSI = unitSI;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getUnit() {
        return unit;
    }

    public String getUnitSI() {
        return unitSI;
    }
}
