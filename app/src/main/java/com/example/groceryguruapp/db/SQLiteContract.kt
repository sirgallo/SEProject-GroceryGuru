package com.example.groceryguruapp.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import java.util.ArrayList

class SQLiteContract(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertUser(user: DbModels.User): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DbSchema.UserEntry.COLUMN_USER_ID, user.userid)
        values.put(DbSchema.UserEntry.COLUMN_USER_FIRST, user.userfirst)
        values.put(DbSchema.UserEntry.COLUMN_USER_LAST, user.userlast)
        values.put(DbSchema.UserEntry.COLUMN_USER_LISTS, user.userlists)

        val newRowId = db.insert(DbSchema.UserEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun insertList(userlist: DbModels.UserList): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DbSchema.ListEntry.COLUMN_LIST_ID, userlist.listid)
        values.put(DbSchema.ListEntry.COLUMN_LIST_NAME, userlist.listname)
        values.put(DbSchema.ListEntry.COLUMN_LIST, userlist.list)

        val newRowId = db.insert(DbSchema.ListEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun insertItem(item: DbModels.Item): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DbSchema.ItemEntry.COLUMN_ITEM_ID, item.itemid)
        values.put(DbSchema.ItemEntry.COLUMN_ITEM_NAME, item.itemname)
        values.put(DbSchema.ItemEntry.COLUMN_ITEM_CATEGORY, item.itemcategory)

        val newRowId = db.insert(DbSchema.ItemEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteList(listid: Long): Boolean {
        val db = writableDatabase

        val selection = DbSchema.ListEntry.COLUMN_LIST_ID + " LIKE ?"
        val selectionArgs = arrayOf(listid.toString())

        db.delete(DbSchema.UserEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun readUser(userid: Long): ArrayList<DbModels.User> {
        val users = ArrayList<DbModels.User>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DbSchema.UserEntry.TABLE_NAME + " where " +
                    DbSchema.UserEntry.COLUMN_USER_ID + " = '" + userid + "'", null)
        } catch(e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var first: String
        var last: String
        var userlists: ByteArray

        if (cursor!!.moveToFirst()) {
            while(!cursor.isAfterLast) {
                first = cursor.getString(cursor.getColumnIndex(DbSchema.UserEntry.COLUMN_USER_FIRST))
                last = cursor.getString(cursor.getColumnIndex(DbSchema.UserEntry.COLUMN_USER_LAST))
                userlists = cursor.getBlob(cursor.getColumnIndex(DbSchema.UserEntry.COLUMN_USER_LISTS))

                users.add(DbModels.User(userid, first, last, userlists))
                cursor.moveToNext()
            }
        }
        return users
    }

    fun readList(listid: Long): ArrayList<DbModels.UserList> {
        val lists = ArrayList<DbModels.UserList>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DbSchema.ListEntry.TABLE_NAME + " where " +
                    DbSchema.ListEntry.COLUMN_LIST_ID + " = '" + listid + "'", null)
        } catch(e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var listname: String
        var userlist: ByteArray

        if (cursor!!.moveToFirst()) {
            while(!cursor.isAfterLast) {
                listname = cursor.getString(cursor.getColumnIndex(DbSchema.ListEntry.COLUMN_LIST_NAME))
                userlist = cursor.getBlob(cursor.getColumnIndex(DbSchema.ListEntry.COLUMN_LIST))


                lists.add(DbModels.UserList(listid, listname, userlist))
                cursor.moveToNext()
            }
        }
        return lists
    }

    fun readAllLists(): ArrayList<DbModels.UserList> {
        val lists = ArrayList<DbModels.UserList>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DbSchema.ListEntry.TABLE_NAME, null)
        } catch(e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var listid: Long
        var listname: String
        var userlist: ByteArray

        if (cursor!!.moveToFirst()) {
            while(!cursor.isAfterLast) {
                listid = cursor.getLong(cursor.getColumnIndex((DbSchema.ListEntry.COLUMN_LIST_ID)))
                listname = cursor.getString(cursor.getColumnIndex(DbSchema.ListEntry.COLUMN_LIST_NAME))
                userlist = cursor.getBlob(cursor.getColumnIndex(DbSchema.ListEntry.COLUMN_LIST))


                lists.add(DbModels.UserList(listid, listname, userlist))
                cursor.moveToNext()
            }
        }
        return lists
    }

    fun readItem(itemid: Long): ArrayList<DbModels.Item> {
        val items = ArrayList<DbModels.Item>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DbSchema.ItemEntry.TABLE_NAME + " where " +
                    DbSchema.ItemEntry.COLUMN_ITEM_ID + " = '" + itemid + "'", null)
        } catch(e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var name: String
        var category: String

        if (cursor!!.moveToFirst()) {
            while(!cursor.isAfterLast) {
                name = cursor.getString(cursor.getColumnIndex(DbSchema.ItemEntry.COLUMN_ITEM_NAME))
                category = cursor.getString(cursor.getColumnIndex(DbSchema.ItemEntry.COLUMN_ITEM_CATEGORY))


                items.add(DbModels.Item(itemid, name, category))
                cursor.moveToNext()
            }
        }
        return items
    }

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FeedReader.db"

        private val SQL_CREATE_ENTRIES =
            "create table " + DbSchema.UserEntry.TABLE_NAME + " (" +
                    DbSchema.UserEntry.COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                    DbSchema.UserEntry.COLUMN_USER_FIRST + " TEXT, " +
                    DbSchema.UserEntry.COLUMN_USER_LAST + " TEXT, " +
                    DbSchema.UserEntry.COLUMN_USER_LISTS + " BLOB);" +
                    "create table " + DbSchema.ListEntry.TABLE_NAME + " (" +
                    DbSchema.ListEntry.COLUMN_LIST_ID + " TEXT PRIMARY KEY, " +
                    DbSchema.ListEntry.COLUMN_LIST_NAME + " TEXT, " +
                    DbSchema.ListEntry.COLUMN_LIST + " BLOB);" +
                    "create table " + DbSchema.ItemEntry.TABLE_NAME + " (" +
                    DbSchema.ItemEntry.COLUMN_ITEM_ID + " TEXT PRIMARY KEY, " +
                    DbSchema.ItemEntry.COLUMN_ITEM_NAME + " TEXT, " +
                    DbSchema.ItemEntry.COLUMN_ITEM_CATEGORY + "TEXT);"

        private val SQL_DELETE_ENTRIES = "drop table if exists " + DbSchema.UserEntry.TABLE_NAME + ";" +
                "drop table if exists " + DbSchema.ListEntry.TABLE_NAME + ";" +
                "drop table if exists " + DbSchema.ItemEntry.TABLE_NAME + ";"
    }
}