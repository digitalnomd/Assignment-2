package com.example.rsg.latitudelongitudeparser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nasif on 3/31/2016.
 */
public class DBHelperClass extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "applicationdata";
    private static final int DATABASE_VERSION = 1;

    public DBHelperClass(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL("create table location(id integer primary key autoincrement, timestamp text not null, latitude real not null, longitude real not null);");
    }

    public void onUpgrade(SQLiteDatabase database, int oldversion, int newversion)
    {
        database.execSQL("DROP TABLE IF EXISTS location");
        onCreate(database);
    }

}
