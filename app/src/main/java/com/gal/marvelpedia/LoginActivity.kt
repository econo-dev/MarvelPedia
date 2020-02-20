package com.gal.marvelpedia

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var usersDBHelper: UsersDBHelper

    open var isLogged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setPointer()
    }

    private fun setPointer() {
        usersDBHelper = UsersDBHelper(this)
        btnLoginCancel.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id){
            R.id.btnLoginCancel -> finish()
            R.id.btnLogin -> {
                //TODO mock data for user. Need to change readUser Method userid:66b25a27-a34d-4bb0-bfe8-7eecccf18703
                val user: ArrayList<UserModel> = usersDBHelper.readUser(txtLoginUser.text.toString())
//                Log.e(" USER ", user[0].name+"@ "+user[0].password+" # "+user[0].userId)
                if ( txtLoginUser.text.isEmpty() || txtLoginPass.text.isEmpty() ){
                    Toast.makeText(this, "Check user name or password", Toast.LENGTH_SHORT).show()
                    return}
                if (user.isEmpty()) {
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                    return
                }
                if (user[0].name == txtLoginUser.text.toString() && user[0].password == txtLoginPass.text.toString() ){
                    Toast.makeText(this, "User ${user[0].name} Logged-in Successfully", Toast.LENGTH_SHORT).show()
                    isLogged = true
                    finish()
                } else Toast.makeText(this, "Check user name or password", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
