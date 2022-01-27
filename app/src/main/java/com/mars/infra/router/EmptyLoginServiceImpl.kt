package com.mars.infra.router

import android.util.Log
import com.mars.infra.router.api.DowngradeImpl

/**
 * Created by JohnnySwordMan on 2022/1/27
 */
@DowngradeImpl(service = [ILoginService::class], singleton = false, isForceDowngrade = false)
class EmptyLoginServiceImpl: ILoginService {

    override fun login() {
        Log.e("mars", "EmptyFakeServiceImpl 降级登录")
    }
}