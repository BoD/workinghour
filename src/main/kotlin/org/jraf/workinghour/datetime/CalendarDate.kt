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

data class CalendarDate(
    val year: Year,
    val month: Month,
    val day: DayOfMonth
) {
    init {
        if (!day.isValid) throw IllegalArgumentException("Invalid day of month")
    }

    companion object {
        fun now(): CalendarDate {
            val nowCalendar = Calendar.getInstance()
            return CalendarDate(
                year = Year(nowCalendar[Calendar.YEAR]),
                month = Month.values()[nowCalendar[Calendar.MONTH]],
                day = DayOfMonth(nowCalendar[Calendar.DAY_OF_MONTH])
            )
        }
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
    DECEMBER
}

inline class DayOfMonth(val dayOfMonth: Int) {
    val isValid get() = dayOfMonth in 0..31
}
