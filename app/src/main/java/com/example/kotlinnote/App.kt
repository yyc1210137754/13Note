package com.example.kotlinnote

import androidx.multidex.MultiDexApplication
import cn.dujc.core.app.Core
import com.example.kotlinnote.du.ListSetup
import com.example.kotlinnote.du.PermissionSetup
import com.example.kotlinnote.du.RootViewHelper
import com.example.kotlinnote.du.ToolbarHelper

class App : MultiDexApplication() {
    companion object {
        var sApp: App? = null
        fun getApp(): App? {
            return sApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        sApp = this
        Core.init(
            sApp,
            arrayOf(ToolbarHelper::class.java),
            ListSetup::class.java,
            PermissionSetup::class.java,
            null,
            RootViewHelper::class.java
        )
        Core.DEBUG = BuildConfig.DEBUG
    }
}