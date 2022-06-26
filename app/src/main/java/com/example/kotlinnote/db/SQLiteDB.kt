package com.example.kotlinnote.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.kotlinnote.entity.NoteBean
import java.util.*

class SQLiteDB constructor(private val context: Context) {

    init {
        var dataBaseHelper = DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
        mSQLiteDatabase = dataBaseHelper.readableDatabase
    }

    companion object {
        private val DATABASE_NAME: String = "note.db";
        private val DATABASE_VERSION = 1;
        private var mSQLiteDB: SQLiteDB? = null
        private var mSQLiteDatabase: SQLiteDatabase? = null


        fun getInstance(context: Context): SQLiteDB? {
            if (mSQLiteDB == null) {
                mSQLiteDB = SQLiteDB(context)
            }
            return mSQLiteDB
        }
    }

    fun addNote(noteBean: NoteBean): Boolean {
        var flag: Boolean = false
        var contentValues: ContentValues = ContentValues()
        contentValues.put("title", noteBean.title)
        contentValues.put("content", noteBean.content)
        contentValues.put("add_time", noteBean.add_time)
        contentValues.put("update_time", noteBean.update_time)
        contentValues.put("note_code", noteBean.note_code)
        var insert: Long? = mSQLiteDatabase?.insert("note", null, contentValues)
        var b = insert ?: 0 > 0
        if (b) {
            flag = true
        }
        return flag
    }

    fun updateNote(noteBean: NoteBean): Boolean {
        var flag = false
        var contentValues = ContentValues()
        contentValues.put("title", noteBean.title)
        contentValues.put("content", noteBean.content)
        contentValues.put("update_time", noteBean.update_time)
        contentValues.put("note_code", noteBean.note_code)
        var update = mSQLiteDatabase?.update(
            "note", contentValues, "note_id=?",
            arrayOf(noteBean.note_id)
        )
        if (update ?: 0 > 0) {
            flag = true
        }
        return flag
    }

    fun delNote(id: String): Boolean {
        var flag = false
        var delete = mSQLiteDatabase?.delete("note", "note_id=?", arrayOf(id))
        if (delete ?: 0 > 0) {
            flag = true
        }
        return flag
    }

    fun findAllData(orderBy: String): List<NoteBean>? {
        val noteBeans: MutableList<NoteBean> = ArrayList()
        val cursor = mSQLiteDatabase?.query(
            "note",
            arrayOf(
                "note_id",
                "title",
                "content",
                "add_time",
                "update_time",
                "note_code"
            ),
            null,
            null,
            null,
            null,
            orderBy + if ("title" == orderBy) " asc" else " desc"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val noteBean = NoteBean()
                noteBean.note_id = cursor.getString(cursor.getColumnIndex("note_id"))
                noteBean.title = cursor.getString(cursor.getColumnIndex("title"))
                noteBean.content = cursor.getString(cursor.getColumnIndex("content"))
                noteBean.add_time = cursor.getString(cursor.getColumnIndex("add_time"))
                noteBean.update_time = cursor.getString(cursor.getColumnIndex("update_time"))
                noteBean.note_code = cursor.getString(cursor.getColumnIndex("note_code"))
                noteBeans.add(noteBean)
            }
        }
        return noteBeans
    }

    fun findDataByContent(
        content: String,
        orderBy: String
    ): List<NoteBean>? {
        val noteBeans: MutableList<NoteBean> = ArrayList()
        val cursor = mSQLiteDatabase?.query(
            "note",
            arrayOf(
                "note_id",
                "title",
                "content",
                "add_time",
                "update_time",
                "note_code"
            ),
            "content like '%$content%' or title like '%$content%'",
            null,
            null,
            null,
            orderBy + if ("title" == orderBy) " asc" else " desc"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val noteBean = NoteBean()
                noteBean.note_id = cursor.getString(cursor.getColumnIndex("note_id"))
                noteBean.title = cursor.getString(cursor.getColumnIndex("title"))
                noteBean.content = cursor.getString(cursor.getColumnIndex("content"))
                noteBean.add_time = cursor.getString(cursor.getColumnIndex("add_time"))
                noteBean.update_time = cursor.getString(cursor.getColumnIndex("update_time"))
                noteBean.note_code = cursor.getString(cursor.getColumnIndex("note_code"))
                noteBeans.add(noteBean)
            }
        }
        return noteBeans
    }

    fun findDataByCode(code: String): NoteBean? {
        val noteBean = NoteBean()
        val cursor = mSQLiteDatabase?.rawQuery(
            "select *  from note where note_code=?",
            arrayOf(code)
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                noteBean.note_id = cursor.getString(cursor.getColumnIndex("note_id"))
                noteBean.title = cursor.getString(cursor.getColumnIndex("title"))
                noteBean.content = cursor.getString(cursor.getColumnIndex("content"))
                noteBean.add_time = cursor.getString(cursor.getColumnIndex("add_time"))
                noteBean.update_time = cursor.getString(cursor.getColumnIndex("update_time"))
                noteBean.note_code = cursor.getString(cursor.getColumnIndex("note_code"))
            }
        }
        return noteBean
    }

}