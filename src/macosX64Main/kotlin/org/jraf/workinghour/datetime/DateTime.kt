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

import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSTimeZone
import platform.Foundation.timeZoneWithAbbreviation
import kotlin.time.Duration

actual operator fun DateTime.plus(duration: Duration): DateTime {
    val calendar = getNSCalendar()
    val thisNsDate = asNSDate()
    val nsDate = calendar.dateByAddingUnit(NSCalendarUnitMinute, duration.inMinutes.toLong(), thisNsDate, 0)!!
    return nsDate.asDateTime()
}


private fun DateTime.asNSDate(): NSDate {
    val calendar = getNSCalendar()
    calendar.timeZone = NSTimeZone.timeZoneWithAbbreviation("GMT")!!
    val components = NSDateComponents().apply {
        year = this@asNSDate.date.year.year.toLong()
        month = this@asNSDate.date.month.ordinal.toLong() + 1
        day = this@asNSDate.date.day.dayOfMonth.toLong()
        hour = this@asNSDate.time.hour.hour.toLong()
        minute = this@asNSDate.time.minutes.minutes.toLong()
    }
    return calendar.dateFromComponents(components)!!
}

private fun NSDate.asDateTime(): DateTime {
    val calendar = getNSCalendar()
    val components = calendar.components(
        NSCalendarUnitDay
                or NSCalendarUnitMonth
                or NSCalendarUnitYear
                or NSCalendarUnitHour
                or NSCalendarUnitMinute,
        this
    )
    return DateTime.build(
        year = components.year.toInt(),
        month = components.month.toInt() - 1,
        day = components.day.toInt(),
        hour = components.hour.toInt(),
        minutes = components.minute.toInt()
    )
}
