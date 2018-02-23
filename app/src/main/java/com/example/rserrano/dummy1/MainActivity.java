package com.example.rserrano.dummy1;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private CursorAdapter cursorAdapter;
    private static final String AUTHORITY1 = "com.example.rserrano.dummy1";
    private static final String AUTHORITY2 = "com.example.cristinacorrecher.dummy2";
    private static final String BASE_PATH = "contacts";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY1 + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int CONTACTS = 1;
    private static final int CONTACT_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY1,BASE_PATH, CONTACTS);
        uriMatcher.addURI(AUTHORITY1,BASE_PATH + "/#",CONTACT_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cursorAdapter = new ContactsCursorAdapter(this,null,0);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getLoaderManager().initLoader(0, null, this);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartLoader2();
            }
        });
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartLoader1();
            }
        });
    }

    private void restartLoader2() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View getEmpIdView = li.inflate(R.layout.dialog_get_name_phone, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        // set dialog_get_name_phone.xml to alertdialog builder
        alertDialogBuilder.setView(getEmpIdView);

        final EditText nameInput = (EditText) getEmpIdView.findViewById(R.id.editTextDialogNameInput);
        final EditText phoneInput = (EditText) getEmpIdView.findViewById(R.id.editTextDialogPhoneInput);
        // set dialog message

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        insertContact(nameInput.getText().toString(), phoneInput.getText().toString(),"dummy2");
                    }
                }).create()
                .show();
    }
    private void restartLoader1() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View getEmpIdView = li.inflate(R.layout.dialog_get_name_phone, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        // set dialog_get_name_phone.xml to alertdialog builder
        alertDialogBuilder.setView(getEmpIdView);

        final EditText nameInput = (EditText) getEmpIdView.findViewById(R.id.editTextDialogNameInput);
        final EditText phoneInput = (EditText) getEmpIdView.findViewById(R.id.editTextDialogPhoneInput);
        // set dialog message

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        insertContact(nameInput.getText().toString(), phoneInput.getText().toString(),"dummy1");
                    }
                }).create()
                .show();
    }

    private void deleteAllContacts() {

        getContentResolver().delete(ContactsProvider.CONTENT_URI,null,null);
        //restartLoader();
        Toast.makeText(this,"All Contacts Deleted",Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAllContacts:
                deleteAllContacts();
                return true;
            default:
                return false;
        }
    }

    private void insertContact(String contactName,String contactPhone,String origin) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.CONTACT_NAME,contactName);
        values.put(DBOpenHelper.CONTACT_PHONE,contactPhone);
        values.put(DBOpenHelper.CONTENT_ORIGIN, origin);
        Uri contactUri  = getContentResolver().insert(ContactsProvider.CONTENT_URI,values);
        Toast.makeText(this,"Created Contact " + contactName,Toast.LENGTH_LONG).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

}