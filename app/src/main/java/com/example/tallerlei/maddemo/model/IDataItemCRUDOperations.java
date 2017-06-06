package com.example.tallerlei.maddemo.model;

import java.util.List;

/**
 * Created by Matthias on 23.05.2017.
 */

public interface IDataItemCRUDOperations {

    // C create
    public DataItem createDataItem(DataItem item);

    // R read all items
    public List<DataItem> readAllDataItems();

    // R read item with id
    public DataItem readDataItem(long id);

    // U update item with id
    public DataItem updateDataItem(long id, DataItem item);

    // D delte item with id
    public boolean deleteDataItem(long id);

}
