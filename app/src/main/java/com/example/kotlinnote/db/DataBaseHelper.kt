package com.example.kotlinnote.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    companion object {
        val DATABASE_NAME: String = "note.db"
        val DATABASE_VERSION: Int = 1
        var mSQLiteDB: SQLiteDB? = null;
    }

    override fun onCreate(db: SQLiteDatabase?) {
        var sql = "create table note(note_id INTEGER PRIMARY KEY AUTOINCREMENT ,title varchar(20)" +
                ",content TEXT,add_time DATETIME,update_time DATETIME,note_code TEXT)"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
    }

}