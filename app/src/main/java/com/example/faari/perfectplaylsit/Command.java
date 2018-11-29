package com.example.faari.perfectplaylsit;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Command {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "command")
    public String command;

    public Command(String command){
        this.command = command;
    }

    long getKey(){
        return id;
    }

    void setKey(long id){
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }
}
