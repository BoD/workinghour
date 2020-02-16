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

data class Date(
    val year: Year,
    val month: Month,
    val day: DayOfMonth
) {
    val isWeekend: Boolean

    init {
        if (!day.isValid) throw IllegalArgumentException("Invalid day of month")
        val dayOfWeek = asCalendar()[Calendar.DAY_OF_WEEK]
        isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }

    fun toFormattedWeekDay(): String = WEEK_DAY_FORMAT.format(asCalendar().time)

    private fun asCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year.year)
        set(Calendar.MONTH, month.ordinal)
        set(Calendar.DAY_OF_MONTH, day.dayOfMonth)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }


    companion object {
        private val WEEK_DAY_FORMAT = SimpleDateFormat("EEEE")

        fun today(): Date {
            val nowCalendar = Calendar.getInstance()
            return Date(
                year = Year(nowCalendar[Calendar.YEAR]),
                month = Month.values()[nowCalendar[Calendar.MONTH]],
                day = DayOfMonth(nowCalendar[Calendar.DAY_OF_MONTH])
            )
        }

        fun build(year: Int, month: Int, day: Int) = Date(Year(year), Month.values()[month], DayOfMonth(day))
    }

}

inline class Year(val year: Int)

enum class Month {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    companion object
}

inline class DayOfMonth(val dayOfMonth: Int) {
    val isValid get() = dayOfMonth in 1..31
}
