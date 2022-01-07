package com.mars.infra.router

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.mars.infra.router.api.RouterUri

@RouterUri(module = "main", path = "/main/page")
class MainActivity : AppCompatActivity() {

    lateinit var mBtnStartLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtnStartLogin = findViewById(R.id.btn_start_login)

        mBtnStartLogin.setOnClickListener {
            Router.loadUri(this, "login", "/login")
        }
    }
}