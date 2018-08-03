package com.osama.slider

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

open class ObservableHorizontalScrollView(context: Context, attrs: AttributeSet) : HorizontalScrollView(context, attrs) {
    var onScrollChanged: ((scrollX: Int) -> Unit)? = null
    var onMotionChange: ((motionEvent: Int) -> Unit)? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChanged?.invoke(l)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        onMotionChange?.invoke(motionEvent.action)
        return super.onTouchEvent(motionEvent)
    }

    public override fun computeHorizontalScrollRange(): Int {
        return super.computeHorizontalScrollRange()
    }

}