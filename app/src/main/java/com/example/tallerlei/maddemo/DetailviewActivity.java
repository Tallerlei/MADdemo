package com.example.tallerlei.maddemo;

import android.content.Intent;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.example.tallerlei.maddemo.model.DataItem;

import android.util.Log;

/**
 * Created by Matthias on 02.05.2017.
 */

public class DetailviewActivity extends AppCompatActivity {

    protected static final String DATA_ITEM = "dataItem";

    public static final int RESULT_DELETE_ITEM = 10;
    public static final int REQUEST_PICK_CONTACT = 1;
    private TextView itemNameText;
    private Button saveItemButton;

    private DataItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // select layout
        setContentView(R.layout.activity_detailview);

        // read out UI elements
        itemNameText = (TextView) findViewById(R.id.itemNameText);
        saveItemButton = (Button) findViewById(R.id.saveItem);

        // set content on UI elements
        setTitle(R.string.title_Detailview);
        // getExtra Parameter from other Activity
        item = (DataItem) getIntent().getSerializableExtra(DATA_ITEM);

        if (item != null) {
            itemNameText.setText(item.getName());
        }
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        itemNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateEmailText();
                    return true;
                }
                return false;
            }
        });
    }

    private void validateEmailText() {
        String itemName = itemNameText.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(itemName).matches()) {
            itemNameText.setError("thithith");
        } else {
        }

    }


    private void saveItem() {

        Intent returnIntent = new Intent();
        String itemName = itemNameText.getText().toString();
        DataItem item = new DataItem(itemName);
        returnIntent.putExtra(DATA_ITEM, item);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void deleteItem() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(DATA_ITEM, item);

        setResult(RESULT_DELETE_ITEM, returnIntent);
        Log.i("DetailviewActivity", "finishing");
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_detailview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.saveItem) {
            saveItem();
            return true;
        } else if (item.getItemId() == R.id.deleteItem) {

            // delete functionality
            deleteItem();
            return true;
        } else if (item.getItemId() == R.id.addContact) {
            addContact();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void addContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(pickContactIntent, REQUEST_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_CONTACT && resultCode == RESULT_OK) {
            processSelectedContact(data.getData());
        }
    }

    private void processSelectedContact(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);

        phoneCursor.moveToFirst();
        if (phoneCursor.getCount() > 0) {
            do {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int phoneNumberType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));

                if (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                    break;
                }
            } while (phoneCursor.moveToNext());
        }
    }
}
