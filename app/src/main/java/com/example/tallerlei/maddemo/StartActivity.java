package com.example.tallerlei.maddemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tallerlei.maddemo.model.DataItem;
import com.example.tallerlei.maddemo.model.IDataItemCRUDOperationsAsync;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class StartActivity extends AppCompatActivity {
    private TextView loginEmail;
    private TextView loginPassword;
    private TextView loginError;
    private Button loginButton;
    private Boolean pwCorrect = false;
    private Boolean emailCorrect = false;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        loginPassword = (TextView) findViewById(R.id.loginPassword);
        loginEmail = (TextView) findViewById(R.id.loginEmail);
        loginError = (TextView) findViewById(R.id.loginErrorText);
        loginButton = (Button) findViewById(R.id.loginButton);
        progressDialog = new ProgressDialog(this);
        TextWatcher credentialsChangeWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (loginError.getVisibility() == View.VISIBLE) {
                    loginError.setVisibility(View.INVISIBLE);
                }
            }
        };
        loginEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    validateEmailText();
                }
                return false;
            }
        });
        loginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    validatePasswordText();
                }
                return false;
            }
        });
        loginEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEmailText();
                }
            }
        });

        loginPassword.addTextChangedListener(credentialsChangeWatcher);
        loginEmail.addTextChangedListener(credentialsChangeWatcher);
        loginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validatePasswordText();
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loginEmail.clearFocus();
                loginPassword.clearFocus();
                loginButton.requestFocus();
                if (pwCorrect && emailCorrect) {
                    progressDialog.show();
                    JSONObject credentials = new JSONObject();


                    try {
                        credentials.put("email", loginEmail.getText().toString());
                        credentials.put("pwd", loginPassword.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((DataItemApplication) getApplication()).loginToRemoteDatabase(credentials, new IDataItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
                        @Override
                        public void process(Boolean result) {
                            if (result == true) {
                                syncDbsAndStart();
                            } else {
                                progressDialog.hide();
                                loginError.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }


            }
        });
        progressDialog.show();
        ((DataItemApplication) getApplication()).isWebServiceAvailable(new IDataItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
            @Override
            public void process(Boolean result) {
                progressDialog.hide();
                if (!result) {
                    Toast.makeText(StartActivity.this, "Remote Database is NOT Available!", Toast.LENGTH_LONG).show();
                    Intent callOverviewIntent = new Intent(StartActivity.this, OverviewActivity.class);
                    startActivity(callOverviewIntent);
                } else {
                    Toast.makeText(StartActivity.this, "Remote Database is Available!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void validateEmailText() {
        String email = loginEmail.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Email format incorrect");
            emailCorrect = false;
        } else {
            emailCorrect = true;
        }
    }

    private void validatePasswordText() {
        String password = loginPassword.getText().toString();
        if (!password.matches("\\d{6}")) {
            loginPassword.setError("Password format incorrect");
            pwCorrect = false;
        } else {
            pwCorrect = true;
        }
    }


    public void syncDbsAndStart() {
        if (localEntries(StartActivity.this)) {
            ((DataItemApplication) getApplication()).deleteAllDataItems(new IDataItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
                @Override
                public void process(Boolean result) {
                    ((DataItemApplication) getApplication()).addLocalItemsToRemoteDb(new IDataItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
                        @Override
                        public void process(Boolean result) {
                            progressDialog.hide();
                            Intent callOverviewIntent = new Intent(StartActivity.this, OverviewActivity.class);
                            startActivity(callOverviewIntent);
                        }

                    });
                }

            });
        } else {
            ((DataItemApplication) getApplication()).addRemoteItemsToLocalDb(new IDataItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
                @Override
                public void process(Boolean result) {
                    progressDialog.hide();
                    Intent callOverviewIntent = new Intent(StartActivity.this, OverviewActivity.class);
                    startActivity(callOverviewIntent);
                }

            });

            // implement getting items to local db
        }
    }

    public static boolean localEntries(Context context) {
        SQLiteDatabase db = context.openOrCreateDatabase("mydb7.sqlite", Context.MODE_PRIVATE, null);
        String selectString = "SELECT * FROM *";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = db.query("DATAITEMS", new String[]{"ID", "NAME", "DESCRIPTION", "DUEDATE", "FAVOURITE", "DONE"}, null, null, null, null, "ID");
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }
}
