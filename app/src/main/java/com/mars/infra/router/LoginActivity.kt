package com.mars.infra.router

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.mars.infra.login.api.ILoginService
import com.mars.infra.router.api.Builder
import com.mars.infra.router.api.Inject
import com.mars.infra.router.api.RouterUri
import com.mars.infra.router.runtime.Router

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
@RouterUri(module = "login", path = "/login")
@Builder
class LoginActivity : Activity() {

    lateinit var mBtnLogin: Button
    lateinit var mTvUserName: TextView
    lateinit var mTvPassword: TextView

    @Inject
    lateinit var username: String

    @Inject
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mBtnLogin = findViewById(R.id.btn_login)
        mTvUserName = findViewById(R.id.tv_username)
        mTvPassword = findViewById(R.id.tv_password)

        mBtnLogin.setOnClickListener {
            login()
        }

        mTvUserName.text = username
        mTvPassword.text = password
    }

    private fun login() {
        Router.getService(ILoginService::class.java)?.also {
            Log.e("mars", "LoginActivity--->ILoginService = $it")
        }?.login()

        Router.getService(IFakeService::class.java)?.also {
            Log.e("mars", "LoginActivity--->IFakeService = $it")
        }?.fakeTest()

        Router.getService(IAccountService::class.java)?.also {
            Log.e("mars", "LoginActivity--->IAccountService = $it")
        }?.getUser()
    }
}