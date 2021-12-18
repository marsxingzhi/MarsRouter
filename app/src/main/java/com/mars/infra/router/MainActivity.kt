package com.mars.infra.router

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mars.infra.router.api.RouterUri

@RouterUri(module = "main", path = "/main/page")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}