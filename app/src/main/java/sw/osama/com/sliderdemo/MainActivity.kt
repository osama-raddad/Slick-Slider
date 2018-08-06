package sw.osama.com.sliderdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.split
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data: HashMap<String, String> = HashMap()
        for (i in  0 until 16) {

//            val split = ((i * 15) / 60).toString().split(".")
//
//            val h = if (split.size >= 1) split[0] else "0"
//            val m = if (split.size >= 2) split[1] else "0"
//
//            data[("0$h : 0$m")] = ""
            data[i.toString()] = i.toString()
        }
        slider.partSize = 4
        slider.setData(data)
        slider.onReady = {
            slider.startSliding()
            slider.onItemChangeListener = { s: String, any: Any? -> time.text = s }
            play.setOnClickListener { slider.pauseSliding() }
            forward.setOnClickListener { slider.forward() }
            backward.setOnClickListener { slider.backward() }
            speed_up.setOnClickListener { slider.speedFactor(slider.speedFactor + 1) }
            speed_down.setOnClickListener { slider.speedFactor(if (slider.speedFactor - 1 <= 0) 1 else slider.speedFactor - 1) }
        }
    }

}
