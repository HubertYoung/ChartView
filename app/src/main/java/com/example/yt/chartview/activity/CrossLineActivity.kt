package com.example.yt.chartview.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.yt.chartview.R
import com.example.yt.chartview.bean.CrossLineBean
import com.example.yt.chartview.bean.History
import com.google.gson.Gson
import com.yt.crossline.CrossLineChartView
import com.yt.crossline.CrossLineCoordinate
import com.yt.linechart.ChartNode
import com.yt.utils.LogUtils
import com.yt.utils.Utils
import org.jetbrains.anko.find
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.util.*

class CrossLineActivity : AppCompatActivity() {
	val TAG = CrossLineActivity::class.java.simpleName
	private lateinit var mCrossLine: CrossLineChartView
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_cross_line)
		mCrossLine = find<CrossLineChartView>(R.id.cross_line)
		val json = getDataFromAsset()
		LogUtils.eTag(TAG, "json is $json")
		val data = Gson().fromJson(json, CrossLineBean::class.java)
		initData(data)
		mCrossLine.setInvalidataChart { initData(data) }


	}

	/**
	 * 从asset下获取json数据
	 */
	private fun getDataFromAsset(): String {
		var builder = StringBuilder()
		val stream: InputStream = assets.open("income.json")
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

	var payList: ArrayList<History>? = null
	var rcyList: ArrayList<History>? = null

	private fun initData(data: CrossLineBean) {
		val responseData = data.ResponseData
		val list = responseData.History
		LogUtils.eTag(TAG, "list===$list")
		val payInfo = responseData.pay
		val rcvInfo = responseData.rcv

		if (payList == null) {
			payList = arrayListOf<History>()
		} else {
			payList!!.clear()
		}

		if (rcyList == null) {
			rcyList = arrayListOf<History>()
		} else {
			rcyList!!.clear()
		}

		payList!!.addAll(list)
		rcyList!!.addAll(list)

		Collections.sort(payList, PayComparator())
		LogUtils.eTag(TAG, "排序后的payList$payList")
		Collections.sort(rcyList, RcyComparator())
		LogUtils.eTag(TAG, "排序后的rcyList$rcyList")


		val payLength = payList!!.size
		val minPay = payList!![0].pay
		val bigDecimalMinPay = BigDecimal(minPay.replace(",", ""))
		val maxPay = payList!![payLength - 1].pay
		val bigDecimalMaxPay = BigDecimal(maxPay.replace(",", ""))

		val rcvLength = rcyList!!.size
		val minRcv = rcyList!![0].rcv
		val bigDecimalMinRcv = BigDecimal(minRcv.replace(",", ""))
		val maxRcv = rcyList!![rcvLength - 1].rcv
		val bigDecimalMaxRcv = BigDecimal(maxRcv.replace(",", ""))
		var minY = ""
		var maxY = ""
		if (bigDecimalMaxPay >= bigDecimalMaxRcv) {
			maxY = maxPay
		} else {
			maxY = maxRcv
		}
		if (bigDecimalMinPay >= bigDecimalMinRcv) {
			minY = minRcv
		} else {
			minY = minPay
		}

		val points = getPoint(list)
		LogUtils.eTag(TAG, "倒序points$points")
		mCrossLine.setxPoints(points)
		mCrossLine.setPartLength(mPartLength)

		var payDataNodes: ArrayList<ChartNode> = ArrayList<ChartNode>()
		var recDataNodes: ArrayList<ChartNode> = ArrayList<ChartNode>()

		val size = list.size
		val xPointSize = points.size
		var j = 0

		//新建两个缓存的list用于缓存node数据，然后在后面在倒序处理
		val payDataCache = ArrayList<ChartNode>()
		val recDataCache = ArrayList<ChartNode>()
		//x坐标业务要求固定12条，收支点的数据动态的，所以从画点从右往左画
		for (i in 0 until  size) {
			var xNode = 0f
			xNode = points.get(xPointSize - 1 - j).getX()
			j++
			//把当前的数据转化成ChartNode
			val payChartNode = Utils.convertNode(minY, maxY, xNode, list[i].pay, list[i].pay, mCrossLine.getLineChartHeight() - Utils.dp2px(this, CrossLineChartView.marginBottom), Utils.dp2px(this, 40))
			val recChartNode = Utils.convertNode(minY, maxY, xNode, list[i].rcv, list[i].rcv, mCrossLine.getLineChartHeight() - Utils.dp2px(this, CrossLineChartView.marginBottom), Utils.dp2px(this, 40))

			payDataCache.add(payChartNode)
			recDataCache.add(recChartNode)
		}
		payDataNodes = sortCharNode(payDataCache)
		recDataNodes = sortCharNode(recDataCache)
		//view上面显示支出
		mCrossLine.setPayNodes(payDataNodes)
		//view上面显示收入
		mCrossLine.setRecNodes(recDataNodes)

		//设置颜色
		val payColorBean = CrossLineCoordinate.ColorBean(payInfo.payColor, payInfo.payColor, payInfo.payColor, null, null)
		val recColorBean = CrossLineCoordinate.ColorBean(rcvInfo.rcvColor, rcvInfo.rcvColor, rcvInfo.rcvColor, null, null)

		//支出颜色
		mCrossLine.setPayColorBean(payColorBean)
		//收入颜色
		mCrossLine.setRecColorBean(recColorBean)

		//顶部红点显示的文字
		mCrossLine.setPayDotTextBean(payInfo.payName)
		mCrossLine.setRecDotTextBean(rcvInfo.rcvName)
		//必须要调用刷新重绘，否则拿不到mCrossLine的宽度
		mCrossLine.invalidate()
	}

	/**
	 * x轴每一部分的宽度，将想轴分为12份
	 */
	private var mPartLength: Int = 0

	/**
	 * 获取x轴显示的月份
	 * 格式有2017.8-2018.7
	 */
	private fun getPoint(list: List<History>): ArrayList<ChartNode> {
		val xPoints = ArrayList<ChartNode>()
		//取出最后一个月份
		val lastMonth = getLastMonth(list)
		//取出第一个有数据的月
		val firstMonth = getFirstMonth(list)
		val lastMonthYear = getLastMonthYear(list)
		//起始位置
		var firstPos = Integer.parseInt(firstMonth)
		//获取当前年份
		val firstMonthYear = getFirstMonthYear(list)
		var lastPos = Integer.parseInt(lastMonth)
		LogUtils.eTag(TAG, "pos is $lastPos")
		var subPart = 0
//		if (lastPos >= 1 && firstPos!=1 && lastPos !=firstPos && list.size > lastPos)  {
//			subPart = Integer.parseInt(getSpaceMonth(list, lastPos)) - firstPos + lastPos
//			LogUtils.eTag(TAG,"getSpaceMonth(list, lastPos)="+getSpaceMonth(list, lastPos))
//		} else {
//			subPart = lastPos - firstPos
//		}
		val part = 11
		val width = mCrossLine.width
		val offset = Utils.dp2px(this, 60)

		val scaleX = (width - offset) / part
		//每个块的宽度，从右向左开始画
		mPartLength = scaleX
		var j = 0
		var subYear = ""
		for (i in 0 .. part) {
//			subPart++
			val startX = scaleX * i
			val stopY = mCrossLine.getLineChartHeight() - Utils.dp2px(this, CrossLineChartView.marginBottom)
			var month = ""
			month = (firstPos + j).toString() + "月"
			if (1 != firstPos) {
				if ("1月" == month) {
					subYear = (Integer.parseInt(subYear.substring(0, 4)) + 1).toString() + "年"
				} else if ("12月".equals(month)) {
					subYear = firstMonthYear
				}
			} else {
				subYear = ""
			}
			if (firstPos + j == 13) {
				firstPos = 1
				j = 0
				month = firstPos.toString() + "月"
				if ("1月" == month) {
					subYear = (Integer.parseInt(subYear.substring(0, 4)) + 1).toString() + "年"
				}
			}
			j++
			val chartNode = ChartNode((startX + offset / 2).toFloat(), stopY.toFloat(), month)
			chartNode.subYear = subYear
			//默认状态下最后一条数据为选中状态
			chartNode.isFlag = month == (lastMonth +"月")

			xPoints.add(chartNode)
		}

//        for (i in part downTo 0) {
//            val startX = scaleX * i
//            val stopY = mCrossLine.getLineChartHeight() - Utils.dp2px(this,
//                    CrossLineChartView.marginBottom)
//            var month = ""
//            month = (pos + j).toString() + "月"
//            if (12 != pos) {
//                if ("1月".equals(month)) {
//                    subYear = lastMonthYear
//                } else if ("12月".equals(month)) {
//                    subYear = (Integer.parseInt(subYear.substring(0, 4)) - 1).toString() + "年"
//                }
//            } else {
//                subYear = ""
//            }
//            if (pos + j == 0) {
//                pos = 12
//                j = 0
//                month = pos.toString() + "月"
//                if ("12月".equals(month)) {
//                    subYear = (Integer.parseInt(subYear.substring(0, 4)) - 1).toString() + "年"
//                }
//            }
//            j--
//            val chartNode = ChartNode((startX + offset / 2).toFloat(), stopY.toFloat(), month)
//            chartNode.subYear = subYear
//            //默认状态下最后一条数据为选中状态
//            chartNode.isFlag = i == part
//
//            xPoints.add(chartNode)
//        }
		return sortCharNode(xPoints)
	}

	private fun getSpaceMonth(list: List<History>, lastPos: Int): String {
		return list[list.size - 1-lastPos].month.split("年")[1].replace("月", "")
	}

	private fun getFirstMonthYear(list: List<History>): String {
		return list[0].month.substring(0, 5)
	}

	private fun getFirstMonth(list: List<History>): String {
		return list[0].month.split("年")[1].replace("月", "")
	}

	private fun getLastMonthYear(list: List<History>): String {
		return list[0].month.substring(0, 5)
	}


	/**
	 * 倒序排列
	 */
	private fun sortCharNode(xPoints: ArrayList<ChartNode>): ArrayList<ChartNode> {
		val nodes = ArrayList<ChartNode>()
		for (i in xPoints.indices.reversed()) {
			nodes.add(xPoints.get(i))
		}
		return nodes
	}

	/**
	 * 获取最后一个月
	 */
	private fun getLastMonth(list: List<History>): String {
		return list[list.size - 1].month.split("年")[1].replace("月", "")
	}


	/**
	 * 伴生对象，相当于Java中的静态方法
	 */
	companion object LAUNCH {
		fun launch(context: Context) {
			val intent = Intent()
			intent.setClass(context, CrossLineActivity::class.java)
			context.startActivity(intent)
		}
	}


	class RcyComparator : Comparator<History> {
		override fun compare(o1: History?, o2: History?): Int {
			val bigDecimal1 = BigDecimal(o1!!.rcv.replace(",", ""))
			val bigDecimal2 = BigDecimal(o2!!.rcv.replace(",", ""))
			return bigDecimal1.compareTo(bigDecimal2)
		}

	}

	class PayComparator : Comparator<History> {
		override fun compare(o1: History?, o2: History?): Int {
			val bigDecimal1 = BigDecimal(o1!!.pay.replace(",", ""))
			val bigDecimal2 = BigDecimal(o2!!.pay.replace(",", ""))
			return bigDecimal1.compareTo(bigDecimal2)
		}

	}

}


