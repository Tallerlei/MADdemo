package com.example.tallerlei.maddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tallerlei.maddemo.model.DataItem;
import com.example.tallerlei.maddemo.model.IDataItemCRUDOperationsAsync;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
    private SimpleDateFormat dateFormatter;
    private /*IDataItemCRUDOperations*/ IDataItemCRUDOperationsAsync crudOperations;

    private class ItemViewHolder {

        private DataItem item;
        private TextView itemNameView;
        private CheckBox itemDoneView;
        private ImageButton itemFavourite;
        private TextView itemDueDate;

        public ItemViewHolder(View itemView) {
            // read out the Checkbox
            this.itemDoneView = (CheckBox) itemView.findViewById(R.id.itemDone);
            // read out the text view for item name
            this.itemNameView = (TextView) itemView.findViewById(R.id.itemName);
            this.itemDueDate = (TextView) itemView.findViewById(R.id.itemDueDate);
            this.itemFavourite = (ImageButton) itemView.findViewById(R.id.itemFavourite);

            // set the view holder on the list item view
            itemView.setTag(this);
        }

        public void bindToView(final DataItem item) {
            if (item != null) {
                this.itemNameView.setText(item.getName());
                this.itemDueDate.setText(dateFormatter.format(item.getDueDate()));
                this.itemDoneView.setChecked(item.isDone());
//                this.getItemDoneView().setChecked(item.isDone());

                if (item.isFavourite() == true) {
                    this.itemFavourite.setBackgroundResource(R.drawable.fav);
                } else {
                    this.itemFavourite.setBackgroundResource(R.drawable.non_fav);
                }

                if(item.getDueDate() <= System.currentTimeMillis()){
                    itemNameView.setTextColor(ContextCompat.getColor(OverviewActivity.this, R.color.colorWarning));
                } else {
                    itemNameView.setTextColor(ContextCompat.getColor(OverviewActivity.this, R.color.colorText));
                }

                this.itemFavourite.setOnClickListener(new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item != null) {

                            if (item.isFavourite()) {
                                item.setFavourite(false);
                                itemFavourite.setBackgroundResource(R.drawable.non_fav);
                            } else {
                                item.setFavourite(true);
                                itemFavourite.setBackgroundResource(R.drawable.fav);
                            }
                            crudOperations.updateDataItem(item.getId(), item, new IDataItemCRUDOperationsAsync.CallbackFunction<DataItem>() {
                                @Override
                                public void process(DataItem result) {

                                }
                            });
                        }
                    }
                });
                this.itemDoneView.setOnClickListener(new CheckBox.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item != null) {

                            if (item.isDone()) {
                                item.setDone(false);
                                itemDoneView.setChecked(false);
                            } else {
                                item.setDone(true);
                                itemDoneView.setChecked(true);
                            }
                            crudOperations.updateDataItem(item.getId(), item, new IDataItemCRUDOperationsAsync.CallbackFunction<DataItem>() {
                                @Override
                                public void process(DataItem result) {

                                }
                            });
                        }
                    }
                });
                this.item = item;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. select the view to be controlled
        setContentView(R.layout.activity_overview);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
        // 2. read out elements from the view
        helloText = (TextView) findViewById(R.id.helloText);
        listView = (ViewGroup) findViewById(R.id.ListView);
        addItemButton = (FloatingActionButton) findViewById(R.id.addItemButton);

        progressDialog = new ProgressDialog(this);

        // 3. set content on the elements
        setTitle(R.string.title_overview);
        helloText.setText(R.string.hello_text);

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
                } else {
                    // create a new instance of list item view
                    itemView = getLayoutInflater().inflate(R.layout.itemview_overview, null);
                    // create a new instance of the view holder
                    ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);
                }

                // read out viewholder
                ItemViewHolder viewHolder = (ItemViewHolder) itemView.getTag();
                // read out item
                DataItem item = getItem(position);
                viewHolder.bindToView(item);

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

        crudOperations = ((DataItemApplication) getApplication()).getCRUDOperationsImpl() /*SimpleDataItemCRUDOperationsImpl();*/ /*LocalDataItemCRUDOperationsImpl(this);*/ /*RemoteDataItemCRUDOperationsImpl()*/;

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
                sortItems("default");
            }
        });
    }

    public void createAndShowItem(/*final*/ DataItem item) {

        progressDialog.show();

        crudOperations.createDataItem(item, new IDataItemCRUDOperationsAsync.CallbackFunction<DataItem>() {
            @Override
            public void process(DataItem result) {
                addItemToListView(result);
                progressDialog.hide();
            }
        });
    }

    public void updateAndShowItem(final Long id, final DataItem item) {
        progressDialog.show();
        crudOperations.updateDataItem(id, item, new IDataItemCRUDOperationsAsync.CallbackFunction<DataItem>() {
            @Override
            public void process(DataItem result) {
                itemsList.set(getArrayPosition(id), item);
                listViewAdapter.notifyDataSetChanged();
                progressDialog.hide();
            }
        });
    }

    public int getArrayPosition(Long id) {
        for (int i = 0; i < listViewAdapter.getCount(); i++) {
            if (listViewAdapter.getItem(i).getId() == id) {
                return i;
            }
        }

        return -1;
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
            DataItem item = (DataItem) data.getSerializableExtra(DATA_ITEM);
            if (resultCode == DetailviewActivity.RESULT_DELETE_ITEM) {
                deleteAndRemoveItem(item);
            } else {
                updateAndShowItem(item.getId(), item);
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
        } else {
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

    private void sortItems(String sorting) {

        Comparator<DataItem> compareDueDate = new Comparator<DataItem>() {
            @Override
            public int compare(DataItem o1, DataItem o2) {
                Long b1 = o1.getDueDate();
                Long b2 = o2.getDueDate();
                return b1.compareTo(b2);
            }
        };
        Comparator<DataItem> compareFavourite = new Comparator<DataItem>() {
            @Override
            public int compare(DataItem o1, DataItem o2) {
                Boolean b1 = o1.isFavourite();
                Boolean b2 = o2.isFavourite();
                return b2.compareTo(b1);
            }
        };
        Comparator<DataItem> compareDone = new Comparator<DataItem>() {
            @Override
            public int compare(DataItem o1, DataItem o2) {
                Boolean b1 = o1.isDone();
                Boolean b2 = o2.isDone();
                return b2.compareTo(b1);
            }
        };

        Comparator<DataItem> compareName = new Comparator<DataItem>() {
            @Override
            public int compare(DataItem o1, DataItem o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };

        switch (sorting) {
            case "date":
                Collections.sort(itemsList, compareFavourite);
                Collections.sort(itemsList, compareDueDate);
                Collections.sort(itemsList, compareDone);
                break;
            case "favourites":
                Collections.sort(itemsList, compareDueDate);
                Collections.sort(itemsList, compareFavourite);
                Collections.sort(itemsList, compareDone);
                break;
            default:
                Collections.sort(itemsList, compareDone);
        }
        this.listViewAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sortByDate) {
            sortItems("date");
            return true;
        } else if (item.getItemId() == R.id.sortByImportance) {
            sortItems("favourites");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
