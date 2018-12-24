package com.example.yt.chartview.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.yt.chartview.R
import com.example.yt.chartview.bean.AssesInfo
import com.example.yt.chartview.bean.DateList
import com.example.yt.chartview.bean.LineBean
import com.example.yt.chartview.bean.X
import com.google.gson.Gson
import com.yt.linechart.ChartNode
import com.yt.linechart.Coordinate
import com.yt.linechart.LineChartView
import com.yt.linechart.LineChartView.marginBottom
import com.yt.utils.LogUtils
import com.yt.utils.Utils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

class LineChartActivity : AppCompatActivity() {
    val TAG = LineChartActivity::class.java.simpleName
    private var mLineChartView: LineChartView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_chart)
        mLineChartView = findViewById<LineChartView>(R.id.lc_view)
        val json = getDataFromAssets()
        LogUtils.eTag(TAG, "json is $json")
        val data = Gson().fromJson(json, LineBean::class.java)
        mLineChartView!!.setInvalidataChart { initData(data) }
        initData(data)
    }


    private fun initData(data: LineBean) {
        val respData = data.respData
        val assesInfo = respData.AssesInfo
        val colorText = respData.ColorText
        val dateList = respData.DateList
        val list = respData.List
        val xpoints = getXpoints(dateList)
        mLineChartView!!.setxPoints(xpoints)
        val nodes = getNodes(list, xpoints, assesInfo)
        mLineChartView!!.nodes = nodes
        mLineChartView!!.bottomTextBean = getBottomTextBean(dateList, list)
        val colorBean = Coordinate.ColorBean(colorText.areaColor, colorText.textColor, colorText.lineColor, colorText
                .minPellucid, colorText.maxPellucid)
        colorBean.deepColor = colorText.deepColor
        colorBean.shallColor = colorText.shallColor
        mLineChartView!!.colorBean = colorBean
        addChartText(nodes, colorBean)
        mLineChartView!!.invalidate()

    }

    /**
     * x轴底部数据显示
     */
    private fun getBottomTextBean(dateList: DateList, listBean: List<X>): Coordinate.BottomTextBean {
        val bottomTextBean = Coordinate.BottomTextBean(dateList.startDate, dateList
                .endDate, dateList.midDate)
        bottomTextBean.isChange = dateList.isShowAllDate
        //当月全部日期
        val dates = arrayListOf<String>()
        for (element in listBean) {
            val date = element.date.substring(6)
            dates.add(date)
        }
        bottomTextBean.setDateList(dates)
        return bottomTextBean
    }

    private fun addChartText(nodes: ArrayList<ChartNode>, colorBean: Coordinate.ColorBean) {
        for (element in nodes) {
            if (element.isMinY || element.isMaxY){
                addNodeText(element, colorBean)
            }

        }

    }

    /**
     *折线图上添加资产明细 只显示最大和最小
     */
    private fun addNodeText(element: ChartNode, colorBean: Coordinate.ColorBean) {
        LogUtils.eTag(TAG, "addNodeText:::${element.text}")
        val textView = TextView(this)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams
                .WRAP_CONTENT)
        textView.setPadding(8, 8, 8, 8)
        textView.layoutParams = params
        mLineChartView!!.addView(textView)
        textView.setBackgroundColor(Color.WHITE)
        var content: String = ""
        if (element.isMinY) {
            content = "￥" + getFormatText(element.text)
        } else if (element.isMaxY) {
            content = "￥" + getFormatText(element.text)
        }
        textView.text = content
        var textColor: Int = 0
        try {
            textColor = Color.parseColor(colorBean.textColor)
        } catch (exception: Exception) {
            textColor = Color.parseColor("#FD8E4A")
        }
        textView.setTextColor(textColor)
        textView.setTextSize(10F)
        val x = element.x
        val y = element.y
        val offsetsUp = Utils.dp2px(this@LineChartActivity, 27)
        val offsetsDown = Utils.dp2px(this@LineChartActivity, 5)
        //textView的位置
        if (x > mLineChartView!!.width / 2) {
            val offset = Utils.dp2px(this, 50)
            textView.x = x - offset
        } else {
            textView.x = x
        }
        if (element.isMinY()) {
            textView.y = y + offsetsDown
        } else {
            textView.y = y - offsetsUp
        }

    }

    /**
     *金额格式为6,789.78
     */
    private fun getFormatText(text: String?): String? {
        LogUtils.eTag(TAG, "text is $text")
        var decimalFormat: DecimalFormat? = null
        if (text!!.startsWith("0.")) {
            return text
        }
        if (text.indexOf(".") != -1) {
            val split = text.split(".")
            val s = split[1]
            val stringBuffer = StringBuffer()
            for (i in 0 until s.length) {
                if (i == 0) {
                    stringBuffer.append(".0")
                } else {
                    stringBuffer.append("0")
                }
            }
            decimalFormat = DecimalFormat("#,###" + stringBuffer.toString())
            return decimalFormat.format(BigDecimal(text))
        } else {
            decimalFormat = DecimalFormat("#,###")
            return decimalFormat.format(BigDecimal(text))
        }
        return text
    }


    private fun getNodes(list: List<X>, xpoints: ArrayList<ChartNode>?, assesInfo: AssesInfo)
            : ArrayList<ChartNode> {
        val maxY = assesInfo.maxAsset
        val minY = assesInfo.minAsset
        val dataNodes = arrayListOf<ChartNode>()
        for (element in list) {
            val date = Integer.parseInt(element.date.substring(6))
            if (xpoints != null && xpoints.size > 0) {
                for (i in 0 until xpoints.size) {
                    if ((date - 1) == i) {
                        LogUtils.eTag(TAG, "minY is $minY;maxY is $maxY;xpoints[i].x is " +
                                "${xpoints[i]
                                        .x};" + "element.totalAsset is ${element.totalAsset};mLineChartView!!" +
                                ".height is ${mLineChartView!!.lineChartHeight}")
                        val chartNode = Utils.convertNode(minY, maxY, xpoints[i].x, element.totalAsset,
                                element.totalAsset, mLineChartView!!.lineChartHeight - Utils.dp2px(this,
                                marginBottom), Utils.dp2px(this, 40))
                        if (maxY.equals(element.totalAsset) && assesInfo.maxDate.equals(element.date)) {
                            chartNode.isMaxY = true
                        } else if (minY.equals(element.totalAsset) && assesInfo.minDate.equals(element.date)) {
                            chartNode.isMinY = true
                        }
                        dataNodes.add(chartNode)
                        break
                    }
                }
            }
        }
        return dataNodes

    }

    /**
     * 获取x轴的点
     */
    private fun getXpoints(dateList: DateList): ArrayList<ChartNode>? {
        val xPoints = arrayListOf<ChartNode>()
        val size = Integer.parseInt(dateList.endDate.substring(3))
        if (size > 1 || size <= 31) {
            val screenSize = Utils.getScreenSize(this)
            val width = screenSize[0]
            val offSet = Utils.sp2px(this, 30F)
            val scaleX = (width - offSet) / (size - 1)
            for (index in 0 until size) {
                val startX = scaleX * index + offSet / 2
                val startY = Utils.dp2px(this, 25)
                val stopY = mLineChartView!!.lineChartHeight - Utils.dp2px(this, marginBottom)
                xPoints.add(ChartNode(startX.toFloat(), stopY.toFloat(), ""))
            }
        }
        return xPoints
    }

    /**
     * 从assets目录下读取文件
     */
    private fun getDataFromAssets(): String {
        var builder = StringBuilder()
        val stream: InputStream = assets.open("line.json")
        val length = stream.available()

        val bufferedReader = BufferedReader(InputStreamReader(stream))

        var line: String? = ""

        do {
            line = bufferedReader.readLine()
            if (line != null) {
                builder.append(line)
            } else {
                break
            }
        } while (true)

        return builder.toString()
    }


    companion object {
        fun launch(context: Context) {
            val intent = Intent()
            intent.setClass(context, LineChartActivity::class.java)
            context.startActivity(intent)
        }
    }


}
