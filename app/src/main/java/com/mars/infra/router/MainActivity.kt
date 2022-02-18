package com.mars.infra.router

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.mars.infra.router.api.RouterUri
import com.mars.infra.router.api.UriRequest
import com.mars.infra.router.runtime.Router

@RouterUri(module = "main", path = "/main/page")
class MainActivity : AppCompatActivity() {

    lateinit var mBtnStartLogin: Button
    lateinit var mBtnStartLive: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtnStartLogin = findViewById(R.id.btn_start_login)
        mBtnStartLive = findViewById(R.id.btn_start_live)

        mBtnStartLogin.setOnClickListener {
//            Router.loadUri(this, "login", "/login")
            Router.loadUri(this, UriRequest().apply {
                uri = "/login"
                param1 = "张三"
                param2 = "123"
            })
        }

        mBtnStartLive.setOnClickListener {
            Router.loadUri(this, "live", "/live/main")
        }
    }
}