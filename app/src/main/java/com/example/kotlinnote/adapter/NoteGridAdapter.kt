package com.example.kotlinnote.adapter

import android.text.TextUtils
import android.widget.TextView
import cn.dujc.core.adapter.BaseAdapter
import cn.dujc.core.adapter.BaseViewHolder
import com.example.kotlinnote.R
import com.example.kotlinnote.entity.NoteBean

class NoteGridAdapter(list: MutableList<NoteBean>? = ArrayList()) :
    BaseAdapter<NoteBean>(R.layout.item_note_grid, list) {
    override fun convert(helper: BaseViewHolder?, item: NoteBean?) {
        helper?.getView<TextView>(R.id.tv_note_title)?.text = item?.title
        helper?.getView<TextView>(R.id.tv_note_title2)?.text = item?.title
        helper?.getView<TextView>(R.id.tv_note_content)?.text = item?.content
        if (!TextUtils.isEmpty(item?.update_time)) {
            item?.update_time?.split(" ")
            helper?.getView<TextView>(R.id.tv_note_last_time)?.text = item?.update_time
        }
    }
}