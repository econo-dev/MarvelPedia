package com.gal.marvelpedia

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // upgrade policy - discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertUser(user: UserModel): Boolean {
        //access data in write mode
        val db = writableDatabase

        //create a new map of values, the column names are the keys
        val values = ContentValues()
//        values.put(DBContract.UserEntry.COLUMN_USER_ID, user.id)
        values.put(DBContract.UserEntry.COLUMN_NAME, user.name)
        values.put(DBContract.UserEntry.COLUMN_PASSWORD, user.password)

        //insert new row, returning the primary key value of the row
        val newRowId = db.insert(DBContract.UserEntry.TABLE_NAME, null, values)
        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUser(userid: String): Boolean {
        //access the data in write mode
        val db = writableDatabase

        // 'where' clause query
        val selection = DBContract.UserEntry.COLUMN_USER_ID + " LIKE ?"
        //specify the argument in placeholder order
        val selectionArgs = arrayOf(userid)
        //SQL Statement issue
        db.delete(DBContract.UserEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun readUser(userName: String): ArrayList<UserModel> {
        val users = ArrayList<UserModel>()
        val db = writableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.UserEntry.TABLE_NAME + " WHERE " + DBContract.UserEntry.COLUMN_NAME + "='" + userName +"'", null)
        } catch (e: SQLException){
            Log.e("SQL_readUser ", e.message)
            //if table not created, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var id: Int
        var name: String
        var password: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
                name = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_NAME))
                password = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_PASSWORD))

//                users.add(UserModel(id , name, password))
                cursor.moveToNext()
            }
        }
        return users
    }

    fun readAllUsers(): ArrayList<UserModel> {
        val users = ArrayList<UserModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_NAME, null)
        } catch (e: SQLException) {
            Log.e("SQL_readAllUsers ", e.message)
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var userid: Int
        var name: String
        var password: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                userid = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
                name = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_NAME))
                password = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_PASSWORD))

//                users.add(UserModel(userid, name, password))
                cursor.moveToNext()
            }
        }
        return users
    }

    companion object {
        //changing db schema requires incremting the database version
        val DATABASE_VERSION = 2
        val DATABASE_NAME = "MarvelUsers.db"

        private val SQL_CREATE_ENTRIES = "CREATE TABLE " + DBContract.UserEntry.TABLE_NAME + " (" + DBContract.UserEntry.COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBContract.UserEntry.COLUMN_NAME + " TEXT," + DBContract.UserEntry.COLUMN_PASSWORD + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_NAME
    }
}