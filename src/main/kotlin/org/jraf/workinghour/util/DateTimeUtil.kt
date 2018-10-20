/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2018 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.workinghour.util

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DurationFormatUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

private val HOUR_MINUTE_FORMAT = SimpleDateFormat("H:mm")
private val DAY_FORMAT = SimpleDateFormat("MMMM d yyyy")
private val WEEK_DAY_FORMAT = SimpleDateFormat("EEEE")

fun workingDayAgo(nbDaysAgo: Int): Calendar {
    val cal = Calendar.getInstance()
    for (i in 0 until nbDaysAgo) {
        cal.add(Calendar.DAY_OF_MONTH, -1)
        // Rewind until it is not the weekend
        while (cal[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || cal[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
    }
    return cal
}

fun workingDaysAgo(nbWeekAgo: Int): List<Calendar> {
    val cal = Calendar.getInstance()
    // Rewind until it is not the weekend
    while (cal[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || cal[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
        cal.add(Calendar.DAY_OF_MONTH, -1)
    }
    cal.add(Calendar.WEEK_OF_MONTH, -nbWeekAgo)
    cal[Calendar.DAY_OF_WEEK] = Calendar.FRIDAY
    val res = mutableListOf<Calendar>()
    for (i in 1..5) {
        res += cal.clone() as Calendar
        cal.add(Calendar.DAY_OF_MONTH, -1)
    }
    return res
}

fun Date.formatHourMinute(): String = HOUR_MINUTE_FORMAT.format(this)
fun Date.formatDay(): String = DAY_FORMAT.format(this)
fun Date.formatWeekDay(): String = WEEK_DAY_FORMAT.format(this)

fun formatDuration(minutes: Number): String {
    return StringUtils.rightPad(
        DurationFormatUtils.formatDuration(TimeUnit.MINUTES.toMillis(minutes.toLong()), "H'h'm'm'")
            .replace("h0m", "h")
            .replace(Regex("^0h(.+)"), "$1")
        , 6
    )

}

infix operator fun Date?.minus(date: Date?): Long {
    if (this == null || date == null) return 0L
    return TimeUnit.MILLISECONDS.toMinutes(time - date.time)
}