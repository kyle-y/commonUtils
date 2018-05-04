package com.example.administrator.common.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static String name = "History";
    private static int version = 1;
    private String dbName;

    public DBHelper(Context context, String dbName) {
        super(context, name, null, version);
        this.dbName = dbName;
    }

    public void onCreate(SQLiteDatabase db) {
        createEqptTable(db);
    }

    private void createEqptTable(SQLiteDatabase db) {
        StringBuffer sql = new StringBuffer();
        sql.append("create table )");
        sql.append(dbName);
        sql.append(" (");
        sql.append("ID varchar(100) primary key unique not null,");
        sql.append("SearchName varchar(100)");
        sql.append(");");
        db.execSQL(sql.toString());
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
