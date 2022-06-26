package com.example.kotlinnote.adapter

import android.text.TextUtils
import android.widget.TextView
import cn.dujc.core.adapter.BaseAdapter
import cn.dujc.core.adapter.BaseViewHolder
import com.example.kotlinnote.R
import com.example.kotlinnote.entity.NoteBean
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteAdapter(list: MutableList<NoteBean>? = ArrayList()) :
    BaseAdapter<NoteBean>(R.layout.item_note, list) {
    override fun convert(helper: BaseViewHolder?, item: NoteBean?) {
        helper?.getView<TextView>(R.id.tv_note_title)?.text = item?.title
        helper?.getView<TextView>(R.id.tv_note_content)?.text = item?.content

        if (!TextUtils.isEmpty(item?.update_time)) {
            var time = item?.update_time?.split(" ")
            var date = Date()
            var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            var tvLastTime = helper?.getView<TextView>(R.id.tv_note_last_time)
            if (simpleDateFormat.format(date).equals(time!![0])) {
                tvLastTime?.text = time[1]
            } else {
                tvLastTime?.text = item?.update_time
            }
        }
        helper?.addOnClickListener(R.id.tv_delete)

    }
}