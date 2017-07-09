package com.example.tallerlei.maddemo.model;

import java.util.List;

/**
 * Created by Matthias Tallarek on 03.07.2017.
 */

public interface IDataItemCRUDOperationsAsync {

    interface CallbackFunction<T> {

        void process(T result);
    }

    // C create
    void createDataItem(DataItem item, CallbackFunction<DataItem> callback);

    // R read all items
    void readAllDataItems(CallbackFunction<List<DataItem>> callback);

    // R read item with id
    void readDataItem(long id, CallbackFunction<DataItem> callback);

    // U update item with id
    void updateDataItem(long id, DataItem item, CallbackFunction<DataItem> callback);

    // D delete item with id
    void deleteDataItem(long id, CallbackFunction<Boolean> callback);

    // D delete item with id
    void deleteAllDataItems(CallbackFunction<Boolean> callback);

}
