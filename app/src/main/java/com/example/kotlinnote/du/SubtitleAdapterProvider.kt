package com.example.kotlinnote.du

import android.content.Context
import android.widget.TextView
import cn.dujc.core.adapter.BaseQuickAdapter
import cn.dujc.core.adapter.BaseViewHolder
import cn.dujc.core.adapter.multi2.ViewProvider
import cn.dujc.core.util.StringUtil
import com.example.kotlinnote.R

/**
 * 用做副标题的adapter内容实现，这是一个字体大小26，黑色，padding 20的textView
 */
class SubtitleAdapterProvider : ViewProvider {
    override fun layoutId(): Int {
        return R.layout.item_subtitle
    }

    override fun convert(
        context: Context,
        adapter: BaseQuickAdapter<*, *>?,
        helper: BaseViewHolder,
        data: Any
    ) {
        val textView = helper.getView<TextView>(R.id.tv_text)
        textView.text = StringUtil.concat(data)
    }
}