package com.huytran.rermandroid.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.base.BaseActivity
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.di.scope.ApplicationContext
import com.kinda.alert.KAlertDialog
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LauncherActivity : BaseActivity() {

    @Inject lateinit var privatePreferences: SharedPreferences
    @Inject lateinit var userController: UserController
    @Inject @ApplicationContext lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        val token = privatePreferences.getString("session", null)
        if (token != null) {

            val loadingDialog = KAlertDialog(this, KAlertDialog.PROGRESS_TYPE)
            loadingDialog
                .setTitleText("Login")
                .setCancelable(false)

            userController.loginWithToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    loadingDialog.show()
                    disposableContainer.add(it)
                }
                .doOnTerminate {
                    loadingDialog.dismiss()
                }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        startActivitySilently(MainActivity::class.java)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, e.message ?: "Login Fail", Toast.LENGTH_SHORT).show()
                        startActivitySilently(LoginActivity::class.java)
                    }

                })
        } else {
            startActivitySilently(LoginActivity::class.java)
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

}