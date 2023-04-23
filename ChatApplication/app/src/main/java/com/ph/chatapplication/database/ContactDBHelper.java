package com.ph.chatapplication.database;

import static com.ph.chatapplication.activity.adapter.ContactFragmentAdapter.DataHolder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

// 联系人页面数据库
public class ContactDBHelper extends SQLiteOpenHelper {
    private static final String DB_Name = "contact.db";
    private static final int DB_Version = 2;
    private static final String Table_Name = "contact_info";
    private static ContactDBHelper mHelper = null;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private ContactDBHelper(Context context){
        super(context, DB_Name, null, DB_Version);
    }

    public static ContactDBHelper getInstance(Context context){
        if (mHelper == null){
            mHelper = new ContactDBHelper(context);
        }
        return mHelper;
    }



    // 打开数据库的读链接
    public SQLiteDatabase openReadLink(){
        if (mRDB == null || !mRDB.isOpen()){
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    // 打开数据库的写链接
    public SQLiteDatabase openWriteLink(){
        if (mWDB == null || !mWDB.isOpen()){
            mWDB = mHelper.getWritableDatabase();
        }
        return mWDB;
    }

    // 关闭数据库
    public void closeLink(){
        if (mRDB != null && mRDB.isOpen()){
            mRDB.close();
            mRDB = null;
        }

        if (mWDB != null && mWDB.isOpen()){
            mWDB.close();
            mWDB = null;
        }
    }

    // 创建数据库，执行建表语句
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql ="CREATE TABLE IF NOT EXISTS contact_info (" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "nickname VARCHAR NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 插入指定id数据
    public long insert(DataHolder contact){
        ContentValues values = new ContentValues();
        values.put("userId",contact.userId);
        //values.put("potrait", contact.portrait.toString());
        values.put("nickname",contact.nickName);
        return mWDB.insert(Table_Name, null, values);
    }

    // 删除指定id数据
    public long deleteByName(String userId){
        return mWDB.delete(Table_Name, "name=?", new String[]{userId});
    }


    // 删除全部数据
    public long deleteAll(String Table_Name){
        return mWDB.delete(Table_Name, "1=1", null);
    }

    // 更新数据库
    public long update(DataHolder contact){
        ContentValues values = new ContentValues();
        values.put("userId",contact.userId);
        //values.put("potrait", contact.portrait.toString());
        values.put("nickname",contact.nickName);
        return mWDB.update(Table_Name, values, "userId=?", new String[]{String.valueOf(contact.userId)});
    }

    // 查询数据库全部数据
    public List<DataHolder> queryAll() throws ParseException {
        List<DataHolder> list = new ArrayList<>();
        // 返回记录查询动作，返回结果集游标
        Cursor cursor = mRDB.query(Table_Name, null, null, null, null, null, null);

        // 循环取出游标指向的每条记录
        while (cursor.moveToNext()){
            DataHolder contatc = new DataHolder();
            contatc.userId = cursor.getInt(0);
            //contatc.portrait = cursor.getInt(1);
            contatc.nickName = String.valueOf(cursor.getInt(1));
            list.add(contatc);
        }

        return list;
    }
}
