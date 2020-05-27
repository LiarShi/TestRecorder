package com.liar.testrecorder.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

/**
 * 毫秒-->分:秒:毫秒
 */
fun ms2str(milSec: Long): String {
    var msi = milSec % 1000 / 10
    var si = milSec / 1000 % 60
    var mi = milSec / 1000 / 60
    var s = String.format("%02d", si)
    var ms = String.format("%02d", msi)
    var m = ""
    if (mi < 100)
        m = String.format("%02d", mi)
    return "$m:$s:$ms"
}

/**
 * 分:秒:毫秒-->毫秒
 */
fun str2ms(str: String): Long {
    val split = str.split(":")
    return split[0].toLong() * 60 * 1000 + split[1].toLong() * 1000 + split[2].toLong()
}

/**
 * 当前日期格式化为:xx月xx日 12-22-22
 */
fun getMdHms(): String {
    val sf = SimpleDateFormat("MM-dd HH-mm-ss")
    return sf.format(Date())
}

/**
 * 当前日期格式化为:yyyy/mm/dd hh:mm:ss
 */
fun getYmdhms(milSec: Long): String {
    val sf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    return sf.format(milSec)
}
