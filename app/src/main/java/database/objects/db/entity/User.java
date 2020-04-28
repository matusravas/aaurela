package database.objects.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Trieda reprezentujuca entitu databazy users.
 */
@Entity(tableName = "users")
public class User {
    @PrimaryKey
    private int id;
    private String login;
    private String password;
    private String name;
    @ColumnInfo(name = "group_id")
    private int groupID;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
