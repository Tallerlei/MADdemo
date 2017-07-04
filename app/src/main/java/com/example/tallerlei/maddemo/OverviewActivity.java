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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tallerlei.maddemo.model.DataItem;
//import com.example.tallerlei.maddemo.model.IDataItemCRUDOperations;
import com.example.tallerlei.maddemo.model.IDataItemCRUDOperationsAsync;

import com.example.tallerlei.maddemo.model.LocalDataItemCRUDOperationsImpl;
import com.example.tallerlei.maddemo.model.RemoteDataItemCRUDOperationsImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.tallerlei.maddemo.DetailviewActivity.DATA_ITEM;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int EDIT_ITEM = 2;
    public static final int CREATE_ITEM = 1;
    protected static String logger = OverviewActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    private TextView helloText;
    private ViewGroup listView;
    private FloatingActionButton addItemButton;
    private ArrayAdapter<DataItem> listViewAdapter;
    private List<DataItem> itemsList = new ArrayList<DataItem>();

//    private List<DataItem> items = Arrays.asList(new DataItem[]{new DataItem("shit"), new DataItem("lalala"), new DataItem("nuklear"), new DataItem("blödmann"), new DataItem("hänker"), new DataItem("noche ein eintrag"), new DataItem("lorem"), new DataItem("ipsumt"), new DataItem("spasit"), new DataItem("vogel"), new DataItem("awesome")});

    private /*IDataItemCRUDOperations*/ IDataItemCRUDOperationsAsync crudOperations;

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
        listViewAdapter = new ArrayAdapter<DataItem>(this, R.layout.itemview_overview, itemsList) {
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

        crudOperations = ((DataItemApplication)getApplication()).getCRUDOperationsImpl() /*SimpleDataItemCRUDOperationsImpl();*/ /*LocalDataItemCRUDOperationsImpl(this);*/ /*RemoteDataItemCRUDOperationsImpl()*/;

        readItemsAndFillListView();
    }

    private void readItemsAndFillListView() {

        progressDialog.show();

        crudOperations.readAllDataItems(new IDataItemCRUDOperationsAsync.CallbackFunction<List<DataItem>>() {
            @Override
            public void process(List<DataItem> result) {
                progressDialog.hide();
                for (DataItem item : result) {
                    addItemToListView(item);
                }
                Log.i(logger, "items: " + itemsList);
            }
        });
    }

    public void createAndShowItem(/*final*/ DataItem item) {

        progressDialog.show();

        crudOperations.createDataItem(item, new IDataItemCRUDOperationsAsync.CallbackFunction<DataItem>(){
            @Override
            public void process(DataItem result) {
                addItemToListView(result);
                progressDialog.hide();
            }
        });
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
        startActivityForResult(detailViewIntent, EDIT_ITEM);


    }

    private void addNewItem() {
        Intent addNewItemIntent = new Intent(this, DetailviewActivity.class);

        startActivityForResult(addNewItemIntent, CREATE_ITEM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ITEM && resultCode == Activity.RESULT_OK) {
            DataItem item = (DataItem) data.getSerializableExtra(DATA_ITEM);
            createAndShowItem(item);
        } else if (requestCode == EDIT_ITEM) {
            if (resultCode == DetailviewActivity.RESULT_DELETE_ITEM) {
                DataItem item = (DataItem) data.getSerializableExtra(DATA_ITEM);
                deleteAndRemoveItem(item);
            }
        }
    }

    private void deleteAndRemoveItem(final DataItem item) {

        crudOperations.deleteDataItem(item.getId(), new IDataItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
            @Override
            public void process(Boolean deleted) {
                if (deleted) {
                    listViewAdapter.remove(findDataItemInList(item.getId()));
                }
            }

        });
    }

    private DataItem findDataItemInList(long id) {
        for (int i = 0; i < listViewAdapter.getCount(); i++) {
            if (listViewAdapter.getItem(i).getId() == id) {
                return listViewAdapter.getItem(i);
            }
        }

        return null;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void sortItems(){
        Log.i(logger, "geht?: " + itemsList);
        Collections.sort(itemsList, new Comparator<DataItem>(){
            @Override
            public int compare(DataItem o1, DataItem o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        this.listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sortItems) {
            sortItems();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
