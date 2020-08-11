package com.ruichaoqun.previewapplication

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import java.lang.RuntimeException
import kotlin.math.max

class SwipeRefreshLayout : LinearLayout, NestedScrollingParent3 {

    private val mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private val animator = ValueAnimator()
    private var state = State.RESET
    private lateinit var mRefreshView: TextView
    var mListener: OnRefreshListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        orientation = VERTICAL
        isNestedScrollingEnabled = true
        createRefreshView()
        initAnimator()
    }

    private fun createRefreshView() {
        mRefreshView = TextView(context)
        mRefreshView.text = "refresh"
        mRefreshView.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        mRefreshView.textSize = 20f
        mRefreshView.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        addView(mRefreshView, 0)
    }

    private fun initAnimator() {
        animator.duration = 300
        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(p0: ValueAnimator?) {
                mRefreshView.layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    animator.animatedValue as Int
                )
            }
        })
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        if (animator.isRunning) {
            animator.cancel()
        }
        return (ViewCompat.SCROLL_AXIS_VERTICAL and axes) != 0
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (childCount != 2) {
            throw RuntimeException("SwipeRefreshLayout should has one child and only has one child")
        }
        if (type == ViewCompat.TYPE_TOUCH) {
            val h = mRefreshView.layoutParams.height
            if (!getChildAt(1).canScrollVertically(-1) && (h > 0 || (h == 0 && dy < 0))) {
                mRefreshView.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, max(0, h - (dy * 0.7).toInt()))
                consumed[1] = dy
            }
        } else {
            if (dy > 0 && state == State.REFRESHING) {
                stopRefresh()
            }
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {

    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {

    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type)
        if (type == ViewCompat.TYPE_TOUCH) {
            if (mRefreshView.height > 200) {
                startRefresh()
            } else if (mRefreshView.height > 0) {
                stopRefresh()
            }
        }
    }

    override fun setOrientation(orientation: Int) {
        super.setOrientation(VERTICAL)
    }

    fun startRefresh() {
        if (animator.isRunning) {
            animator.cancel()
        }
        animator.setIntValues(mRefreshView.height, 200)
        animator.start()
        state = State.REFRESHING
        mListener?.onRefresh()
    }

    fun stopRefresh() {
        if (animator.isRunning) {
            animator.cancel()
        }
        animator.setIntValues(mRefreshView.height, 0)
        animator.start()
        state = State.RESET
    }

    interface OnRefreshListener {
        fun onRefresh()
    }

    enum class State {
        REFRESHING,
        RESET
    }
}