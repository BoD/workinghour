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
