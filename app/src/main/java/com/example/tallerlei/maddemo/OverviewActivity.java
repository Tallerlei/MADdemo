package com.example.tallerlei.maddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tallerlei.maddemo.model.DataItem;
import com.example.tallerlei.maddemo.model.IDataItemCRUDOperations;
import com.example.tallerlei.maddemo.model.LocalDataItemCRUDOperations;
import com.example.tallerlei.maddemo.model.SimpleDataItemCRUDOperationsImpl;

import java.util.Arrays;
import java.util.List;

import static com.example.tallerlei.maddemo.DetailviewActivity.DATA_ITEM;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener {

    protected static String logger = OverviewActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    private TextView helloText;
    private ViewGroup listView;
    private FloatingActionButton addItemButton;
    private ArrayAdapter<DataItem> listViewAdapter;

//    private List<DataItem> items = Arrays.asList(new DataItem[]{new DataItem("shit"), new DataItem("lalala"), new DataItem("nuklear"), new DataItem("blödmann"), new DataItem("hänker"), new DataItem("noche ein eintrag"), new DataItem("lorem"), new DataItem("ipsumt"), new DataItem("spasit"), new DataItem("vogel"), new DataItem("awesome")});

    private IDataItemCRUDOperations crudOperations;

    private class ItemViewHolder {

        public TextView itemNameView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. select the view to be controlled
        setContentView(R.layout.activity_overview);

        // 2. read out elements from the view
        helloText = (TextView) findViewById(R.id.helloText);
        Log.i(logger, "helloText: " + helloText);
        listView = (ViewGroup) findViewById(R.id.ListView);
        Log.i(logger, "listView: " + listView);
        addItemButton = (FloatingActionButton) findViewById(R.id.addItemButton);

        progressDialog = new ProgressDialog(this);

        // 3. set content on the elements
        setTitle(R.string.title_overview);
        helloText.setText(R.string.hello_text);

        // 4. set listeners to allow user interaction
        helloText.setOnClickListener(this);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });

        // instantiate listview with adapter
        listViewAdapter = new ArrayAdapter<DataItem>(this, R.layout.itemview_overview) {
            @NonNull
            @Override
            public View getView(int position, View itemView, ViewGroup parent) {

                if (itemView != null) {
                    Log.i(logger, "reusing existing itemView for element at position: " + position);
                } else {
                    Log.i(logger, "creating new itemView for element at position: " + position);
                    // create a new instance of list item view
                    itemView = getLayoutInflater().inflate(R.layout.itemview_overview, null);
                    // read out the text view for item name
                    TextView itemNameView = (TextView) itemView.findViewById(R.id.itemName);
                    // create a new instance of the view holder
                    ItemViewHolder itemViewHolder = new ItemViewHolder();
                    // set the itemNameView attribute on view holder to text view
                    itemViewHolder.itemNameView = itemNameView;
                    // set the view holder on the list item view
                    itemView.setTag(itemViewHolder);
                }

                ItemViewHolder viewHolder = (ItemViewHolder) itemView.getTag();

                DataItem item = getItem(position);
//                Log.i(logger, "creating view for position " + position + " and item: " + item);

                viewHolder.itemNameView.setText(item.getName());

                return itemView;
            }
        };
        ((ListView) listView).setAdapter(listViewAdapter);
        listViewAdapter.setNotifyOnChange(true);

        ((ListView) listView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataItem selectedItem = listViewAdapter.getItem(position);
                showDetailviewForItem(selectedItem);
            }
        });

        crudOperations = new /*SimpleDataItemCRUDOperationsImpl();*/ LocalDataItemCRUDOperations(this);

        readItemsAndFillListView();
    }

    private void readItemsAndFillListView() {

//        List<DataItem> items = crudOperations.readAllDataItem();
//
//        for (DataItem item : items) {
//            addItemToListView(item);
//        }

        new AsyncTask<Void, Void, List<DataItem>>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected List<DataItem> doInBackground(Void... params) {
                return crudOperations.readAllDataItems();
            }

            @Override
            protected void onPostExecute(List<DataItem> dataItems) {
                progressDialog.hide();
                for (DataItem item : dataItems) {
                    addItemToListView(item);
                }
            }
        }.execute();
    }

    public void createAndShowItem(/*final*/ DataItem item) {

//        progressDialog.show();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                DataItem createdItem = crudOperations.createDataItem(item);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        addItemToListView(item);
//                        progressDialog.hide();
//                    }
//                });
//            }
//        }).start();

        new AsyncTask<DataItem, Void, DataItem>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected DataItem doInBackground(DataItem... params) {
                DataItem createdItem = crudOperations.createDataItem(params[0]);
                return createdItem;
            }

            @Override
            protected void onPostExecute(DataItem dataItem) {
                addItemToListView(dataItem);
                progressDialog.hide();
            }

        }.execute(item);


    }

    private void addItemToListView(DataItem item) {
        listViewAdapter.add(item);
    }

    private void showDetailviewForItem(DataItem item) {
        // Activity Instanz erstellt
        Intent detailViewIntent = new Intent(this, DetailviewActivity.class);

        // Parameter übergeben
        detailViewIntent.putExtra(DATA_ITEM, item);

        // Android öffnet detailviewActivity
        startActivity(detailViewIntent);
    }

    private void addNewItem() {
        Intent addNewItemIntent = new Intent(this, DetailviewActivity.class);

        startActivityForResult(addNewItemIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            DataItem item = (DataItem) data.getSerializableExtra(DATA_ITEM);
            createAndShowItem(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == helloText) {
            Log.i(logger, "onClick(): " + v);
        } else {
            Log.i(logger, "onClick() on unknown element: " + v);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}
