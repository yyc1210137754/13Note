package com.example.kotlinnote.du

import android.content.Context
import cn.dujc.core.initializer.permission.IPermissionSetup
import cn.dujc.core.permission.IOddsPermissionOperator
import cn.dujc.core.permission.OddsPermissionFuckImpl
import cn.dujc.core.ui.IBaseUI.IPermissionKeeper

class PermissionSetup : IPermissionSetup {
    override fun getOddsPermissionOperator(
        context: Context,
        permissionKeeper: IPermissionKeeper
    ): IOddsPermissionOperator {
        return OddsPermissionFuckImpl(context, permissionKeeper)
    }
}