package com.mars.infra.router

import android.app.Application

/**
 * Created by JohnnySwordMan on 2022/1/7
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Router.init(this)
    }
}