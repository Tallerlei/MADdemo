package com.example.tallerlei.maddemo.model;

import java.util.List;
import java.util.Arrays;

/**
 * Created by Matthias on 23.05.2017.
 */

public class SimpleDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    @Override
    public DataItem createDataItem(DataItem item) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        return Arrays.asList(new DataItem[]{new DataItem("shit"), new DataItem("lalala"), new DataItem("nuklear"), new DataItem("blödmann"), new DataItem("hänker"), new DataItem("noche ein eintrag"), new DataItem("lorem"), new DataItem("ipsumt"), new DataItem("spasit"), new DataItem("vogel"), new DataItem("awesome")});
    }

    @Override
    public DataItem readDataItem(long id) {
        return null;
    }

    @Override
    public DataItem updateDataItem(long id, DataItem item) {
        return null;
    }

    @Override
    public boolean deleteDataItem(long id) {
        return false;
    }
}
