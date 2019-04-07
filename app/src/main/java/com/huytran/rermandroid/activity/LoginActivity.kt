package com.huytran.rermandroid.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity @Inject constructor() : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener{
            val loginStatus = true
            if(loginStatus == true){
                val toast = Toast.makeText(this, "Login success", Toast.LENGTH_LONG)
                toast.show()
                val intentMain = Intent(this, MainActivity::class.java)
                startActivity(intentMain)
            }else{
                val toast = Toast.makeText(this, "Login fail", Toast.LENGTH_LONG)
                toast.show()
            }
        }

        btnSignup.setOnClickListener {
            val intentSignUpActivity = Intent(this, SignUpActivity::class.java)
            startActivity(intentSignUpActivity)
        }
    }

}