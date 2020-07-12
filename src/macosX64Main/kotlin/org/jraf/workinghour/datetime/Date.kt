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

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitWeekday
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSTimeZone
import platform.Foundation.date
import platform.Foundation.timeZoneWithAbbreviation
import kotlin.time.Duration

actual operator fun Date.plus(duration: Duration): Date {
    val calendar = getNSCalendar()
    val thisNsDate = asNSDate()
    val nsDate = calendar.dateByAddingUnit(NSCalendarUnitMinute, duration.inMinutes.toLong(), thisNsDate, 0)!!
    return nsDate.asDate()
}

actual fun Date.Companion.today(): Date = NSDate.date().asDate()


fun getNSCalendar(): NSCalendar {
    val calendar = NSCalendar.currentCalendar
    calendar.timeZone = NSTimeZone.timeZoneWithAbbreviation("GMT")!!
    return calendar
}

actual val Date.weekDay: WeekDay
    get() {
        val calendar = getNSCalendar()
        val components = calendar.components(NSCalendarUnitWeekday, asNSDate())
        return when (components.weekday) {
            1L -> WeekDay.SUNDAY
            2L -> WeekDay.MONDAY
            3L -> WeekDay.TUESDAY
            4L -> WeekDay.WEDNESDAY
            5L -> WeekDay.THURSDAY
            6L -> WeekDay.FRIDAY
            else -> WeekDay.SATURDAY
        }
    }

private fun Date.asNSDate(): NSDate {
    val calendar = getNSCalendar()
    val components = NSDateComponents().apply {
        year = this@asNSDate.year.year.toLong()
        month = this@asNSDate.month.ordinal.toLong() + 1
        day = this@asNSDate.day.dayOfMonth.toLong()
    }
    return calendar.dateFromComponents(components)!!
}

private fun NSDate.asDate(): Date {
    val calendar = getNSCalendar()
    val components = calendar.components(
        NSCalendarUnitDay
                or NSCalendarUnitMonth
                or NSCalendarUnitYear,
        this
    )
    return Date.build(
        year = components.year.toInt(),
        month = components.month.toInt() - 1,
        day = components.day.toInt()
    )
}
