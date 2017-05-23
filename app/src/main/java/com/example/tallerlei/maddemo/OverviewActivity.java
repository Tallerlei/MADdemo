package com.example.tallerlei.maddemo;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import static com.example.tallerlei.maddemo.DetailviewActivity.DATA_ITEM;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener{

    protected static String logger = OverviewActivity.class.getSimpleName();

    private TextView helloText;
    private ViewGroup listView;
    private FloatingActionButton addItemButton;
    private ArrayAdapter<DataItem> listViewAdapter;

    private List<DataItem> items = Arrays.asList(new DataItem[]{new DataItem("shit"), new DataItem("lalala"), new DataItem("nuklear"), new DataItem("blödmann"), new DataItem("hänker"), new DataItem("noche ein eintrag"), new DataItem("lorem"), new DataItem("ipsumt"), new DataItem("spasit"),new DataItem("vogel"), new DataItem("awesome")});

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
        Log.i(logger,  "helloText: " + helloText);
        listView = (ViewGroup) findViewById(R.id.ListView);
        Log.i(logger,  "listView: " + listView);
        addItemButton = (FloatingActionButton) findViewById(R.id.addItemButton);

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
        listViewAdapter = new ArrayAdapter<DataItem>(this, R.layout.itemview_overview){
            @NonNull
            @Override
            public View getView(int position, View itemView, ViewGroup parent) {

                if(itemView != null) {
                    Log.i(logger, "reusing existing itemView for element at position: " + position);
                }
                else {
                    Log.i(logger, "creating new itemView for element at position: " + position);
                    // create a new instance of list item view
                    itemView = getLayoutInflater().inflate(R.layout.itemview_overview, null);
                    // read out the text view for item name
                    TextView itemNameView = (TextView)itemView.findViewById(R.id.itemName);
                    // create a new instance of the view holder
                    ItemViewHolder itemViewHolder = new ItemViewHolder();
                    // set the itemNameView attribute on view holder to text view
                    itemViewHolder.itemNameView = itemNameView;
                    // set the view holder on the list item view
                    itemView.setTag(itemViewHolder);
                }

                ItemViewHolder viewHolder = (ItemViewHolder)itemView.getTag();

                DataItem item = getItem(position);
//                Log.i(logger, "creating view for position " + position + " and item: " + item);

                viewHolder.itemNameView.setText(item.getName());

                return itemView;
            }
        };
        ((ListView)listView).setAdapter(listViewAdapter);
        listViewAdapter.setNotifyOnChange(true);

        readItemsAndFillListView();
    }

    private void readItemsAndFillListView() {
        for(DataItem item : items) {
            addItemToListView(item);
        }
    }

    private void addItemToListView(DataItem item) {

        listViewAdapter.add(item);


//        View listItemView = getLayoutInflater().inflate(R.layout.itemview_overview, null);
//        TextView itemNameView = (TextView) listItemView.findViewById(R.id.itemName);
//
//        listItemView.setTag(item);
//        itemNameView.setText(item.getName());
//
//        listItemView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                DataItem item = (DataItem) v.getTag();
//                showDetailviewForItem(item);
//            }
//        });
//        listView.addView(listItemView);

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

        startActivityForResult(addNewItemIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            DataItem item = (DataItem) data.getSerializableExtra(DATA_ITEM);
            addItemToListView(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == helloText) {
            Log.i(logger, "onClick(): " + v);
        }
        else {
            Log.i(logger, "onClick() on unknown element: " + v);
        }
    }


}
