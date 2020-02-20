package com.gal.marvelpedia

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity(), UserAble {

    lateinit var usersDBHelper: UsersDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usersDBHelper = UsersDBHelper(this)
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

        var userId = UUID.randomUUID().toString()
        Log.e("user_UUID ", userId)
        var result = usersDBHelper.insertUser(UserModel(userId, userName, password1))
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
