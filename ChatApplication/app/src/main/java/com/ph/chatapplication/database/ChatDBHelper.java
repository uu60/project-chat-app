package com.ph.chatapplication.database;

import static com.ph.chatapplication.activity.adapter.MessageAdapter.DataHolder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

// 联系人页面数据库
public class ChatDBHelper extends SQLiteOpenHelper {

    private static final int DB_Version = 2;
    private static final String Table_Name = "history";
    private static ChatDBHelper mHelper = null;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

//    public ChatDBHelper(Context context){
//        super(context, DB_Name, null, DB_Version);
//    }

    public ChatDBHelper(Context context, String DB_Name) {
        super(context, DB_Name, null, DB_Version);
    }


//    public static ChatDBHelper getInstance(Context context){
//        if (mHelper == null){
//            mHelper = new ChatDBHelper(context);
//        }
//        return mHelper;
//    }

    public static ChatDBHelper getInstance(Context context, String DB_Name) {
        if (mHelper == null) {
            mHelper = new ChatDBHelper(context, DB_Name);
        }
        return mHelper;
    }


    // 打开数据库的读链接
    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    // 打开数据库的写链接
    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            try {
                mWDB = mHelper.getWritableDatabase();
            } catch (Exception e) {
                Log.e("mWDB", String.valueOf(e));
            }

        }
        return mWDB;
    }

    // 关闭数据库
    public void closeLink() {
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;
        }

        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }

    // 创建数据库，执行建表语句
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS history " +
                "(time varchar(20) primary key," +
                "text varchar(40)," +
                "userId integer)";
        db.execSQL(sql);
    }

//    public void createTable(SQLiteDatabase db, String Table_Name) {
//        String sql ="CREATE TABLE IF NOT EXISTS "+ Table_Name +" (" +
//                "time INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
//                "text VARCHAR NOT NULL);";
//        db.execSQL(sql);
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

//    public void selectTable(String Table_Name){
//        this.Table_Name = Table_Name;
//        SQLiteDatabase db = null;
//        createTable(db, Table_Name);
//    }

    // 插入指定id数据
    public void insert(DataHolder contact, Integer userId) throws ParseException {
//        ContentValues values = new ContentValues();
//        values.put("time",contact.time);
//        //values.put("potrait", contact.portrait.toString());
//        values.put("text",contact.text);
        if (queryOne(contact.time)) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            Object[] objects = new Object[3];
            objects[0] = contact.time;
            objects[1] = contact.text;
            objects[2] = userId;
            String sql = "insert into history(time, text, userId) values(?,?,?)";
            try {
                db.execSQL(sql, objects);
            } catch (Exception e) {
                Log.e("databaseinsert", String.valueOf(e));
            }
        }

    }

    // 删除指定id数据
    public long deleteByName(String userId) {
        return mWDB.delete(Table_Name, "name=?", new String[]{userId});
    }


    // 删除全部数据
    public long deleteAll(String Table_Name) {
        return mWDB.delete(Table_Name, "1=1", null);
    }

    // 更新数据库
    public long update(DataHolder contact, Integer userId) {
        ContentValues values = new ContentValues();
        values.put("time", contact.time);
        //values.put("potrait", contact.portrait.toString());
        values.put("text", contact.text);
        values.put("userId", userId);
        return mWDB.update(Table_Name, values, "time=?",
                new String[]{String.valueOf(contact.time)});
    }

    // 查询数据库全部数据
    public List<DataHolder> queryAll() throws ParseException {
        List<DataHolder> list = new ArrayList<>();
        // 返回记录查询动作，返回结果集游标
        Cursor cursor = mRDB.query(Table_Name, null, null, null, null, null, null);

        // 循环取出游标指向的每条记录
        while (cursor.moveToNext()) {
            DataHolder contatc = new DataHolder();
            contatc.time = cursor.getString(0);
            //contatc.portrait = cursor.getInt(1);
            contatc.text = cursor.getString(1);
            list.add(contatc);
        }

        return list;
    }

    public boolean queryOne(String time) throws ParseException {
        // 返回记录查询动作，返回结果集游标
        Cursor cursor = mRDB.query(Table_Name, null, null, null, null, null, null);
        Boolean out = true;
        Boolean balance = true;
        // 循环取出游标指向的每条记录
        try {
            while (cursor.moveToNext()) {
                balance = false;
                DataHolder contatc = new DataHolder();
                contatc.time = cursor.getString(0);
//            //contatc.portrait = cursor.getInt(1);
//            contatc.text = String.valueOf(cursor.getInt(1));
//            list.add(contatc);
                if (contatc.time.equals(time)) {
                    out = false;
                }
            }
        } catch (Exception e) {
            out = true;
            Log.e("database", String.valueOf(e));
        }
        return out || balance;
    }
}
