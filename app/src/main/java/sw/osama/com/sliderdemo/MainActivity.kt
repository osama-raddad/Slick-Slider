package sw.osama.com.sliderdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.osama.slider.Slider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var data: MutableMap<String, Any> = HashMap()
        val a: MutableMap<String, Any>
        for (i in 0 until 13) data[i.toString()] = i.toString()
        a = data.toList().sortedBy { (key, _) -> key.toInt() }.toMap() as MutableMap<String, Any>
        data = a
        sliderFrameLayout.removeAllViews()
        val slider = Slider(this)
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
                runOnUiThread { time.text = it.second as CharSequence? }
            }

            play.setOnClickListener { slider.pauseSliding() }
            forward.setOnClickListener { slider.forward() }
            backward.setOnClickListener { slider.backward() }
            speed_up.setOnClickListener { slider.speedFactor(slider.speedFactor + 1) }
            speed_down.setOnClickListener { slider.speedFactor(if (slider.speedFactor - 1 <= 0) 1 else slider.speedFactor - 1) }
        }
        sliderFrameLayout.addView(slider)
    }
}
