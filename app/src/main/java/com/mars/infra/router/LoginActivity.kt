package com.mars.infra.router

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mars.infra.router.api.RouterUri
import com.mars.infra.router.runtime.Router

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
@RouterUri(module = "login", path = "/login")
class LoginActivity : AppCompatActivity() {

    lateinit var mBtnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mBtnLogin = findViewById(R.id.btn_login)

        mBtnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        Router.getService(ILoginService::class.java)?.also {
            Log.e("mars", "ILoginService = $it")
        }?.login()

        Router.getService(IFakeService::class.java)?.also {
            Log.e("mars", "IFakeService = $it")
        }?.fakeTest()

        Router.getService(IAccountService::class.java)?.also {
            Log.e("mars", "IAccountService = $it")
        }?.getUser()
    }
}