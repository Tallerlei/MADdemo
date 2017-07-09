package com.example.tallerlei.maddemo;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tallerlei.maddemo.model.DataItem;
import com.example.tallerlei.maddemo.model.IDataItemCRUDOperations;
import com.example.tallerlei.maddemo.model.IDataItemCRUDOperationsAsync;
import com.example.tallerlei.maddemo.model.RemoteDataItemCRUDOperationsImpl;
import com.example.tallerlei.maddemo.model.SyncedDataItemCRUDOperationsImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthias Tallarek on 03.07.2017.
 */

public class DataItemApplication extends Application implements IDataItemCRUDOperationsAsync {

    private static String logger = DataItemApplication.class.getSimpleName();

    private IDataItemCRUDOperations crudOperations;

    private Map<Long, DataItem> dataItemMap = new HashMap<Long, DataItem>();

    @Override
    public void onCreate() {
        crudOperations = /*new RemoteDataItemCRUDOperationsImpl();*/ new SyncedDataItemCRUDOperationsImpl(this); /* LocalDataItemCRUDOperationsImpl(this);*/
        super.onCreate();
    }

    IDataItemCRUDOperationsAsync getCRUDOperationsImpl() {
        return this;
    }

    @Override
    public void createDataItem(DataItem item, final CallbackFunction<DataItem> callback) {
        new AsyncTask<DataItem, Void, DataItem>() {
            @Override
            protected DataItem doInBackground(DataItem... params) {
                return crudOperations.createDataItem(params[0]);
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
                return crudOperations.readAllDataItems();
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
    public void updateDataItem(long id, final DataItem item, final CallbackFunction<DataItem> callback) {
        class MyParams {
           long id;
            DataItem item;
            MyParams(Long id, DataItem item){
                this.id = id;
                this.item = item;
            }
        }
        MyParams myParams = new MyParams(id, item);

        new AsyncTask<MyParams, Void, DataItem>() {
            @Override
            protected DataItem doInBackground(MyParams... params) {

                return crudOperations.updateDataItem(params[0].id, params[0].item);
            }
            @Override
            protected void onPostExecute(DataItem item) {

                callback.process(item);
            }
        }.execute(myParams);

    }

    @Override
    public void deleteDataItem(long id, final CallbackFunction<Boolean> callback) {
        new AsyncTask<Long, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Long... params) {
                return crudOperations.deleteDataItem(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                callback.process(aBoolean);
            }
        }.execute(id);
    }

    @Override
    public void deleteAllDataItems(final CallbackFunction<Boolean> callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return crudOperations.deleteAllDataItems();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                callback.process(aBoolean);
            }
        }.execute();
    }

    public void isWebServiceAvailable(final CallbackFunction<Boolean> callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    HttpURLConnection con = (HttpURLConnection) new URL("http://10.0.2.2:8080/").openConnection();
                    con.setConnectTimeout(500);
                    if (con.getResponseCode() == 200) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (crudOperations instanceof SyncedDataItemCRUDOperationsImpl) {
                    ((SyncedDataItemCRUDOperationsImpl) crudOperations).setRemoteAvailable(aBoolean);

                }
                callback.process(aBoolean);
            }
        }.execute();
    }

    public void loginToRemoteDatabase(JSONObject credentials, final CallbackFunction<Boolean> callback) {
        new AsyncTask<JSONObject, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(JSONObject... params) {
                try {
                    HttpURLConnection httpCon = (HttpURLConnection) new URL("http://10.0.2.2:8080/api/users/auth").openConnection();
                    httpCon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    httpCon.setDoOutput(true);
                    httpCon.setRequestMethod("PUT");
                    OutputStreamWriter out = new OutputStreamWriter(
                            httpCon.getOutputStream());
                    out.write(params[0].toString());
                    out.close();
                    if (httpCon.getResponseCode() == 200) {
                        InputStream is = httpCon.getInputStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String body = rd.readLine();
                        if(Boolean.valueOf(body) == true) {
                            return true;
                        } else {
                            return false;
                        }

                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (crudOperations instanceof SyncedDataItemCRUDOperationsImpl) {
                    ((SyncedDataItemCRUDOperationsImpl) crudOperations).setRemoteAvailable(aBoolean);

                }
                callback.process(aBoolean);
            }
        }.execute(credentials);
    }

    public void addLocalItemsToRemoteDb(final CallbackFunction<Boolean> callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                List<DataItem> dataItems = ((SyncedDataItemCRUDOperationsImpl) crudOperations).getLocalDataItems();
                for (DataItem item : dataItems) {
                    ((SyncedDataItemCRUDOperationsImpl) crudOperations).createDataItemRemote(item);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                callback.process(aBoolean);
            }
        }.execute();
    }
    public void addRemoteItemsToLocalDb(final CallbackFunction<Boolean> callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                List<DataItem> dataItems = ((SyncedDataItemCRUDOperationsImpl) crudOperations).readAllDataItems();
                for (DataItem item : dataItems) {
                    ((SyncedDataItemCRUDOperationsImpl) crudOperations).createDataItemLocal(item);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                callback.process(aBoolean);
            }
        }.execute();
    }
}
