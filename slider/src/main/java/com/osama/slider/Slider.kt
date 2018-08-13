@file:Suppress("DEPRECATION")

package com.osama.slider

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.Resources
import android.os.AsyncTask
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


class Slider(context: Context) : ObservableHorizontalScrollView(context) {
    var partSize: Int = 1
    var displacement: Int = 0
    var vibrate: Boolean = true
    var vibrationLength: Long = 15

    var speedFactor: Int = 1
        private set

    var onReady: () -> Unit = {}
    var onPlay: () -> Unit = {}
    var onStop: () -> Unit = {}

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val root: View = inflater.inflate(R.layout.view_slider, this)
    private lateinit var linearLayout: LinearLayout
    private var linearLayoutWidth: Float = 0f
    private var itemWidth: Float = dpToPx(48f) / partSize
    private var currentPosition = 0
    private lateinit var scrollAnimator: ObjectAnimator
    private val playerSpeed: Float = 1600f
    private var start: Int = 0
    private var startGrayWidth: Int = 0
    private var end: Int = 0
    private lateinit var item: View
    private var partsCount: Int = 16
    private var oldIndex: Int = -1

    private val fastSpeed: Float = 50f
    private val delay: Long = 400
    private val displayWidth = Resources.getSystem().displayMetrics.widthPixels

    private var motionEvent: Int = MotionEvent.ACTION_UP

    var onItemChangeListener: (item: Pair<*, *>) -> Unit = { }
    var titleFormatter: ((key: Any?) -> String)? = null
    private var items: MutableMap<*, *> = mutableMapOf<Any, Any>()

