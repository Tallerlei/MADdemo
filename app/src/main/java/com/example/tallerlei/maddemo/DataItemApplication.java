package com.example.tallerlei.maddemo;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tallerlei.maddemo.model.DataItem;
import com.example.tallerlei.maddemo.model.IDataItemCRUDOperations;
import com.example.tallerlei.maddemo.model.IDataItemCRUDOperationsAsync;
import com.example.tallerlei.maddemo.model.LocalDataItemCRUDOperationsImpl;
import com.example.tallerlei.maddemo.model.RemoteDataItemCRUDOperationsImpl;

import java.util.List;

/**
 * Created by Matthias Tallarek on 03.07.2017.
 */

public class DataItemApplication extends Application implements IDataItemCRUDOperationsAsync {

    private static String logger = DataItemApplication.class.getSimpleName();

    private IDataItemCRUDOperations syncCrudOperations;

    @Override
    public void onCreate() {
        Log.i(logger, "OnCreate()");
        syncCrudOperations = new RemoteDataItemCRUDOperationsImpl(); /* LocalDataItemCRUDOperationsImpl(this);*/
    }

    IDataItemCRUDOperationsAsync getCRUDOperationsImpl() {
        return this;
    }

    @Override
    public void createDataItem(DataItem item, final CallbackFunction<DataItem> callback) {
        new AsyncTask<DataItem, Void, DataItem>() {
            @Override
            protected DataItem doInBackground(DataItem... params) {
                return syncCrudOperations.createDataItem(params[0]);
            }

            @Override
            protected void onPostExecute(DataItem dataItem) {
                callback.process(dataItem);
            }
        }.execute(item);
    }

    @Override
    public void readAllDataItems(final CallbackFunction<List<DataItem>> callback) {
        new AsyncTask<Void, Void, List<DataItem>>() {
            @Override
            protected List<DataItem> doInBackground(Void... params) {
                return syncCrudOperations.readAllDataItems();
            }

            @Override
            protected void onPostExecute(List<DataItem> dataItems) {
                callback.process(dataItems);
            }
        }.execute();
    }

    @Override
    public void readDataItem(long id, CallbackFunction<DataItem> callback) {

    }

    @Override
    public void updateDataItem(long id, DataItem item, CallbackFunction<DataItem> callback) {

    }

    @Override
    public void deleteDataItem(long id, final CallbackFunction<Boolean> callback) {
        new AsyncTask<Long, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Long... params) {
                return syncCrudOperations.deleteDataItem(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                callback.process(aBoolean);
            }
        }.execute(id);
    }
}
