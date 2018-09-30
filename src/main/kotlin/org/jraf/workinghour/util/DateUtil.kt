package org.jraf.workinghour.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

private val HOUR_MINUTE_FORMAT = SimpleDateFormat("H:mm")
private val DAY_FORMAT = SimpleDateFormat("MMMM d yyyy")
private val WEEK_DAY_FORMAT = SimpleDateFormat("EEEE")

fun workingDayAgo(nbDaysAgo: Int): Calendar {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH, -nbDaysAgo)
    // Rewind again if it is now the weekend
    while (cal[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || cal[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
        cal.add(Calendar.DAY_OF_MONTH, -1)
    }
    return cal
}

fun Date.formatHourMinute(): String = HOUR_MINUTE_FORMAT.format(this)
fun Date.formatDay(): String = DAY_FORMAT.format(this)
fun Date.formatWeekDay(): String = WEEK_DAY_FORMAT.format(this)
