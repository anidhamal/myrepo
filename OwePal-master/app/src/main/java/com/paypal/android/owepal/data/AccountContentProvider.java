/*
 * Copyright (c) 2015 PayPal, Inc.
 *
 * All rights reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package com.paypal.android.owepal.data;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * TODO: Write Javadoc for MoneyContentProvider.
 *
 * @author pngai
 */
public class AccountContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.paypal.provider.Account";
    static final String URL = "content://" + PROVIDER_NAME + "/account";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String AMOUNT = "amount";
    public static final String DATETIME = "datetime";

    private static HashMap<String, String> ACCOUNT_PROJECTION_MAP;
    private static HashMap<String, String> ACCOUNT_HISTORY_PROJECTION_MAP;

    static final int ACCOUNTS = 1;
    static final int ACCOUNTS_ID = 2;
    static final int ACCOUNTS_HISTORY = 3;
    static final int ACCOUNTS_HISTORY_ID = 4;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "accounts", ACCOUNTS);
        uriMatcher.addURI(PROVIDER_NAME, "accounts/#", ACCOUNTS_ID);
        uriMatcher.addURI(PROVIDER_NAME, "accountshistory", ACCOUNTS_HISTORY);
        uriMatcher.addURI(PROVIDER_NAME, "accountshistory/#", ACCOUNTS_HISTORY_ID);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "ACCOUNTS";
    static final String ACCOUNTS_TABLE_NAME = "accounts";
    static final String ACCOUNTS_HISTORY_TABLE_NAME = "accounts_history";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_ACCOUNT_DB_TABLE =
            " CREATE TABLE " + ACCOUNTS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " amount REAL NOT NULL);";
    static final String CREATE_ACCOUNT_HISTORY_DB_TABLE =
            " CREATE TABLE " + ACCOUNTS_HISTORY_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " datetime TEXT NOT NULL); ";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_ACCOUNT_DB_TABLE);
            db.execSQL(CREATE_ACCOUNT_HISTORY_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_HISTORY_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = 0;
        switch (uriMatcher.match(uri)) {
            case ACCOUNTS:
                rowID = db.insert(ACCOUNTS_TABLE_NAME, "", values);
                break;
            case ACCOUNTS_HISTORY:
                rowID = db.insert(ACCOUNTS_HISTORY_TABLE_NAME, "", values);
                break;
            default: break;
        }

        /**
         * If record is added successfully
         */
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case ACCOUNTS:
                qb.setTables(ACCOUNTS_TABLE_NAME);
                qb.setProjectionMap(ACCOUNT_PROJECTION_MAP);
                break;
            case ACCOUNTS_ID:
                qb.setTables(ACCOUNTS_TABLE_NAME);
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;
            case ACCOUNTS_HISTORY:
                qb.setTables(ACCOUNTS_HISTORY_TABLE_NAME);
                qb.setProjectionMap(ACCOUNT_HISTORY_PROJECTION_MAP);
                break;
            case ACCOUNTS_HISTORY_ID:
                qb.setTables(ACCOUNTS_HISTORY_TABLE_NAME);
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on task ids
             */
            sortOrder = _ID;
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case ACCOUNTS:
                count = db.delete(ACCOUNTS_TABLE_NAME, selection, selectionArgs);
                break;
            case ACCOUNTS_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(ACCOUNTS_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
            case ACCOUNTS_HISTORY:
                count = db.delete(ACCOUNTS_HISTORY_TABLE_NAME, selection, selectionArgs);
                break;
            case ACCOUNTS_HISTORY_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(ACCOUNTS_HISTORY_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case ACCOUNTS:
                count = db.update(ACCOUNTS_TABLE_NAME, values, selection, selectionArgs);
                break;
            case ACCOUNTS_ID:
                count = db.update(ACCOUNTS_TABLE_NAME, values, _ID +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
            case ACCOUNTS_HISTORY:
                count = db.update(ACCOUNTS_HISTORY_TABLE_NAME, values, selection, selectionArgs);
                break;
            case ACCOUNTS_HISTORY_ID:
                count = db.update(ACCOUNTS_HISTORY_TABLE_NAME, values, _ID +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all records
             */
            case ACCOUNTS:
                return "vnd.android.cursor.dir/vnd.example.accounts";
            /**
             * Get a particular record
             */
            case ACCOUNTS_ID:
                return "vnd.android.cursor.item/vnd.example.accounts";
            case ACCOUNTS_HISTORY:
                return "vnd.android.cursor.dir/vnd.example.account_history";
            case ACCOUNTS_HISTORY_ID:
                return "vnd.android.cursor.dir/vnd.example.account_history";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
