package com.example.kotlinnote

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.dujc.core.impls.TextWatcherImpl
import cn.dujc.core.ui.BaseActivity
import cn.dujc.core.util.ToastUtil
import com.example.kotlinnote.adapter.NoteAdapter
import com.example.kotlinnote.adapter.NoteGridAdapter
import com.example.kotlinnote.db.SQLiteDB
import com.example.kotlinnote.entity.NoteBean
import com.example.kotlinnote.picker.SinglePickUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_write_note.*
import kotlinx.android.synthetic.main.toolbar_normal.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity(), View.OnClickListener {
    private val mNoteBeanList: MutableList<NoteBean>? = ArrayList()
    override fun getViewId(): Int = R.layout.activity_main
    private var isFirst = true
    private val mOrderBys = Arrays.asList("编辑日期", "截止日期", "标题")
    private var mOrderBy = "update_time"
    private var mNoteAdapter: NoteAdapter? = null
    private var mNoteGridAdapter: NoteGridAdapter? = null
    private var isRecycview = true

    var refreshLayout: SwipeRefreshLayout? = null
    var tvBack: ImageView? = null
    var coreListViewId: RecyclerView? = null

    override fun initBasic(savedInstanceState: Bundle?) {
        setTitle("备忘录")
        refreshLayout = findViewById(R.id.core_list_refresh_id)
        tvBack = findViewById(R.id.core_toolbar_back_id)
        coreListViewId = findViewById(R.id.core_list_view_id)
        edit_search_context.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(s: Editable?) {
                mNoteBeanList?.clear()
                mNoteBeanList?.addAll(
                    SQLiteDB.getInstance(mActivity)?.findDataByContent(
                        s.toString(),
                        mOrderBy
                    )?:ArrayList()
                )
                if (isRecycview) {
                    mNoteAdapter?.notifyDataSetChanged()
                } else {
                    mNoteGridAdapter?.notifyDataSetChanged()
                }
            }
        })
        refreshLayout?.isEnabled = false
        core_toolbar_back_id?.visibility = View.GONE
        tv_order_by.setOnClickListener(this)
        iv_add_note.setOnClickListener(this)
        iv_switch_view.setOnClickListener(this)
        initAdapter()
        updateView()


    }


    override fun onResume() {
        super.onResume()
        if (!isFirst) {
            loadData()
        }
        isFirst = false;
    }



    private fun initAdapter() {
        mNoteAdapter = NoteAdapter(mNoteBeanList)
        mNoteAdapter?.setOnItemClickListener { adapter, view, position ->
            goToUpdate(position)
        }
        mNoteGridAdapter = NoteGridAdapter(mNoteBeanList)
        mNoteGridAdapter?.setOnItemClickListener { adapter, view, position ->
            goToUpdate(position)
        }
        mNoteAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val noteBean = mNoteBeanList!![position]
            if (view.id == R.id.tv_delete) {
                if (SQLiteDB.getInstance(mActivity)?.delNote(noteBean?.note_id!!)!!) {
                    loadData()
                } else {
                    ToastUtil.showToast(mActivity, "删除失败")
                }
            }
        }

    }

    private fun goToUpdate(position: Int) {
        val noteBean = mNoteBeanList!![position]
        starter().with(WriteNoteActivity.EXTRA_TYPE, WriteNoteActivity.EXTRA_UPDATE)
            .with(WriteNoteActivity.EXTRA_CONTENT, noteBean.content)
            .with(WriteNoteActivity.EXTRA_TITLE, noteBean.title)
            .with(WriteNoteActivity.EXTRA_ID, noteBean.note_id)
            .go(WriteNoteActivity::class.java)
    }

    private fun updateView() {
        if (!isRecycview) {
            coreListViewId?.layoutManager = GridLayoutManager(mActivity, 3)
            coreListViewId?.adapter = mNoteGridAdapter
            loadData()
        } else {
            coreListViewId?.layoutManager = LinearLayoutManager(mActivity)
            coreListViewId?.adapter = mNoteAdapter
            loadData()
        }
    }

    private fun loadData() {
        mNoteBeanList?.clear()
        mNoteBeanList?.addAll(SQLiteDB.getInstance(mActivity)?.findAllData(mOrderBy)!!)
        tv_note_count.text = String.format("%s个备忘录", mNoteBeanList?.size)
        if (isRecycview) {
            mNoteAdapter?.notifyDataSetChanged()
        } else {
            mNoteGridAdapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_add_note -> {
                starter().with(WriteNoteActivity.EXTRA_TYPE, WriteNoteActivity.EXTRA_ADD).go(
                    WriteNoteActivity::class.java
                )
            }
            R.id.tv_order_by -> {
                val singlePickUtil = SinglePickUtil(mActivity)
                singlePickUtil.show()
                val textView: TextView = singlePickUtil.findViewById<TextView>(R.id.tv_title)!!
                textView.text = ""
                singlePickUtil.resetData(mOrderBys)
                singlePickUtil.setOnSelectedPositionListener(object :
                    SinglePickUtil.OnSelectedPositionListener {
                    override fun onSelected(selectedValue: Any?, position: Int) {
                        tv_order_by.text = String.format("按%s排序", selectedValue.toString())
                        when (position) {
                            0 -> mOrderBy = "update_time"
                            1 -> mOrderBy = "add_time"
                            2 -> mOrderBy = "title"
                        }
                        loadData()
                    }
                })
            }
            R.id.iv_switch_view -> {
                isRecycview = !isRecycview
                updateView()
            }
        }
    }
}
