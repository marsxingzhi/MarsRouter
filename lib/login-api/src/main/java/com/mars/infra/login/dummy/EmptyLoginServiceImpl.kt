package com.mars.infra.login.dummy

import android.util.Log
import com.mars.infra.login.api.ILoginService
import com.mars.infra.router.api.DowngradeImpl

/**
 * Created by Mars on 2022/5/30
 */
@DowngradeImpl(service = [ILoginService::class], singleton = false, isForceDowngrade = false)
class EmptyLoginServiceImpl: ILoginService {

    override fun login() {
        Log.e("mars", "EmptyFakeServiceImpl 降级登录")
    }
}