package com.example.tallerlei.maddemo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthias on 06.06.2017.
 */

public class LocalDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    public static final String DATAITEMS = "DATAITEMS";
    protected static String logger = LocalDataItemCRUDOperationsImpl.class.getSimpleName();

    private SQLiteDatabase db;

    public LocalDataItemCRUDOperationsImpl(Context context) {

        db = context.openOrCreateDatabase("mydb7.sqlite", Context.MODE_PRIVATE, null);
        if (db.getVersion() == 0) {
            db.setVersion(1);
            db.execSQL("CREATE TABLE " + DATAITEMS + " (ID INTEGER PRIMARY KEY, NAME TEXT, DESCRIPTION TEXT, DUEDATE INTEGER, DONE INTEGER, FAVOURITE INTEGER)");
        }
    }

    @Override
    public DataItem createDataItem(DataItem item) {

        ContentValues values = new ContentValues();
        values.put("NAME", item.getName());
        values.put("DESCRIPTION", item.getDescription());
        values.put("DUEDATE", item.getDueDate());

        if(item.isDone() == true) {
            values.put("DONE", 1);
        } else {
            values.put("DONE", 0);
        }
        if(item.isFavourite() == true) {
            values.put("FAVOURITE", 1);
        } else {
            values.put("FAVOURITE", 0);
        }
        long id = db.insert(DATAITEMS, null, values);
        item.setId(id);

        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {

        List<DataItem> items = new ArrayList<DataItem>();

        Cursor cursor = db.query(DATAITEMS, new String[]{"ID", "NAME", "DESCRIPTION", "DUEDATE", "DONE", "FAVOURITE"}, null, null, null, null, "ID");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            boolean next = false;
            do {
                DataItem item = new DataItem();
                items.add(item);
                long id = cursor.getLong(cursor.getColumnIndex("ID"));
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                long dueDate = cursor.getLong(cursor.getColumnIndex("DUEDATE"));

                Boolean done = false;
                if(cursor.getInt(cursor.getColumnIndex("FAVOURITE")) == 1) {
                    done = true;
                }
                Boolean favourite = false;
                if(cursor.getInt(cursor.getColumnIndex("FAVOURITE")) == 1) {
                    favourite = true;
                }
                item.setId(id);
                item.setName(name);
                item.setDescription(description);
                item.setDueDate(dueDate);

                item.setDone(done);
                item.setFavourite(favourite);


                next = cursor.moveToNext();
            } while (next);

        }

        return items;
    }

    @Override
    public DataItem readDataItem(long id) {
        return null;
    }

    @Override
    public DataItem updateDataItem(long id, DataItem item) {
        ContentValues cv = new ContentValues();
        cv.put("NAME",item.getName());
        cv.put("DESCRIPTION", item.getDescription());
        cv.put("DUEDATE", item.getDueDate());
        if(item.isDone()==true) {
            cv.put("DONE", 1);
        } else {
            cv.put("DONE", 0);
        }
        if(item.isFavourite()==true) {
            cv.put("FAVOURITE", 1);
        } else {
            cv.put("FAVOURITE", 0);
        }
        db.update(DATAITEMS, cv, "ID=?", new String[]{String.valueOf(id)});
        return item;
    }

    @Override
    public boolean deleteDataItem(long id) {

        int numOfRows = db.delete(DATAITEMS, "ID=?", new String[]{String.valueOf(id)});

        return numOfRows > 0;
    }

    @Override
    public boolean deleteAllDataItems() {
        return false;
    }
}
