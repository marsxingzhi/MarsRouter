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
class LoginActivity: AppCompatActivity() {

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
        val loginService = Router.getService(ILoginService::class.java)
        loginService?.login()

        Router.getService(IFakeService::class.java)?.fakeTest()

        Router.getService(IAccountService::class.java)?.getUser()?.also {
            Log.e("mars", "成功调用AccountServiceImpl的getUser方法")
        }
    }
}