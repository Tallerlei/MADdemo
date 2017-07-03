package com.example.tallerlei.maddemo.model;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Matthias Tallarek on 03.07.2017.
 */

public interface IDataItemCRUDOperationsAsync {

    public static interface CallbackFunction<T> {

        public void process(T result);
    }

    // C create
    public void createDataItem(DataItem item, CallbackFunction<DataItem> callback);

    // R read all items
    public void readAllDataItems(CallbackFunction<List<DataItem>> callback);

    // R read item with id
    public void readDataItem(long id, CallbackFunction<DataItem> callback);

    // U update item with id
    public void updateDataItem(long id, DataItem item, CallbackFunction<DataItem> callback);

    // D delte item with id
    public void deleteDataItem(long id, CallbackFunction<Boolean> callback);

}
