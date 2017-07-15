package com.itheima.qq17.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-02 15:51
 * 网址：http://www.itheima.com
 */

public class DBUtils {
    private static Context sContext;
    public static void init(Context context) {
        sContext = context;
    }

    /**
     *  将username的所有的联系人删除掉，然后重新插入
     * @param username
     * @param contacts
     */
    public static void updateContacts(String username, List<String> contacts){
        if (sContext==null){
            throw  new RuntimeException("DBUtils使用前必须初始化！");
        }
        ContactSQLiteOpenHelper openHelper = new ContactSQLiteOpenHelper(sContext);
        SQLiteDatabase writableDatabase = openHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        /**
         * sql： delete from t_contact where username=?
         * 参数1：表名
         * 参数2：where表达式
         * 参数3：用于替换where表达式中？号的真是的参数
         */
       writableDatabase.delete("t_contact","username=?",new String[]{username});
        ContentValues values = new ContentValues();
        values.put("username",username);
        for (String contact : contacts) {
            values.put("contact",contact);
            writableDatabase.insert("t_contact",null,values);
        }
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        //释放数据库资源
        writableDatabase.close();
    }

    /**
     * SQL:select contact from t_contact where username=? order by contact
     * 返回当前用户的所有的联系人
     * @param username
     * @return
     */
    public static List<String> getContacts(String username){
        if (sContext==null){
            throw  new RuntimeException("DBUtils使用前必须初始化！");
        }
        List<String> contacts = new ArrayList<>();
        ContactSQLiteOpenHelper openHelper = new ContactSQLiteOpenHelper(sContext);
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query("t_contact", new String[]{"contact"}, "username=?", new String[]{username}, null, null, "contact");
        while(cursor!=null&&cursor.moveToNext()){
            String contact = cursor.getString(0);
            contacts.add(contact);
        }
        /**
         * 释放
         */
        cursor.close();
        database.close();
        return contacts;
    }
}
