package com.example.words5;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WordsDBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "wordsdb";//数据库名字
    private final static int DATABASE_VERSION = 1;//数据库版本
    // 建表SQL
    private final static String CREATE_WORDSDB = "create table words(" +
            "id integer primary key autoincrement," +
            "word text," +
            "meaning text)";
    //删表SQL
    private final static String SQL_DELETE_DATABASE = "drop table if exists words" ;

    public WordsDBHelper(Context context1, String s, Context context, int i){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORDSDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_DATABASE);
    }
}
