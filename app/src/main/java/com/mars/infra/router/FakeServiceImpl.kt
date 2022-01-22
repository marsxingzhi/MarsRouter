package com.mars.infra.router

import com.mars.infra.router.api.ServiceImpl

/**
 * Created by JohnnySwordMan on 2022/1/22
 */
@ServiceImpl(service = [IFakeService::class], singleton = false)
class FakeServiceImpl: IFakeService {

    override fun fakeTest() {
    }
}