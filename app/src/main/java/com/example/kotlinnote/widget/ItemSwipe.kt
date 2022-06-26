package com.example.kotlinnote.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.Scroller
import com.example.kotlinnote.R
import java.lang.ref.WeakReference
import java.util.*

/**
 * 滑动删除的控件
 */
class ItemSwipe @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private var mThisReference: WeakReference<ItemSwipe>? = null
    private var deleteView: View? = null
    private val scroller: Scroller
    private var onTouching = false
    private var scrollerEnable = true
    var isOpen = false
        private set
    private var oX = 0f
    private var oY = 0f
    private var scrollerScale = 0.168f //默认大概所需滑出控件的1/6大小多一些就可以滑出
    private var limit = 0
    private var startX = 0
    private var deleteWidth = 0
    //private long mPressTime = 0;//按下的时间
    private var mLongClicked = false
    private var mIsMoving = false
    private val mPressHandler = Handler(Looper.getMainLooper())
    private val mPressRunnable = Runnable {
        if (onTouching && !mIsMoving) {
            mLongClicked = performLongClick()
            onTouching = false
        } else {
            mLongClicked = false
        }
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        super.onLayout(changed, l, t, r, b)
        if (deleteView != null) {
            val params =
                deleteView?.layoutParams as LayoutParams
            var leftMargin = 0
            var topMargin = 0
            if (params != null) {
                leftMargin = params.leftMargin
                topMargin = params.topMargin
            }
            val cl: Int
            val cr: Int
            val ct: Int
            val cb: Int
            cl = measuredWidth - paddingRight + leftMargin
            cr = cl + deleteView?.measuredWidth!!
            ct = paddingTop + topMargin
            cb = ct + deleteView?.measuredHeight!!
            deleteView?.layout(cl, ct, cr, cb)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var index = 0
        val count = childCount
        while (index < count) {
            val view = getChildAt(index)
            val params =
                view.layoutParams as LayoutParams
            if (params.isOutside) {
                deleteView = view
                deleteWidth = view.measuredWidth
                if (limit == 0) {
                    limit = (deleteWidth * scrollerScale).toInt()
                }
                if (limit < MIN_LIMIT) {
                    limit = MIN_LIMIT
                }
                break
            }
            index++
        }
    }

    override fun onDetachedFromWindow() {
        mPressHandler.removeCallbacks(mPressRunnable)
        super.onDetachedFromWindow()
        OPENING_SCROLLER.remove(WeakReference(this))
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (!isScrollerEnable()) {
            closeAll(this)
            //当滑动不启用时，返回默认方法
            return super.dispatchTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //mPressTime = System.currentTimeMillis();
                onTouching = true
                mIsMoving = false
                oX = event.rawX
                oY = event.rawY
                mPressHandler.removeCallbacks(mPressRunnable)
                mPressHandler.postDelayed(mPressRunnable, 500)
                closeAll(this, null)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onTouching = false
                val cX = event.rawX
                if (deleteView != null) {
                    val internalX = cX - oX
                    if (Math.abs(internalX) < MIN_LIMIT) {
                        val cY = event.rawY
                        val internalY = cY - oY
                        if (Math.abs(internalY) < MIN_LIMIT) {
                            val clicked = performClickOnThisZone(cX, cY)
                            close(if (clicked) ANIMATION_DURATION else 0)
                            mLongClicked = false
                            return clicked
                        } else {
                            mIsMoving = true
                            close()
                        }
                    } else if (internalX < -limit) { //cX - oX > 0 <--
                        mIsMoving = true
                        open()
                    } else if (internalX > limit) { //cX - oX > 0 -->
                        mIsMoving = true
                        close()
                    } else {
                        mIsMoving = true
                        closeAll(this)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                onTouching = true
                mIsMoving = true
                closeAll(this)
            }
            else -> {
                onTouching = false
                closeAll(null)
            }
        }
        return onTouching || super.dispatchTouchEvent(event)
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, 0)
            postInvalidate()
        }
    }

    fun isScrollerEnable(): Boolean {
        return scrollerEnable
    }

    @JvmOverloads
    fun open(duration: Int = ANIMATION_DURATION) {
        if (isOpen) {
            return
        }
        scroller.startScroll(startX, scroller.currY, deleteWidth, 0, duration)
        isOpen = true
        invalidate()
        if (mThisReference == null) {
            mThisReference = WeakReference(this)
        }
        OPENING_SCROLLER.add(mThisReference!!)
    }

    @JvmOverloads
    fun close(duration: Int = ANIMATION_DURATION) {
        if (!isOpen) {
            return
        }
        scroller.startScroll(deleteWidth, scroller.currY, -deleteWidth, 0, duration)
        isOpen = false
        invalidate()
        OPENING_SCROLLER.remove(mThisReference)
        mThisReference?.clear()
        mThisReference = null
    }

    /**
     * 执行了点击事件，开始判断被点中的到底是哪个。
     * <br></br>逻辑：
     *
     * 1.递归向其内部子View传递点击事件，如果(x,y)刚好在内部控件上，且其内部控件有点击事件，则认为点击事件被其消耗，停止传递
     *
     * 2.当递归内部子View结束，依然没有消耗掉事件，则尝试[.performClick]，如果被消耗，则停止传递
     *
     * 3.当2执行完毕后依然没有消耗掉点击事件，则向外部ViewGroup递归传递点击，传到没有外部或外部为AbsListView，或者外部设置了点击事件为止
     *
     * @param x
     * @param y
     * @return 本方法如果返回false，是已经被消耗了点击事件，因为我内部用了‘!/非’。与系统方法相反
     */
    private fun performClickOnThisZone(x: Float, y: Float): Boolean {
        return (!mLongClicked
                && !loopTranslateClickToChild(this, x, y)
                && !performClick()
                && !loopTranslateClickToParent(this))
    }

    /**
     * 向外部ViewGroup递归传递点击，传到没有外部或外部为AbsListView，或者外部设置了点击事件为止
     *
     * @param view 当前View
     */
    private fun loopTranslateClickToParent(view: View): Boolean {
        val viewParent = view.parent
        var parent: ViewGroup? = null
        if (viewParent is ViewGroup) {
            parent = viewParent
        }
        if (parent == null) {
            return false
        }
        var result = false
        if (parent is AbsListView) {
            val listView = parent
            val position = listView.getPositionForView(view)
            if (position != AbsListView.INVALID_POSITION) {
                result = result || listView.performItemClick(
                    view,
                    position,
                    listView.getItemIdAtPosition(position)
                )
            }
        } else if (!parent.performClick()) {
            result = result || loopTranslateClickToParent(parent)
        }
        return result
    }

    /**
     * 递归向其内部子View传递点击事件，如果(x,y)刚好在内部控件上，且其内部控件有点击事件，则认为点击事件被其消耗，停止传递
     *
     * @param parent
     * @return
     */
    private fun loopTranslateClickToChild(
        parent: ViewGroup?,
        x: Float,
        y: Float
    ): Boolean {
        if (parent == null) {
            return false
        }
        val count = parent.childCount
        for (index in 0 until count) {
            val child = parent.getChildAt(index)
            val location = IntArray(2)
            child.getLocationOnScreen(location)
            val l = location[0]
            val r = l + child.width
            val t = location[1]
            val b = t + child.height
            if (child is ViewGroup) {
                if (loopTranslateClickToChild(child, x, y)) {
                    return true
                }
                if (child.getVisibility() == View.VISIBLE && l <= x && x <= r && t <= y && y <= b && child.performClick()) {
                    return true
                }
            } else if (child.visibility == View.VISIBLE && l <= x && x <= r && t <= y && y <= b && child.performClick()) {
                return true
            }
        }
        return false
    }

    fun setScrollerEnable(scrollerEnable: Boolean) {
        this.scrollerEnable = scrollerEnable
        if (!scrollerEnable) {
            close(0)
        }
    }

    fun setScrollerScale(scrollerScale: Float) {
        this.scrollerScale = scrollerScale
    }

    fun setLimit(limit: Int) {
        this.limit = limit
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    class LayoutParams : FrameLayout.LayoutParams {
        var isOutside = false

        constructor(c: Context, attrs: AttributeSet?) : super(
            c,
            attrs
        ) {
            if (attrs != null) {
                val array =
                    c.obtainStyledAttributes(attrs, R.styleable.ItemSwipe_Layout)
                isOutside = array.getBoolean(R.styleable.ItemSwipe_Layout_IamOutside, isOutside)
                array.recycle()
            }
        }

        constructor(width: Int, height: Int) : super(width, height) {}
        constructor(source: ViewGroup.LayoutParams?) : super(source!!) {}
    }

    companion object {
        /**
         * 用于记录生成了多少控件，为了达到listview中点中一个，其余滑动控件自动关闭
         */
        private val OPENING_SCROLLER: MutableList<WeakReference<ItemSwipe>> =
            ArrayList()
        private const val ANIMATION_DURATION = 500 //默认滑动动画时间
        private const val MIN_LIMIT = 5 //最小滑动距离，同时也作为点击事件的触发基础
        fun dispatchTouchEventOnActivity(ev: MotionEvent) {
            if (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) {
                closeAll(null, ev)
            }
        }

        /**
         * 关闭其他
         *
         * @param event  事件，不为null时，在事件以内的不关闭
         * @param except 排除, null = 不排除
         */
        @JvmOverloads
        fun closeAll(except: ItemSwipe?, event: MotionEvent? = null) {
            for (reference in OPENING_SCROLLER) {
                val scroller = reference.get()
                if (scroller != null && scroller.isOpen && (except == null || scroller !== except)) {
                    if (event != null) {
                        val location = IntArray(2)
                        scroller.getLocationOnScreen(location)
                        if (location[0] > event.rawX || event.rawX > location[0] + scroller.width || location[1] > event.rawY || event.rawY > location[1] + scroller.height
                        ) {
                            scroller.close()
                        }
                    } else {
                        scroller.close()
                    }
                }
            }
        }
    }

    init {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ItemSwipe)
            limit = array.getDimensionPixelOffset(R.styleable.ItemSwipe_scrollerLimit, limit)
            scrollerScale = array.getFloat(R.styleable.ItemSwipe_scrollerScale, scrollerScale)
            scrollerEnable = array.getBoolean(R.styleable.ItemSwipe_scrollerEnable, scrollerEnable)
            array.recycle()
        }
        scroller = Scroller(context)
        startX = scroller.startX
    }
}