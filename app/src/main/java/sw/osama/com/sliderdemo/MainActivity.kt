package sw.osama.com.sliderdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.osama.slider.RTLSimpleSlider
import com.osama.slider.RTLPartSlider
import com.osama.slider.SimpleSlider
import com.osama.slider.PartSlider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupPartsSlider(genData(14))
        setupRTLPartsSlider(genData(14))
//
        setupSlider(genData(4))
        setupRTLSlider(genData(4))
    }

    private fun genData(size: Int): MutableMap<String, Any> {
        val data: MutableMap<String, Any> = HashMap()
        for (i in 0 until size) data[i.toString()] = i.toString()
        return data.toList().sortedBy { (key, _) -> key.toInt() }.toMap() as MutableMap<String, Any>
    }

    private fun setupRTLPartsSlider(data: MutableMap<String, Any>) {
        sliderPartRTLFrameLayout.removeAllViews()
        val slider = RTLPartSlider(this)
        slider.isVerticalScrollBarEnabled = false
        slider.isHorizontalScrollBarEnabled = false
        slider.partSize = 4
        slider.titleFormatter = { "$it:00" }
        slider.setData(data)
        slider.onPlay = { play.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp) }
        slider.onStop = { play.setImageResource(R.drawable.ic_play_circle_filled_black_24dp) }
        slider.onReady = {
            slider.startSliding()
            slider.onItemChangeListener = {
//                runOnUiThread { time.text = it.second as CharSequence? }
            }

            play.setOnClickListener { slider.pauseSliding() }
            forward.setOnClickListener { slider.forward() }
            backward.setOnClickListener { slider.backward() }
            speed_up.setOnClickListener { slider.speedFactor(slider.speedFactor + 1) }
            speed_down.setOnClickListener { slider.speedFactor(if (slider.speedFactor - 1 <= 0) 1 else slider.speedFactor - 1) }
        }
        sliderPartRTLFrameLayout.addView(slider)
    }

    private fun setupPartsSlider(data: MutableMap<String, Any>) {
        sliderPartFrameLayout.removeAllViews()
        val slider = PartSlider(this)
        slider.isVerticalScrollBarEnabled = false
        slider.isHorizontalScrollBarEnabled = false
        slider.partSize = 4
        slider.titleFormatter = { "$it:00" }
        slider.setData(data)
        slider.onPlay = { play.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp) }
        slider.onStop = { play.setImageResource(R.drawable.ic_play_circle_filled_black_24dp) }
        slider.onReady = {
            slider.startSliding()
            slider.onItemChangeListener = {
//                runOnUiThread { time.text = it.second as CharSequence? }
            }

            play.setOnClickListener { slider.pauseSliding() }
            forward.setOnClickListener { slider.forward() }
            backward.setOnClickListener { slider.backward() }
            speed_up.setOnClickListener { slider.speedFactor(slider.speedFactor + 1) }
            speed_down.setOnClickListener { slider.speedFactor(if (slider.speedFactor - 1 <= 0) 1 else slider.speedFactor - 1) }
        }
        sliderPartFrameLayout.addView(slider)
    }

    private fun setupSlider(data: MutableMap<String, Any>) {
        sliderFrameLayout.removeAllViews()
        val slider = SimpleSlider(this)
        slider.isVerticalScrollBarEnabled = false
        slider.isHorizontalScrollBarEnabled = false
        slider.titleFormatter = { "$it:00" }
        slider.setData(data)
        slider.onPlay = { play.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp) }
        slider.onStop = { play.setImageResource(R.drawable.ic_play_circle_filled_black_24dp) }
        slider.onReady = {
            slider.startSliding()
            slider.onItemChangeListener = {
//                runOnUiThread { time.text = it.second as CharSequence? }
            }

            play.setOnClickListener { slider.pauseSliding() }
            forward.setOnClickListener { slider.forward() }
            backward.setOnClickListener { slider.backward() }
            speed_up.setOnClickListener { slider.speedFactor(slider.speedFactor + 1) }
            speed_down.setOnClickListener { slider.speedFactor(if (slider.speedFactor - 1 <= 0) 1 else slider.speedFactor - 1) }
        }
        sliderFrameLayout.addView(slider)
    }

    private fun setupRTLSlider(data: MutableMap<String, Any>) {
        sliderRTLFrameLayout.removeAllViews()
        val slider = RTLSimpleSlider(this)
        slider.isVerticalScrollBarEnabled = false
        slider.isHorizontalScrollBarEnabled = false
        slider.titleFormatter = { "$it:00" }
        slider.setData(data)
        slider.onPlay = { play.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp) }
        slider.onStop = { play.setImageResource(R.drawable.ic_play_circle_filled_black_24dp) }
        slider.onReady = {
            slider.startSliding()
            slider.onItemChangeListener = {
//                runOnUiThread { time.text = it.second as CharSequence? }
            }

            play.setOnClickListener { slider.pauseSliding() }
            forward.setOnClickListener { slider.forward() }
            backward.setOnClickListener { slider.backward() }
            speed_up.setOnClickListener { slider.speedFactor(slider.speedFactor + 1) }
            speed_down.setOnClickListener { slider.speedFactor(if (slider.speedFactor - 1 <= 0) 1 else slider.speedFactor - 1) }
        }
        sliderRTLFrameLayout.addView(slider)
    }
}
