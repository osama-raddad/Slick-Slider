package sw.osama.com.sliderdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var data: MutableList<Pair<String,Any>> = ArrayList()
        for (i in 0 until 16) data.add(i.toString() to i.toString())
        data = data.toList().sortedBy { (key, _) -> key } as MutableList
        slider.partSize = 4
        slider.startDisplacement = 1
        slider.endDisplacement = 3
        slider.titleFormatter = { "$it:00" }
        slider.setData(data)
        slider.onReady = {
            slider.startSliding()
            slider.onItemChangeListener = { time.text = it.first as CharSequence? }
            play.setOnClickListener { slider.pauseSliding() }
            forward.setOnClickListener { slider.forward() }
            backward.setOnClickListener { slider.backward() }
            speed_up.setOnClickListener { slider.speedFactor(slider.speedFactor + 1) }
            speed_down.setOnClickListener { slider.speedFactor(if (slider.speedFactor - 1 <= 0) 1 else slider.speedFactor - 1) }
        }
    }

}
