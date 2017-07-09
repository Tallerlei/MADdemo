package com.example.tallerlei.maddemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.example.tallerlei.maddemo.model.DataItem;

import android.util.Log;
import android.widget.TimePicker;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Matthias on 02.05.2017.
 */

public class DetailviewActivity extends AppCompatActivity {

    protected static final String DATA_ITEM = "dataItem";

    public static final int RESULT_DELETE_ITEM = 10;
    public static final int RESULT_UPDATE_ITEM = 20;
    public static final int REQUEST_PICK_CONTACT = 1;
    private TextView itemNameText;
    private TextView itemDescriptionText;
    private TextView itemDueDate;
    private TextView itemDueTime;
    private TextView itemContacts;
    private Button setDueTimeButton;
    private DatePickerDialog dueDatePickerDialog;
    private CheckBox itemDone;
    private CheckBox itemFavourite;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private SimpleDateFormat dateTimeFormatter;
    private Button saveItemButton;

    private DataItem item;

    private Button mPickTime;
    private int mHour;
    private int mMinute;
    static final int TIME_DIALOG_ID = 0;

    /**
     * Called when the Activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // select layout
        setContentView(R.layout.activity_detailview);

        // read out UI elements
        // Capture our View elements

        itemNameText = (TextView) findViewById(R.id.itemNameText);
        itemDescriptionText = (TextView) findViewById(R.id.itemDescriptionText);
        itemDueDate = (TextView) findViewById(R.id.itemDueDate);
        itemDueTime = (TextView) findViewById(R.id.itemDueTime);
        itemContacts = (TextView) findViewById(R.id.itemContacts);
        setDueTimeButton = (Button) findViewById(R.id.setDueTimeButton);
        itemDone = (CheckBox) findViewById(R.id.itemDone);
        itemFavourite = (CheckBox) findViewById(R.id.itemFavourite);
        saveItemButton = (Button) findViewById(R.id.saveItem);

        // Date Picker
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
        timeFormatter = new SimpleDateFormat("hh:mm", Locale.GERMANY);
        dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.GERMANY);
        setDateField();

        // set content on UI elements
        setTitle(R.string.title_Detailview);
        // getExtra Parameter from other Activity
        item = (DataItem) getIntent().getSerializableExtra(DATA_ITEM);

        if (item != null) {
            itemNameText.setText(item.getName());
            itemDescriptionText.setText(item.getDescription());
            itemDone.setChecked(item.isDone());
            itemFavourite.setChecked(item.isFavourite());
            itemDueDate.setText(dateFormatter.format(item.getDueDate()));
            itemDueTime.setText(timeFormatter.format(item.getDueDate()));
        }
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        itemDueDate.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                dueDatePickerDialog.show();
            }

        });
        setDueTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
        }
        return null;
    }

    // Update the time we display in the TextView
    private void updateDisplay() {
        itemDueTime.setText(
                new StringBuilder()
                        .append(pad(mHour)).append(":")
                        .append(pad(mMinute)));
    }

    // The callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    updateDisplay();
                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private void setDateField() {
        Calendar newCalendar = Calendar.getInstance();
        dueDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                itemDueDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void saveItem() {
        Long updateId = Long.valueOf(0);
        if(item != null) {
            updateId = item.getId();
        }

        Intent returnIntent = new Intent();
        String itemName = itemNameText.getText().toString();
        String itemDescription = itemDescriptionText.getText().toString();
        String date = itemDueDate.getText().toString();
        String time = itemDueTime.getText().toString();

        if (date.isEmpty()) {
            date = dateFormatter.format(new Date().getTime());
        }
        if (time.isEmpty()) {
            time = timeFormatter.format(new Date().getTime());
        }
        String dateTime = date + " " + time;
        long dateTimeMillis = 0;
        try {
            Date dT = dateTimeFormatter.parse(dateTime);
            dateTimeMillis = dT.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Boolean done = itemDone.isChecked();
        Boolean favourite = itemFavourite.isChecked();
        DataItem newItem = new DataItem(itemName, itemDescription, dateTimeMillis, done, favourite);
        newItem.setId(updateId);
        returnIntent.putExtra(DATA_ITEM, newItem);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void deleteItem() {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }
    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(DATA_ITEM, item);
                        setResult(RESULT_DELETE_ITEM, returnIntent);
                        finish();
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

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
//                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int phoneNumberType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));

                if (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {

                    String contacts = itemContacts.getText().toString();
                    if (contacts.indexOf("no Contacts attached")!= -1) {
                        contacts = "";
                    }
                    itemContacts.setText(contacts + name + ";");
                    break;
                }
            } while (phoneCursor.moveToNext());
        }
    }
}
