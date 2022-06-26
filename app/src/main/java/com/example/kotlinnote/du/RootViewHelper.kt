package com.example.kotlinnote.du

import android.view.View
import butterknife.ButterKnife
import cn.dujc.core.initializer.content.IRootViewSetup

class RootViewHelper : IRootViewSetup {
    override fun setup(target: Any, rootView: View) {
        ButterKnife.bind(target, rootView)
    }
}