/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2020-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.workinghour.datetime

import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
data class DateTime(
    val date: Date,
    val time: Time
) {
    operator fun plus(duration: Duration): DateTime {
        return asCalendar().apply { add(Calendar.MINUTE, duration.inMinutes.toInt()) }.asDateTime()
    }

    operator fun minus(duration: Duration) = plus(-duration)

    private fun asCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, date.year.year)
        set(Calendar.MONTH, date.month.ordinal)
        set(Calendar.DAY_OF_MONTH, date.day.dayOfMonth)
        set(Calendar.HOUR_OF_DAY, this@DateTime.time.hour.hour)
        set(Calendar.MINUTE, this@DateTime.time.minutes.minutes)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    companion object {
        fun todayNow(): DateTime = DateTime(Date.today(), Time.now())
        fun build(year: Int, month: Int, day: Int, hour: Int, minutes: Int) = DateTime(Date.build(year, month, day), Time.build(hour, minutes))
    }
}

@ExperimentalTime
private fun Calendar.asDateTime() = DateTime.build(
    year = get(Calendar.YEAR),
    month = get(Calendar.MONTH),
    day = get(Calendar.DAY_OF_MONTH),
    hour = get(Calendar.HOUR_OF_DAY),
    minutes = get(Calendar.MINUTE)
)
