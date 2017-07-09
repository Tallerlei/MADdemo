package com.example.tallerlei.maddemo.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Matthias on 08.05.2017.
 */

public class DataItem implements Serializable {

    private String name;
    private String description;
    @SerializedName("expiry")
    private long dueDate;
    private long id;
    private boolean done = false;
    private boolean favourite = false;

    public DataItem() {
    }

    public DataItem(String name, String description, long dueDate, boolean done, boolean favourite) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.done = done;
        this.favourite = favourite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "name='" + name + '\'' +
                ", description=" + description +
                ", duedate=" + dueDate +
                ", id=" + id +
                ", done=" + done +
                ", favourite=" + favourite +
                "}";
    }
}
