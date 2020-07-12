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

import kotlin.time.Duration

data class Date(
    val year: Year,
    val month: Month,
    val day: DayOfMonth
) {
    val isWeekend = weekDay.isWeekend

    init {
        if (!day.isValid) throw IllegalArgumentException("Invalid day of month")
    }

    operator fun minus(duration: Duration) = plus(-duration)

    operator fun compareTo(other: Date) = if (year == other.year) {
        if (month == other.month) {
            day.compareTo(other.day)
        } else {
            month.compareTo(other.month)
        }
    } else {
        year.compareTo(other.year)
    }

    fun toFormattedFullDate(): String {
        val formattedMonth = month.toFormattedString()
        return "$formattedMonth ${day.dayOfMonth} ${year.year}"
    }

    fun toFormattedWeekDay(): String = weekDay.toFormattedString()

    companion object {
        /**
         * [month] is 0 based
         * [day] is 1 based (1..31)
         */
        fun build(year: Int, month: Int, day: Int) = Date(
            Year(year),
            Month.values()[month],
            DayOfMonth(day)
        )
    }
}

expect operator fun Date.plus(duration: Duration): Date

expect fun Date.Companion.today(): Date


inline class Year(val year: Int) {
    operator fun compareTo(other: Year) = year.compareTo(other.year)
}

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
    DECEMBER,
    ;

    fun toFormattedString() = toString().toLowerCase().capitalize()

    companion object
}

inline class DayOfMonth(val dayOfMonth: Int) {
    val isValid get() = dayOfMonth in 1..31

    operator fun compareTo(other: DayOfMonth) = dayOfMonth.compareTo(other.dayOfMonth)
}

enum class WeekDay(val isWeekend: Boolean) {
    MONDAY(false),
    TUESDAY(false),
    WEDNESDAY(false),
    THURSDAY(false),
    FRIDAY(false),
    SATURDAY(true),
    SUNDAY(true),
    ;


    fun toFormattedString() = toString().toLowerCase().capitalize()
}

expect val Date.weekDay: WeekDay
