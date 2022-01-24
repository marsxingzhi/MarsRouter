package com.mars.infra.router

import android.util.Log
import com.mars.infra.router.api.ServiceImpl

/**
 * Created by JohnnySwordMan on 2022/1/22
 */
@ServiceImpl(service = [IAccountService::class], singleton = false)
class AccountServiceImpl : IAccountService {

    override fun getUser(): String {
        return "${AccountServiceImpl::class.java.simpleName}: User".also {
            Log.e("mars", "成功调用AccountServiceImpl的getUser方法---☺️")
        }
    }
}