package com.example.administrator.common.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class EqptDao2 {
    public static EqptDao2 instance;
    private final String TABLE_NAME = "eqpt2";
    private DBHelper helper;

    private EqptDao2(Context context) {
        this.helper = new DBHelper(context, TABLE_NAME);
    }

    public static synchronized EqptDao2 getInstance(Context context) {
        EqptDao2 eqptDao2;
        synchronized (EqptDao2.class) {
            if (instance == null) {
                instance = new EqptDao2(context);
            }
            eqptDao2 = instance;
        }
        return eqptDao2;
    }

    public boolean addEqpt(String eqptID, String searchName) {
        boolean flag = false;
        SQLiteDatabase database = null;
        try {
            database = this.helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ID", eqptID);
            values.put("SearchName", searchName);
            database.insert("eqpt2", null, values);
            flag = true;
            if (database != null) {
                database.close();
            }
        } catch (Exception e) {
            if (database != null) {
                database.close();
            }
        } catch (Throwable th) {
            if (database != null) {
                database.close();
            }
        }
        return flag;
    }

    public List<Eqpt> getAllList() {
        List<Eqpt> list = new ArrayList();
        SQLiteDatabase database = null;
        String sql = "select * from " + TABLE_NAME;
        try {
            database = this.helper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Eqpt eqpt = new Eqpt();
                eqpt.EqptID = cursor.getString(cursor.getColumnIndex("ID"));
                eqpt.SearchName = cursor.getString(cursor.getColumnIndex("SearchName"));
                list.add(eqpt);
            }
            if (database != null) {
                database.close();
            }
        } catch (Exception e) {
            if (database != null) {
                database.close();
            }
        } catch (Throwable th) {
            if (database != null) {
                database.close();
            }
        }
        return list;
    }

    public boolean deletAll(){
        boolean flag = false;
        SQLiteDatabase database = null;
        String sql = "DELETE FROM " + TABLE_NAME;
        try {
            database = helper.getReadableDatabase();
            database.execSQL(sql);
        } catch (Exception e) {
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return flag;
    }

    public class Eqpt{
        public String EqptID;
        public String SearchName;
    }
}