    fun <T, K> setData(data: MutableMap<T, K>) {
        if (::scrollAnimator.isInitialized) scrollAnimator.cancel()
        overScrollMode = View.OVER_SCROLL_NEVER
        items = data
        addItemsToLayout(data)
        addScrollingListener()
        addMotionListener()
        calculateTheStartAndEndOfLayout {
            scrollToStartOfSlider { onReady() }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (::scrollAnimator.isInitialized) scrollAnimator.cancel()
    }

    @SuppressLint("SetTextI18n")
    private fun <T, K> addItemsToLayout(data: MutableMap<T, K>) {
        linearLayout = root.findViewById(R.id.ll)
        linearLayout.removeAllViews()
        val size = if ((displacement > 0) &&
                (((data.size + displacement - 1) / partSize.toFloat())
                        - (data.size + displacement - 1) / partSize) > 0f)
            data.size - 1 + partSize else data.size - 1

        for (i in 0 until size step partSize) {
            item = inflater.inflate(R.layout.view_item, linearLayout, false)
            val label = item.findViewById<TextView>(R.id.label)

            if (displacement > 0) {
                if (i == 0) applyDisplacementStart(item)
                if (data.size - i <= partSize) applyDisplacementEnd(item)
            }
            if (titleFormatter == null) {
                label.text = if (data.keys.elementAt(i) is String) data.keys.elementAt(i).toString() else ""
            } else label.text = titleFormatter?.invoke(data.keys.elementAt(i))

            item.post { itemWidth = item.width.toFloat() / partSize }
            linearLayout.addView(item)
        }
    }

    private fun addScrollingListener() {
        onScrollChanged = {
            currentPosition = it
            val index = getViewIndex()
            if (((index - displacement) >= 0) and ((index - displacement) < items.size)) {
                if (index != oldIndex) {
                    oldIndex = index
                    onItemChangeListener((index - displacement).toString() to items.values.elementAt(index - displacement))
                    vibrate()
                }
            }
            if (::item.isInitialized) itemWidth = item.width.toFloat() / partSize
        }
    }

    private fun vibrate() {
        if (motionEvent == MotionEvent.ACTION_MOVE && vibrate)
            AsyncTask.execute {
                if (Build.VERSION.SDK_INT >= 26)
                    (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(vibrationLength, VibrationEffect.DEFAULT_AMPLITUDE))
                else
                    (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(vibrationLength)

            }
    }

    private fun addMotionListener() {
        onMotionChange = {
            motionEvent = it
            if (::scrollAnimator.isInitialized) {
                if (it == MotionEvent.ACTION_MOVE) {
                    scrollAnimator.pause()
                    onStop()
                }
                if (it == MotionEvent.ACTION_UP) {
                    post { scrollTo(currentPosition, 0) }
                    snapToClosestItem()
                }
            }
        }
    }

    private fun calculateTheStartAndEndOfLayout(callback: () -> Unit) {
        linearLayout.post {
            linearLayoutWidth = linearLayout.width.toFloat()
            startGrayWidth = root.findViewById<View>(R.id.grayArea1).width
            start = ((((startGrayWidth) - (displayWidth / 2))) + ((itemWidth) * displacement)).toInt()
            end = (((((startGrayWidth) - (displayWidth / 2))) + linearLayoutWidth) - ((itemWidth) * ((displacement - partSize).absoluteValue))).toInt()
            callback()
        }
    }

    private fun getViewIndex(): Int {
        val index = ((displayWidth / 2) - startGrayWidth + currentPosition) / (itemWidth)
        return index.toInt()
    }

    private fun applyDisplacementEnd(item: View) {
        when ((displacement - partSize).absoluteValue) {
            1 -> {
                setPartsColor(item.findViewById<View>(R.id.forth_quarter))
            }
            2 -> {
                setPartsColor(item.findViewById<View>(R.id.forth_quarter))
                setPartsColor(item.findViewById<View>(R.id.third_quarter))
            }
            3 -> {
                setPartsColor(item.findViewById<View>(R.id.forth_quarter))
                setPartsColor(item.findViewById<View>(R.id.third_quarter))
                setPartsColor(item.findViewById<View>(R.id.second_quarter))
            }
        }
    }


    private fun applyDisplacementStart(item: View) {
        when (displacement) {
            1 -> {
                setPartsColor(item.findViewById<View>(R.id.first_quarter))
            }
            2 -> {
                setPartsColor(item.findViewById<View>(R.id.first_quarter))
                setPartsColor(item.findViewById<View>(R.id.second_quarter))
            }
            3 -> {
                setPartsColor(item.findViewById<View>(R.id.first_quarter))
                setPartsColor(item.findViewById<View>(R.id.second_quarter))
                setPartsColor(item.findViewById<View>(R.id.third_quarter))
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setPartsColor(quarter: View) {
        quarter.findViewWithTag<View>("v1").setBackgroundColor(resources.getColor(R.color.gray))
        quarter.findViewWithTag<View>("v2").setBackgroundColor(resources.getColor(R.color.gray))
        quarter.findViewWithTag<View>("v3").setBackgroundColor(resources.getColor(R.color.gray))
    }

    private fun startPlaying(factor: Int) {
        onPlay()
//        scroll to the end
        animatedScroll(end, Math.abs((playerSpeed * (partsCount / partSize)) * (currentPosition - end) / (end - start)) / factor) {
            replay(factor)
        }
    }

    fun replay(factor: Int = speedFactor) {
        if (::scrollAnimator.isInitialized)
            scrollAnimator.pause()
        //          delay
        handler.postDelayed({
            //            back to start
            post {
                smoothScrollTo(start, 0)
                //          delay
                handler.postDelayed({
                    //                repeat
                    startPlaying(factor)
                }, delay)
            }
        }, delay)
    }

    private fun snapToClosestItem() {
        when {
            currentPosition < start -> animatedScroll(start, fastSpeed)
            currentPosition > end -> animatedScroll(end, fastSpeed)
            else -> snap()
        }
    }

    private fun scrollToStartOfSlider(cb: () -> Unit = {}) {
        post {
            scrollTo(start, 0)
            animatedScroll(start, fastSpeed) {
                cb()
            }
        }
    }

    private fun snap() {
        val x = (((currentPosition / itemWidth).roundToInt() * itemWidth))
        post { smoothScrollTo(x.toInt(), 0) }
    }

    private fun animatedScroll(position: Int, speed: Float, cb: () -> Unit = {}) {
        post {
            if (::scrollAnimator.isInitialized) scrollAnimator.pause()
            scrollAnimator = ObjectAnimator.ofInt(this, "scrollX", position)
            scrollAnimator.duration = (speed).toLong()
            scrollAnimator.setInterpolator { it }
            scrollAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) = cb()
            })
            scrollAnimator.start()
        }
    }

    private fun dpToPx(float: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, float, resources.displayMetrics)
    }

    fun startSliding() {
        startPlaying(speedFactor)
    }

    /**
     * Returns the state of the slider.
     *
     * @return false if the slider stop or true if it start .
     */
    fun pauseSliding() {
        if (motionEvent != MotionEvent.ACTION_MOVE) {
            if (!scrollAnimator.isPaused and scrollAnimator.isRunning) {
                scrollAnimator.pause()
                snapToClosestItem()
            } else
                startPlaying(speedFactor)
        }
    }

    fun forward() {
        if (motionEvent != MotionEvent.ACTION_MOVE) {
            if (!scrollAnimator.isPaused) scrollAnimator.pause()
            post {
                snapToClosestItem()
                if ((currentPosition > start) and (currentPosition < end))
                    post {
                        val x = (((currentPosition / itemWidth).roundToInt() * itemWidth))
                        smoothScrollTo((x + itemWidth).toInt(), 0)
                        snapToClosestItem()
                        vibrate()
                    }

            }
        }
    }

    fun backward() {
        if (motionEvent != MotionEvent.ACTION_MOVE) {
            if (!scrollAnimator.isPaused) scrollAnimator.pause()
            post {
                snapToClosestItem()
                if ((currentPosition > start) and (currentPosition < end))
                    post {
                        val x = (((currentPosition / itemWidth).roundToInt() * itemWidth))
                        smoothScrollTo((x - itemWidth).toInt(), 0)
                        snapToClosestItem()
                        vibrate()
                    }
            }
        }
    }

    fun speedFactor(factor: Int) {
        speedFactor = factor
        if (!scrollAnimator.isPaused) scrollAnimator.pause()
        startPlaying(factor)
    }
}