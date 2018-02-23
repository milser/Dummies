package com.example.rserrano.dummy1;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by rserrano on 22/02/2018.
 */

public class ContactsProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.rserrano.dummy1";
    private static final String BASE_PATH = "contacts";
    public  static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );
    private static final String[] PERMISION = {"com.example.rserrano.dummy1","com.example.cristinacorrecher.dummy2"};

    private static final int CONTACTS = 1;
    private static final int CONTACT_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY,BASE_PATH, CONTACTS);
        uriMatcher.addURI(AUTHORITY,BASE_PATH + "/#",CONTACT_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query (
            Uri uri, // Uri
            String[] projection, // Which columns to return or null for all columns.
            String selection, // WHERE clause or null for no filter
            String[] selectionArgs,   // WHERE clause value substitution
            String sortOrder // Sort order or null
    ) {
        Log.d("Query",getCallingPackage());
        if(checkPermission(getCallingPackage())){

            Cursor cursor;
            switch (uriMatcher.match(uri)) {
                case CONTACTS:
                    cursor =  database.query(DBOpenHelper.TABLE_CONTACTS,DBOpenHelper.ALL_COLUMNS,
                            selection,null,null,null,DBOpenHelper.CONTACT_NAME +" ASC");
                    break;
                default:
                    throw new IllegalArgumentException("This is an Unknown URI " + uri);
            }
            cursor.setNotificationUri(getContext().getContentResolver(),uri);

            return cursor;
        }
    return null;
    }

    private boolean checkPermission(String check) {
        for(int x=0;x<PERMISION.length;x++) {
            if (PERMISION[x].equals(check)){
                Log.e("-->", "Permision Granted: " + PERMISION[x]);
                return true;
            } else {
                Log.e("-->", "Permision Denied: " + check);
                return false;
            }
        }
        Log.e("-->", "Permision Denied Out: " + check);
        return false;
    }

    @Nullable
    @Override
    public String getType (
            Uri uri //Uri of the query
    ) {

        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                return "vnd.android.cursor.dir/contacts";
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert (
            Uri uri, // URI of the insertion request. can't be null.
            ContentValues values // column/value pairs to be added to storage, can't be null
    ) {
        long id = database.insert(DBOpenHelper.TABLE_CONTACTS,null,values);
        Log.d("INSERT","id: "+values);
        if (id > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(_uri, null);
            Log.d("INSERT","_uri: "+ContentUris.withAppendedId(CONTENT_URI, id));
            return _uri;
        }
        throw new SQLException("Insertion Failed for URI :" + uri);

    }

    @Override
    public int delete (
            Uri uri, //The URI of the query, will have a record ID if you want to delete one record.
            String selection, // optional WHERE clause to match the rows to be deleted
            String[] selectionArgs // WHERE clause value substitution
    ) {
        int delCount = 0;
        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                delCount =  database.delete(DBOpenHelper.TABLE_CONTACTS,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return delCount;
    }

    @Override
    public int update (
            Uri uri, //The URI of query, will have a record ID if you want to update one record.
            ContentValues values, // column/value pairs to be updated in storage, can't be null
            String selection, // optional WHERE clause to match the rows to be updated
            String[] selectionArgs // WHERE clause value substitution
    ) {
        int updCount = 0;
        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                updCount =  database.update(DBOpenHelper.TABLE_CONTACTS,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updCount;
    }
}