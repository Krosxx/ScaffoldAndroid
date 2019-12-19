package cn.daqinjia.android.common

/**
 * # Ext
 * Created on 2019/12/19
 *
 * @author Vove
 */

operator fun String.times(t: Int): String {
    require(t >= 0)
    return Array(t) { this }.joinToString("")
}