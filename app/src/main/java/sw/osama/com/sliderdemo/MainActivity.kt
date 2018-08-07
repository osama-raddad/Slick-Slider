package sw.osama.com.sliderdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var data: MutableMap<String,Any> = HashMap()
        val a:MutableMap<String, Any>
        for (i in 0 until 16) data[i.toString()] = i.toString()
        a= data.toList().sortedBy { (key, _) -> key.toInt() }.toMap() as MutableMap<String, Any>
        data =a
        slider.partSize = 4
        slider.startDisplacement = 1
        slider.endDisplacement = 3
        slider.titleFormatter = { "$it:00" }
        slider.setData(data)
        slider.onReady = {
            slider.startSliding()
            slider.onItemChangeListener = { time.text = it.second as CharSequence? }
            play.setOnClickListener { slider.pauseSliding() }
            forward.setOnClickListener { slider.forward() }
            backward.setOnClickListener { slider.backward() }
            speed_up.setOnClickListener { slider.speedFactor(slider.speedFactor + 1) }
            speed_down.setOnClickListener { slider.speedFactor(if (slider.speedFactor - 1 <= 0) 1 else slider.speedFactor - 1) }
        }
    }
}
