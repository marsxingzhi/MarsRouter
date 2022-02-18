package com.mars.infra.router

import android.app.Application
import com.mars.infra.router.runtime.Router
import com.mars.infra.router.runtime.builder.ActivityBuilder

/**
 * Created by JohnnySwordMan on 2022/1/7
 */
class RouterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Router.init(this)
        ActivityBuilder.init(this)
    }
}