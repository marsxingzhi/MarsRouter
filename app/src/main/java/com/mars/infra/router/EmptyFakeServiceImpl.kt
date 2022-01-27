package com.mars.infra.router

import android.util.Log
import com.mars.infra.router.api.DowngradeImpl

/**
 * Created by JohnnySwordMan on 2022/1/27
 */
@DowngradeImpl(service = [IFakeService::class], singleton = false, isForceDowngrade = false)
class EmptyFakeServiceImpl: IFakeService {

    override fun fakeTest() {
        Log.e("mars", "EmptyFakeServiceImpl 走的降级处理️")
        Log.e("mars", "EmptyFakeServiceImpl---成功调用 fakeTest---☺️")
    }
}