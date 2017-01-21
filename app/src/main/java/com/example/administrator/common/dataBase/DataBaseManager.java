package com.example.administrator.common.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/8.
 */
public class DataBaseManager<T> {
    private SQLiteOpenHelper dbHelper;
    public static DataBaseManager instance = null;
    private SQLiteDatabase sqLiteDatabase;

    private DataBaseManager(Context context){
//        dbHelper = new DataBaseHelper(context, );
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    /**
     * 通过单例模式获取DataBaseManager实例
     * @param context
     * @return
     */
    public static final DataBaseManager getInstance(Context context){
        if(instance == null){
            if(context == null){
                throw new RuntimeException("Context is null");
            }
            instance = new DataBaseManager(context);
        }
        return instance;
    }

    /**
     * 关闭数据库，释放资源
     */
    public void close(){
        if(sqLiteDatabase.isOpen()){
            sqLiteDatabase.close();
            sqLiteDatabase = null;
        }

        if(dbHelper != null){
            dbHelper.close();
            dbHelper = null;
        }

        if(instance != null){
            instance = null;
        }
    }

    /**
     * 执行一个不带占位符参数的SQL语句
     */
    public void execSql(String sql){
        if(sqLiteDatabase != null && sqLiteDatabase.isOpen()){
            sqLiteDatabase.execSQL(sql);
        }else{
            throw new RuntimeException("The DataBase is closed!");
        }
    }


    /**
     * 查询表中有多少条数据
     * @param table
     * @return
     * @throws Exception
     */
    public int getDataCounts(String table) throws Exception{
        Cursor cursor = null;
        int counts = 0;
        if(sqLiteDatabase.isOpen()){
            cursor = queryData2Cursor("select * from" + table, null);
            if(cursor != null && cursor.moveToFirst()){
                counts = cursor.getCount();
            }
        }else{
            throw new RuntimeException("The database is closed!");
        }
        return counts;
    }

    /**
     * 清空表中所有数据
     * @param table
     * @throws Exception
     */
    public void clearAllData(String table) throws Exception{
        if(sqLiteDatabase.isOpen()){
            sqLiteDatabase.execSQL("DELETE FROM" + table);
        }else{
            throw new RuntimeException("The database is closed!");
        }
    }

    /**
     * 插入数据
     * @param sql
     * @param bindArgs
     * @return
     * @throws Exception
     */
    public long insertDataBySql(String sql, String[] bindArgs) throws Exception{
        long result = 0;
        if(sqLiteDatabase.isOpen()){
            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sql);
            if(bindArgs != null){
                int size = bindArgs.length;
                for(int i = 0; i < size; i++){
                    //将参数和占位符绑定起来，对应起来
                    sqLiteStatement.bindString(i+1, bindArgs[i]);
                }
                result = sqLiteStatement.executeInsert();
                sqLiteStatement.close();
            }
        }else{
            throw new RuntimeException("The database is closed!");
        }
        return result;
    }

    /**
     * 插入数据
     * @param table
     * @param values
     * @return
     * @throws Exception
     */
    public long insertData(String table, ContentValues values) throws Exception{
        long result = 0;
        if(sqLiteDatabase.isOpen()){
            result = sqLiteDatabase.insertOrThrow(table, null, values);
        }else{
            throw new RuntimeException("The database is closed!");
        }
        return result;
    }

    /**
     * 批量插入数据
     * @param table 数据库表明
     * @param listData 数据源
     * @param args 数据的键名
     * @return
     * @throws Exception
     */
    public long insertBatchData(String table, List<Map<String, Object>> listData, String[] args) throws Exception{
        sqLiteDatabase.beginTransaction();
        long resultNum = 0;
        ContentValues values = new ContentValues();
        for(int i = 0; i < listData.size(); i++){
            for(int j = 0; j < args.length;j ++){
                values.put(args[j], listData.get(i).get(args[j]).toString());
            }
            long num = insertData(table, values);
            resultNum += num;
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        return resultNum;
    }

    /**
     * 批量插入数据
     * @param table 表名
     * @param list 数据
     * @param args 键名
     * @return
     * @throws Exception
     */
    public long insertBatchData2(String table, List<HashMap<String, Object>> list, String[] args) throws Exception{
        sqLiteDatabase.beginTransaction();
        long resultNum = 0;
        ContentValues values = new ContentValues();
        for(int i = 0; i < list.size(); i++){
            for(int j = 0; j < args.length; j++){
                values.put(args[j], list.get(i).get(args[j]).toString());
            }
            long num = insertData(table, values);
            resultNum += num;
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        return resultNum;
    }

    /**
     *
     * @param sql 执行更新操作的SQL语句
     * @param bindArgs SQL语句中的参数，参数的顺序对应占位符的顺序
     * @throws Exception
     */
    public void updateDataBySql(String sql, String[] bindArgs) throws Exception{
        if(sqLiteDatabase.isOpen()){
            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sql);
            if(bindArgs != null){
                int size = bindArgs.length;
                for(int i = 0; i < size; i++){
                    sqLiteStatement.bindString(i + 1, bindArgs[i]);
                }
                sqLiteStatement.execute();
                sqLiteStatement.close();
            }
        }
    }

    /**
     *
     * @param table 表明
     * @param values 表示更新的数据
     * @param whereClause 表示SQL语句中条件的部分语句
     * @param whereArgs 表示占位符的值
     * @return
     * @throws Exception
     */
    public int updateData(String table, ContentValues values, String whereClause, String[] whereArgs) throws Exception{
        int result = 0;
        if(sqLiteDatabase.isOpen()){
            result = sqLiteDatabase.update(table, values, whereClause, whereArgs);
        }else{
            throw new RuntimeException("The database is closed!");
        }
        return result;
    }

    /**
     *
     * @param sql 执行删除操作的SQL语句
     * @param bindArgs SQL语句中的参数，参数的顺序对应占位符的顺序
     */
    public void deleteDataBySql(String sql, String[] bindArgs){
        if(sqLiteDatabase.isOpen()){
            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sql);
            if(bindArgs != null){
                int size = bindArgs.length;
                for(int i = 0; i < size; i++){
                    sqLiteStatement.bindString(i + 1, bindArgs[i]);
                }
                sqLiteStatement.execute();
                sqLiteStatement.close();
            }
        }else{
            throw new RuntimeException("The database is closed!");
        }
    }

