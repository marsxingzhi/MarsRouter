package com.mars.infra.router

import android.app.Activity
import android.os.Bundle
import com.mars.infra.router.api.RouterUri

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
@RouterUri(module = "login", path = "/login")
class LoginActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}