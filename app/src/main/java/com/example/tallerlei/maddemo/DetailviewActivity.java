package com.example.tallerlei.maddemo;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
/**
 * Created by Matthias on 02.05.2017.
 */

public class DetailviewActivity extends AppCompatActivity{

    protected static final String DATA_ITEM = "dataItem";

    private TextView itemNameText;
    private Button saveItemButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailview);
        setTitle(R.string.title_Detailview);
        itemNameText = (TextView) findViewById(R.id.itemNameText);
        saveItemButton = (Button) findViewById(R.id.saveItem);
        // getExtra Parameter from other Activity
        DataItem item = (DataItem) getIntent().getSerializableExtra(DATA_ITEM);
        if(item != null) {
            itemNameText.setText(item.getName());
        }
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
    }

    private void saveItem() {
        Intent returnIntent = new Intent();
        String itemName = itemNameText.getText().toString();
        DataItem item = new DataItem(itemName);
        returnIntent.putExtra(DATA_ITEM, item);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.saveItem) {
            saveItem();
            return true;
        }
        else if (item.getItemId() == R.id.deleteItem) {
            // delete functionality
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
