package com.example.words5;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {
    public static final int TABLE1_DIR = 0;
    public static final int TABLE1_ITEM = 1;
    public static final  String AddWord = "com.example.words5.provider";
    private static UriMatcher uriMatcher;
    private WordsDBHelper WdbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AddWord,"word",TABLE1_DIR);
        uriMatcher.addURI(AddWord,"word/#",TABLE1_ITEM);
    }

    public DatabaseProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = WdbHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case TABLE1_DIR:
                deletedRows = db.delete("words", selection, selectionArgs);
                break;
            case TABLE1_ITEM:
                String id = uri.getPathSegments().get(1);
                deletedRows = db.delete("words", "id = ?", new String[]{id});
                break;
            default:
                break;
        }
        return deletedRows;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TABLE1_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.words5.provider.words";
            case TABLE1_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.words5.provider.words";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = WdbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)){
            case TABLE1_DIR:
            case TABLE1_ITEM:
                long newWordId = db.insert("words",null,values);
                uriReturn = Uri.parse("content://"+ AddWord +"/word/"+newWordId);
                break;
            default:
                break;
        }
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        WdbHelper = new WordsDBHelper(getContext(),"wordsdb",null,1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = WdbHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case TABLE1_DIR:
                cursor = db.query("words",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case TABLE1_ITEM:
                String id = uri.getPathSegments().get(1);
                cursor = db.query("words",projection,"id = ?",new String[]{ id },null,null,sortOrder);
            default:
                break;
        }
                return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = WdbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (uriMatcher.match(uri)){
            case TABLE1_DIR:
                updatedRows = db.update("words",values,selection,selectionArgs);
                break;
            case TABLE1_ITEM:
                String id = uri.getPathSegments().get(1);
                updatedRows = db.update("words",values,"id = ?",new String[]{ id });
                break;
            default:
                break;
        }
        return updatedRows;
    }
}