    /**
     *
     * @param table 表名
     * @param whereClause  表示SQL语句中条件的部分语句
     * @param whereArgs 表示占位符的值
     * @return
     * @throws Exception
     */
    public int deleteData(String table, String whereClause, String[] whereArgs) throws Exception{
        int result = 0;
        if(sqLiteDatabase.isOpen()){
            result = sqLiteDatabase.delete(table, whereClause, whereArgs);
        }else{
            throw new RuntimeException("The database is closed!");
        }
        return result;
    }

    /**
     * 查询数据
     * @param sql
     * @param selectionArgs
     * @return
     * @throws Exception
     */
    public Cursor queryData2Cursor(String sql, String[] selectionArgs) throws Exception{
        Cursor cursor = null;
        if(sqLiteDatabase.isOpen()){
            cursor = sqLiteDatabase.rawQuery(sql, selectionArgs);
        }else{
            throw new RuntimeException("The database is closed!");
        }
        return cursor;
    }

    /**
     *
     * @param table 表名
     * @param colums 查询需要返回的字段
     * @param selection sql语句中的条件语句
     * @param selectionArgs 占位符的值
     * @param groupBy 表示分组，可以为null
     * @param having sql好语句中的having ，可以为null
     * @param orderBy 表示结果的排序，可以为null
     * @return
     * @throws Exception
     */

    public Cursor queryData(String table, String[] colums, String selection,
                            String[] selectionArgs, String groupBy, String having, String orderBy) throws Exception{
        return null;
    }

    /**
     *
     * @param table 表名
     * @param colums 查询需要返回的字段
     * @param selection sql语句中的条件语句
     * @param selectionArgs 占位符的值
     * @param gourpBy 表示分组，可以为null
     * @param having sql语句中的having 可以为null
     * @param orderBy 表示结果的排序，可以为null
     * @param limit 表示分页，组拼完整的sql语句
     * @return
     */
    public Cursor queryData(String table, String[] colums, String selection,
                            String[] selectionArgs, String gourpBy, String having, String orderBy, String limit){
        return null;
    }

    public Cursor queryData(boolean distinct, String table, String[] colums, String selection,
                            String[] selectionArgs, String groupBy, String having, String orderBy,
                            String limit) throws Exception{
        return null;
    }

    public Cursor queryData(SQLiteDatabase.CursorFactory cursorFactory, boolean distinct, String table,
                            String[] colums, String selection, String[] selectionArgs, String groupBy, String having,
                            String orderBy, String limit) throws Exception{
        return null;
    }

    /**
     *
     * @param sql 执行查询操作的SQL语句
     * @param selectionArgs 查询条件
     * @param object 封装的Object对象，就是java bean
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> query2ListMap(String sql, String[] selectionArgs, Object object) throws Exception{
        List<Map<String, Object>> mapList = new ArrayList<>();
        if(sqLiteDatabase.isOpen()){
            Cursor cursor = null;
            cursor = sqLiteDatabase.rawQuery(sql, selectionArgs);
            Field[] f;
            HashMap<String, Object> map;
            if(cursor != null && cursor.getCount() > 0){
                while(cursor.moveToNext()){
                    map = new HashMap<>();
                    f = object.getClass().getDeclaredFields();
                    for(int i = 0; i < f.length; i++){
                        map.put(f[i].getName(), cursor.getString(cursor.getColumnIndex(f[i].getName())));
                    }
                    mapList.add(map);
                }
            }
            cursor.close();
        }else{
            throw new RuntimeException("The database is closed!");
        }
        return mapList;
    }

    public Map<String, Object> queryFirst2Map(String sql, String[] selectionArgs, Object object) throws  Exception{
        HashMap<String, Object> map = null;
        if(sqLiteDatabase.isOpen()){
            Cursor cursor = null;
            cursor = sqLiteDatabase.rawQuery(sql, selectionArgs);
            Field[] f;
            if(cursor != null && cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    map = new HashMap<>();
                    f = object.getClass().getDeclaredFields();
                    for(int i = 0; i < f.length; i++){
                        map.put(f[i].getName(), cursor.getString(cursor.getColumnIndex(f[i].getName())));
                    }
                }
            }
            cursor.close();
        }else{
            throw new RuntimeException("The database is closed!");
        }
        return map;
    }



}
