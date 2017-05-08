package com.example.tallerlei.maddemo;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Matthias on 02.05.2017.
 */

public class DetailviewActivity extends AppCompatActivity{


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
        String itemName = getIntent().getStringExtra("itemName");
        if(itemName != null) {
            itemNameText.setText(itemName);
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
        returnIntent.putExtra("itemName", itemName);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
