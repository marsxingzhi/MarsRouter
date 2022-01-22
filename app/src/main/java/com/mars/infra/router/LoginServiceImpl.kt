package com.mars.infra.router

import android.util.Log
import com.mars.infra.router.api.ServiceImpl

/**
 * Created by JohnnySwordMan on 2022/1/17
 */
@ServiceImpl(service = [ILoginService::class], singleton = false)
class LoginServiceImpl : ILoginService {

    init {
        Log.e("mars", "插桩测试成功！！！")
    }
    override fun login() {
        Log.e("mars", "登录成功")
    }
}