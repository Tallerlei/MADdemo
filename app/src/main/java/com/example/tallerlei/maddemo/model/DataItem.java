package com.example.tallerlei.maddemo.model;

import java.io.Serializable;

/**
 * Created by Matthias on 08.05.2017.
 */

public class DataItem implements Serializable{

    private String name;
    private long duedate;

    public DataItem() {
    }

    public DataItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuedate() {
        return duedate;
    }

    public void setDuedate(long duedate) {
        this.duedate = duedate;
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "name='" + name + '\'' +
                ", duedate=" + duedate +
                '}';
    }
}
