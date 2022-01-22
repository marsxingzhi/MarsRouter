package com.mars.infra.router

import com.mars.infra.router.api.ServiceImpl

/**
 * Created by JohnnySwordMan on 2022/1/22
 */
@ServiceImpl(service = [IAccountService::class], singleton = false)
class AccountServiceImpl : IAccountService {

    override fun getUser(): String {
        return "${AccountServiceImpl::class.java.simpleName}: User"
    }
}