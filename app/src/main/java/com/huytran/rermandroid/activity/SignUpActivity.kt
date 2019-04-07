package com.huytran.rermandroid.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.view.*
import javax.inject.Inject

class SignUpActivity @Inject constructor() : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btnConfirm.setOnClickListener{
            val toast = Toast.makeText(this, "Sign Up success", Toast.LENGTH_LONG)
            toast.show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        tv_confirm.setOnClickListener {
            val toast = Toast.makeText(this, "Sign Up success", Toast.LENGTH_LONG)
            toast.show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}
