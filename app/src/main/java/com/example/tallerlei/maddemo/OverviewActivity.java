package com.example.tallerlei.maddemo;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener{

    protected static String logger = OverviewActivity.class.getSimpleName();
    private TextView helloText;
    private ViewGroup listView;
    private FloatingActionButton addItemButton;

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

        for (int i= 0; i < listView.getChildCount(); i++) {
            View currentChild = listView.getChildAt(i);
            if(currentChild instanceof TextView) {
                currentChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listItemSelected(v);
                    }
                });
            }
        }
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });
    }


    private void addNewItem() {
        Intent addNewItemIntent = new Intent(this, DetailviewActivity.class);

        startActivityForResult(addNewItemIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String itemName = data.getStringExtra("itemName");
            Toast.makeText(this, "got new iten name: " + itemName, Toast.LENGTH_SHORT).show();
        }
    }

    private void listItemSelected(View v) {
        // Toast.makeText(this, "selected: " + ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
        String itemName = ((TextView)v).getText().toString();

        // Activity Instanz erstellt
        Intent detailViewIntent = new Intent(this, DetailviewActivity.class);

        // Parameter übergeben
        detailViewIntent.putExtra("itemName", itemName);

        // Android öffnet detailviewActivity
        startActivity(detailViewIntent);
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
