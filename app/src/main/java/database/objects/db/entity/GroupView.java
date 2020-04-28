package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy group_view.
 */
@Entity(tableName = "group_view")
public class GroupView {

    @PrimaryKey
    private int id;
    @ColumnInfo(name = "user_id")
    private int userID;
    @ColumnInfo(name = "area_id")
    private int areaID;

    public void setId(int id) {
        this.id = id;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }

    public int getId() {
        return id;
    }

    public int getUserID() {
        return userID;
    }

    public int getAreaID() {
        return areaID;
    }
}
