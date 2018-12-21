package com.example.yt.chartview.bean

/**
 * <br>
 * com.example.yt.chartview.bean
 *
 * @author lei
 * @version 4.21
 * @date 2018/8/28 下午4:24
 * CMBC-版权所有
 *
 */



data class CrossLineBean(
    val delayTime: String,
    val MSG: String,
    val ResponseData: ResponseData,
    val isEncryptForRespData: Boolean,
    val ResponseCode: String,
    val STATUS: String,
    val callbackKey: String,
    val ResponseMsg: String,
    val isAvailable: String
)

data class ResponseData(
    val History: List<History>,
    val helpInfo: HelpInfo,
    val rcv: Rcv,
    val pay: Pay
)

data class History(
    val month: String,
    val pay: String,
    val rcv: String
)

data class Pay(
    val payColor: String,
    val payName: String
)

data class HelpInfo(
    val HELP_TEXT: String,
    val HELP_TITLE: String
)

data class Rcv(
    val rcvColor: String,
    val rcvName: String
)