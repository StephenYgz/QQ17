package com.itheima.qq17.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 作者： itheima
 * 时间：2016-12-02 15:48
 * 网址：http://www.itheima.com
 */

public class ContactSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String CONTACTS_DB = "contacts.db";
    public static final int VERSION = 1;

    private ContactSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public ContactSQLiteOpenHelper(Context context){
        this(context, CONTACTS_DB,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table t_contact (_id integer primary key,username varchar(20),contact varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
