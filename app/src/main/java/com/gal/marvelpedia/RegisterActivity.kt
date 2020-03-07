package com.gal.marvelpedia

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), UserAble {

    private var databaseHelper: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)
        setPointer()
    }

    private fun setPointer() {
        btnReg.setOnClickListener { addNewUser(txtName.text.toString(), txtPassword1.text.toString(), txtPassword2.text.toString()) }

        btnRegCancel.setOnClickListener { finish() }
    }

    override fun addNewUser(userName: String, password1: String, password2: String) {

        if (password1 != password2 || password1.length<3 && password2.length<3 ){
            Toast.makeText(this, "Check Passwords", Toast.LENGTH_SHORT).show()
            return
        }
        if (userName.length<3){
            Toast.makeText(this, "User Name must be at least 3 letters", Toast.LENGTH_SHORT).show()
            return
        }

//        val values = ContentValues()
//        values.put(DBContract.UserEntry.COLUMN_NAME, userName)
//        values.put(DBContract.UserEntry.COLUMN_PASSWORD, password1)
//        val userId = SQLiteDatabaseHelper.writableDatabase.insertWithOnConflict(DBContract.UserEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE)
//        Log.e("user_UUID ", userId.toString())
//        var result = SQLiteDatabaseHelper. .insertUser(UserModel(userId.toInt(), userName, password1))
        databaseHelper!!.addUser(userName, password1)
        Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()

        //clear the EditText
        this.txtName.text.clear()
        this.txtPassword1.text.clear()
        this.txtPassword2.text.clear()

    }

    override fun isUserExists(userName: String) {

    }

    override fun deleteUser(userName: String) {

    }


}
