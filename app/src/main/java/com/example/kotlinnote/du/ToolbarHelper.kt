package com.example.kotlinnote.du

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.dujc.core.initializer.toolbar.IToolbar
import cn.dujc.core.ui.IBaseUI
import com.example.kotlinnote.R
import java.util.*

/**
 * 默认title配置
 */
class ToolbarHelper : IToolbar {
    private val mExclude: List<Class<out IBaseUI?>> =
        ArrayList()

    override fun normal(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.toolbar_normal, parent, false)
    }

    override fun statusBarColor(context: Context): Int {
        return Color.WHITE
    }

    override fun statusBarMode(): Int {
        return IToolbar.DARK
    }

    override fun toolbarStyle(): Int {
        return IToolbar.LINEAR
    }

    override fun include(): List<Class<out IBaseUI?>>? {
        return null
    }

    override fun exclude(): List<Class<out IBaseUI?>> {
        return mExclude
    }
}