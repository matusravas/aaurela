package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy type.
 */
@Entity(tableName = "type")
public class Type {
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "type_group_id")
    private int typeGroupID;
    private String text;

    public int getId() {
        return id;
    }

    public int getTypeGroupID() {
        return typeGroupID;
    }

    public String getText() {
        return text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTypeGroupID(int typeGroupID) {
        this.typeGroupID = typeGroupID;
    }

    public void setText(String text) {
        this.text = text;
    }
}
