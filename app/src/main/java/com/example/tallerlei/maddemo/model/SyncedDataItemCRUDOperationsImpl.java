package com.example.tallerlei.maddemo.model;

import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Matthias Tallarek on 04.07.2017.
 */

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    private IDataItemCRUDOperations localCRUD;
    private IDataItemCRUDOperations remoteCRUD;

    private boolean remoteAvailable = true;

    public void setRemoteAvailable(boolean remoteAvailable) {
        this.remoteAvailable = remoteAvailable;
    }


    public SyncedDataItemCRUDOperationsImpl(Context context) {
        this.localCRUD = new LocalDataItemCRUDOperationsImpl(context);
        this.remoteCRUD = new RemoteDataItemCRUDOperationsImpl();
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        item = localCRUD.createDataItem(item);
        if (remoteAvailable) {
            remoteCRUD.createDataItem(item);
        }
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        if (remoteAvailable) {
            return remoteCRUD.readAllDataItems();
        } else {
            return localCRUD.readAllDataItems();
        }
    }

    public List<DataItem> getLocalDataItems() {
        return localCRUD.readAllDataItems();
    }

    public DataItem createDataItemRemote(DataItem item) {
        return remoteCRUD.createDataItem(item);
    }

    public DataItem createDataItemLocal(DataItem item) {
        return localCRUD.createDataItem(item);
    }

    public boolean hasLocalDataItems() {
        List<DataItem> dataItems = localCRUD.readAllDataItems();
        if (dataItems == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public DataItem readDataItem(long id) {
        return null;
    }

    @Override
    public DataItem updateDataItem(long id, DataItem item) {
        item = localCRUD.updateDataItem(id, item);
        if (remoteAvailable) {
            remoteCRUD.updateDataItem(id, item);
        }
        return item;
    }

    @Override
    public boolean deleteDataItem(long id) {
        boolean deleted = localCRUD.deleteDataItem(id);
        if (deleted && remoteAvailable) {
            remoteCRUD.deleteDataItem(id);
        }
        return deleted;
    }

    @Override
    public boolean deleteAllDataItems() {
        if (remoteAvailable) {
            return remoteCRUD.deleteAllDataItems();
        } else {
            return false;
        }
    }
}
