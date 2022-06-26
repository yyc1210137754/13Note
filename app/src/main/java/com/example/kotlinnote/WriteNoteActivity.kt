package com.example.kotlinnote

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import cn.dujc.core.ui.BaseActivity
import cn.dujc.core.util.ToastUtil
import com.example.kotlinnote.db.SQLiteDB
import com.example.kotlinnote.entity.NoteBean
import kotlinx.android.synthetic.main.activity_write_note.*
import java.text.SimpleDateFormat
import java.util.*

class WriteNoteActivity : BaseActivity(), OnFocusChangeListener {
    companion object {
        val EXTRA_ADD = 1
        val EXTRA_UPDATE = 2
        val EXTRA_TYPE = "EXTRA_TYPE" //是新增还是修改

        val EXTRA_TITLE = "EXTRA_TITLE" //标题

        val EXTRA_CONTENT = "EXTRA_CONTENT" //内容

        val EXTRA_ID = "EXTRA_ID" //备忘录id


    }

    private var mIsShowOk = false //是否正在显示完成的按钮--说明这个时候在编辑
    private var mType = 0

    private var mSQLiteDB: SQLiteDB? = null
    private var mTitle: String? = null
    private var mContent: kotlin.String? = null
    private var mId: kotlin.String? = null
    private var mNoteCode: String? = null
    private val mList: List<String> = ArrayList()
    private val mStringList: List<String> = ArrayList()

    override fun getViewId(): Int {
        return R.layout.activity_write_note

    }

    override fun initBasic(savedInstanceState: Bundle?) {
        mTitle = extras().get<String>(EXTRA_TITLE, null)
        mContent = extras().get<String>(EXTRA_CONTENT, null)
        mId = extras().get<String>(EXTRA_ID, null)
        mSQLiteDB = SQLiteDB.getInstance(mActivity)
        mType = extras().get(EXTRA_TYPE, mType)
        edit_content?.setOnFocusChangeListener(this)
        edit_title?.setOnFocusChangeListener(this)
        title = ""
        if (mType == EXTRA_ADD) {
            setTitleMenuIcon(R.mipmap.icon_ok, 0) { addNote() }
            mIsShowOk = true
        } else if (mType == EXTRA_UPDATE) {
            edit_title?.setText(mTitle)
            edit_content?.setText(mContent)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        when (v.id) {
            R.id.edit_title, R.id.edit_content ->
                if (!mIsShowOk && hasFocus) {
                    setTitleMenuIcon(
                        R.mipmap.icon_ok, 0
                    ) { updateNote() }
                    mIsShowOk = true
                }
        }
    }

    /**
     * 新增
     */
    private fun addNote() {
        val title = edit_title?.text.toString()
        val content = edit_content?.text.toString()
        val date = Date()
        val addTime = SimpleDateFormat("yyyy-MM-dd HH:mm").format(date)
        val updateTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm").format(date)
        mNoteCode = date.time.toString() + ""
        val noteBean = NoteBean()
        noteBean.title = title
        noteBean.content = content
        noteBean.add_time = addTime
        noteBean.update_time = updateTime
        noteBean.note_code = mNoteCode
        if (mSQLiteDB?.addNote(noteBean)!!) {
            ToastUtil.showToast(mActivity, "保存成功")
            mIsShowOk = false
            setTitleMenuIcon(0, 0, null)
            edit_content?.clearFocus()
            edit_title?.clearFocus()
        } else {
            ToastUtil.showToast(mActivity, "保存失败")
        }
    }

    private fun updateNote() {
        var dataByCode = NoteBean() ?: null
        var isAdd = mType == EXTRA_ADD
        if (isAdd) {
            dataByCode = mSQLiteDB?.findDataByCode(mNoteCode!!)
        }
        val title = edit_title?.text.toString()
        val content = edit_content?.text.toString()
        val date = Date()
        val updateTime = SimpleDateFormat("yyyy-MM-dd HH:mm").format(date)
        mNoteCode = date.time.toString() + ""
        var noteBean = NoteBean()
        noteBean.note_id = if (isAdd) dataByCode?.note_id else mId
        noteBean.title = title
        noteBean.content = content
        noteBean.update_time = updateTime
        if (mSQLiteDB?.updateNote(noteBean)!!) {
            ToastUtil.showToast(mActivity, "保存成功")
            mIsShowOk = false
            setTitleMenuIcon(0, 0, null)
            edit_content?.clearFocus()
            edit_title?.clearFocus()
        } else {
            ToastUtil.showToast(mActivity, "保存失败")
        }
    }
}