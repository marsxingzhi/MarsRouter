package com.mars.infra.router

import android.util.Log
import com.mars.infra.router.api.ServiceImpl

/**
 * Created by JohnnySwordMan on 2022/1/22
 */
@ServiceImpl(service = [IFakeService::class], singleton = false)
class FakeServiceImpl: IFakeService {

    override fun fakeTest() {
        Log.e("mars", "FakeServiceImpl---成功调用 fakeTest")
    }
}