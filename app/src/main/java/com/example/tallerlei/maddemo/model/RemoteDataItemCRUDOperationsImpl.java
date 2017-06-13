package com.example.tallerlei.maddemo.model;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Matthias on 13.06.2017.
 */

public class RemoteDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    public interface IDataItemCRUDWepAPI {

        @POST("/api/todos")
        public Call<DataItem> createDataItem(@Body DataItem item);

        @GET("/api/todos")
        public Call <List<DataItem>> readAllDataItems();

        @GET("/api/todos/{id}")
        public Call <DataItem> readDataItem(@Path("id") long id);

        @PUT("/api/todos/{id}")
        public Call<DataItem> updateDataItem(@Path("id") long id, @Body DataItem item);

        @DELETE("/api/todos/{id}")
        public Call<Boolean> deleteDataItem(@Path("id") long id);

    }

    private IDataItemCRUDWepAPI webAPI;

    public RemoteDataItemCRUDOperationsImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                // android studio ip for localhost 10.0.2.2 port as usual 8080
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.webAPI = retrofit.create(IDataItemCRUDWepAPI.class);
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        try {
            DataItem created = this.webAPI.createDataItem(item).execute().body();
            return created;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DataItem> readAllDataItems() {
        try {
            return this.webAPI.readAllDataItems().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
        try {
            return this.webAPI.deleteDataItem(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }
}
