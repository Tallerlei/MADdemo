package com.example.tallerlei.maddemo.model;

import java.util.List;

/**
 * Created by Matthias on 23.05.2017.
 */

public interface IDataItemCRUDOperations {

    // C create
    DataItem createDataItem(DataItem item);

    // R read all items
    List<DataItem> readAllDataItems();

    // R read item with id
    DataItem readDataItem(long id);

    // U update item with id
    DataItem updateDataItem(long id, DataItem item);

    // D delete item with id
    boolean deleteDataItem(long id);

    // D delete item with id
    boolean deleteAllDataItems();

}
