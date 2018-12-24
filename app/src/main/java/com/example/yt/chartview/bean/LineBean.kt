package com.example.yt.chartview.bean

/**
 * <br>
 * com.example.yt.chartview.bean
 *
 * @author lei
 * @version 4.21
 * @date 2018/8/21 下午2:39
 *
 */


data class LineBean(
    val delayTime: String,
    val MSG: String,
    val respData: RespData,
    val isEncryptForRespData: Boolean,
    val ResponseCode: String,
    val STATUS: String,
    val callbackKey: String,
    val ResponseMsg: String,
    val isAvailable: String
)

data class RespData(
    val AssesInfo: AssesInfo,
    val DateList: DateList,
    val BillMonth: String,
    val ColorText: ColorText,
    val List: List<X>
)

data class AssesInfo(
    val maxAsset: String,
    val maxDate: String,
    val minDate: String,
    val minAsset: String
)

data class ColorText(
    val areaColor: String,
    val textColor: String,
    val lineColor: String,
    val minPellucid: String,
    val shallColor: String,
    val maxPellucid: String,
    val deepColor: String
)

data class DateList(
    val startDate: String,
    val endDate: String,
    val midDate: String,
    val isShowAllDate:Boolean
)

data class X(
    val date: String,
    val totalAsset: String
)