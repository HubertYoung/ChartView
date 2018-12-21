package com.example.yt.chartview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.yt.chartview.R.id.*
import com.example.yt.chartview.activity.CrossLineActivity
import com.example.yt.chartview.activity.LineChartActivity
import com.yt.utils.LogUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.dip

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAction()
        dip(12)

    }

    private fun initAction() {
        btn_line.setOnClickListener(this)
        btn_cross.setOnClickListener(this)
        btn3.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }


    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btn_line -> LineChartActivity.launch(this@MainActivity)
            R.id.btn_cross -> CrossLineActivity.launch(this@MainActivity)
            else -> LogUtils.iTag(TAG, "点击出错")
        }
    }


}
