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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.days

class DateTest {
    @Test
    fun `exercise weekDay`() {
        assertEquals(
            expected = WeekDay.THURSDAY,
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(1)).weekDay
        )
        assertEquals(
            expected = WeekDay.FRIDAY,
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(2)).weekDay
        )
        assertEquals(
            expected = WeekDay.SATURDAY,
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(3)).weekDay
        )
        assertEquals(
            expected = WeekDay.SUNDAY,
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(4)).weekDay
        )
        assertEquals(
            expected = WeekDay.MONDAY,
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(5)).weekDay
        )
        assertEquals(
            expected = WeekDay.TUESDAY,
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(6)).weekDay
        )
        assertEquals(
            expected = WeekDay.WEDNESDAY,
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(7)).weekDay
        )
    }

    @Test
    fun `exercise isWeekend`() {
        assertFalse(Date(Year(1970), Month.JANUARY, DayOfMonth(1)).isWeekend)
        assertFalse(Date(Year(1970), Month.JANUARY, DayOfMonth(2)).isWeekend)
        assertTrue(Date(Year(1970), Month.JANUARY, DayOfMonth(3)).isWeekend)
        assertTrue(Date(Year(1970), Month.JANUARY, DayOfMonth(4)).isWeekend)
    }

    @Test
    fun `exercise plus`() {
        assertEquals(
            expected = Date(Year(1970), Month.JANUARY, DayOfMonth(2)),
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(1)) + 1.days
        )
        assertEquals(
            expected = Date(Year(1970), Month.FEBRUARY, DayOfMonth(12)),
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(1)) + 42.days
        )

    }

    @Test
    fun `exercise toFormattedWeekDay`() {
        assertEquals(
            expected = "Thursday",
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(1)).toFormattedWeekDay()
        )
        assertEquals(
            expected = "Friday",
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(2)).toFormattedWeekDay()
        )
        assertEquals(
            expected = "Saturday",
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(3)).toFormattedWeekDay()
        )
        assertEquals(
            expected = "Sunday",
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(4)).toFormattedWeekDay()
        )
        assertEquals(
            expected = "Monday",
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(5)).toFormattedWeekDay()
        )
        assertEquals(
            expected = "Tuesday",
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(6)).toFormattedWeekDay()
        )
        assertEquals(
            expected = "Wednesday",
            actual = Date(Year(1970), Month.JANUARY, DayOfMonth(7)).toFormattedWeekDay()
        )
    }
}
