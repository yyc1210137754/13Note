package com.example.kotlinnote.entity

import android.os.Parcel
import android.os.Parcelable

class NoteBean : Parcelable {
    var note_id: String? = null
    var title: String? = null
    var content: String? = null
    var add_time: String? = null
    var update_time: String? = null
    var note_code: String? = null//用来查询备忘录,时间戳
    override fun describeContents(): Int = 0
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(note_id)
        dest?.writeString(title)
        dest?.writeString(content)
        dest?.writeString(add_time)
        dest?.writeString(update_time)
        dest?.writeString(note_code)
    }
}