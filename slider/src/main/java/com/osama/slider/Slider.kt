package com.osama.slider

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.widget.TextView
import kotlin.math.roundToInt


class Slider(context: Context, attrs: AttributeSet) : ObservableHorizontalScrollView(context, attrs) {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val root: View = inflater.inflate(R.layout.view_slider, this)
    private lateinit var linearLayout: LinearLayout
    private var linearLayoutWidth: Float = 0f
    var partSize: Int = 1
    private var itemWidth: Float = 60f / partSize
    private var currentPosition = 0
    private lateinit var scrollAnimator: ObjectAnimator
    private val playerSpeed: Float = 1200f
    private var start: Int = 0
    private var startGrayWidth: Int = 0
    private var end: Int = 0
    private lateinit var item: View
    private var partsCount: Int = 16
    private val fastSpeed: Float = 50f
    private val delay: Long = 400
    private val displayWidth = Resources.getSystem().displayMetrics.widthPixels
    var speedFactor: Int = 1
        private set
    private var motionEvent: Int = MotionEvent.ACTION_UP
    var onReady: () -> Unit = {}
    var onPlay: () -> Unit = {}
    var onStop: () -> Unit = {}
    lateinit var onItemChangeListener: (key: String, obj: Any?) -> Unit
    private var items: HashMap<String, out Any?> = HashMap()

    fun setData(data: HashMap<String, out Any>) {
        overScrollMode = View.OVER_SCROLL_NEVER
        items = data
        addItemsToLayout(data)
        addScrollingListener()
        addMotionListener()
        calculateTheStartAndEndOfLayout {
            scrollToStartOfSlider { onReady() }
        }
    }

    private fun calculateTheStartAndEndOfLayout(cb: () -> Unit) {
        linearLayout.post {
            linearLayoutWidth = linearLayout.width.toFloat()
            startGrayWidth = root.findViewById<View>(R.id.grayArea1).width
            start = startGrayWidth - (displayWidth / 2)
            end = (start + linearLayoutWidth).toInt()
            cb()
        }
    }

    private fun addMotionListener() {
        onMotionChange = {
            motionEvent = it
            if (::scrollAnimator.isInitialized) {
                if (it == MotionEvent.ACTION_MOVE) scrollAnimator.pause()
                if (it == MotionEvent.ACTION_UP) snapToClosestItem()
            }
        }
    }

    private fun addScrollingListener() {
        onScrollChanged = {
            currentPosition = it
            val index = getViewIndex()
            if (((index) >= 0) and ((index) < items.size))
                if (::onItemChangeListener.isInitialized)
                    onItemChangeListener(index.toString(), items.values.elementAt(index))
            if (::item.isInitialized) itemWidth = item.width.toFloat() / partSize
        }
    }

    private fun getViewIndex(): Int {
        val index = ((displayWidth / 2) - startGrayWidth + currentPosition) / (itemWidth)
        return index.roundToInt()
    }

    @SuppressLint("SetTextI18n")
    private fun addItemsToLayout(data: HashMap<String, out Any>) {
        linearLayout = root.findViewById(R.id.ll)

        for (i in 0 until data.size step partSize) {
            item = inflater.inflate(R.layout.view_item, linearLayout, false)
            val label = item.findViewById<TextView>(R.id.label)
            label.text = data.keys.elementAt(i)
            item.post { itemWidth = item.width.toFloat() / partSize }
            linearLayout.addView(item)
        }
    }

    private fun startPlaying(factor: Int) {
//        scroll to the end
        animatedScroll(end, Math.abs((playerSpeed * (partsCount / partSize)) * (currentPosition - end) / (end - start)) / factor) {
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
        val x = (((currentPosition / itemWidth).roundToInt() * itemWidth) + dpToPx(5.2f))
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
                override fun onAnimationCancel(animation: Animator?) = onStop()
                override fun onAnimationStart(animation: Animator?) = onPlay()
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
                        val x = (((currentPosition / itemWidth).roundToInt() * itemWidth) + dpToPx(5.2f))
                        smoothScrollTo((x + itemWidth).toInt(), 0)
                        snapToClosestItem()
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
                        val x = (((currentPosition / itemWidth).roundToInt() * itemWidth) + dpToPx(5.2f))
                        smoothScrollTo((x - itemWidth).toInt(), 0)
                        snapToClosestItem()
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