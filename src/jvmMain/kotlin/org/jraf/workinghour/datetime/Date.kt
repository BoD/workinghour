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

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.time.Duration

actual val Date.isWeekend: Boolean
    get() {
        val dayOfWeek = asCalendar()[Calendar.DAY_OF_WEEK]
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }

private fun Date.asCalendar(): Calendar = Calendar.getInstance().apply {
    set(Calendar.YEAR, year.year)
    set(Calendar.MONTH, month.ordinal)
    set(Calendar.DAY_OF_MONTH, day.dayOfMonth)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

actual operator fun Date.plus(duration: Duration): Date {
    return asCalendar().apply { add(Calendar.MINUTE, duration.inMinutes.toInt()) }.asDate()
}

private fun Calendar.asDate() = Date.build(
    year = get(Calendar.YEAR),
    month = get(Calendar.MONTH),
    day = get(Calendar.DAY_OF_MONTH)
)

private val WEEK_DAY_FORMAT = SimpleDateFormat("EEEE")

actual fun Date.toFormattedWeekDay(): String = WEEK_DAY_FORMAT.format(asCalendar().time)
actual fun Date.Companion.today(): Date {
    val nowCalendar = Calendar.getInstance()
    return Date(
        year = Year(nowCalendar[Calendar.YEAR]),
        month = Month.values()[nowCalendar[Calendar.MONTH]],
        day = DayOfMonth(nowCalendar[Calendar.DAY_OF_MONTH])
    )
}

actual fun Month.toFormattedString(): String = toString().toLowerCase(Locale.US).capitalize(Locale.US)
