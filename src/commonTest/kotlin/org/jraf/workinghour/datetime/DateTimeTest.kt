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
import kotlin.time.days
import kotlin.time.hours
import kotlin.time.minutes

class DateTimeTest {
    @Test
    fun `exercise plus`() {
        assertEquals(
            expected = DateTime(Date(Year(1970), Month.JANUARY, DayOfMonth(1)), Time(Hour(0), Minutes(1))),
            actual = DateTime(Date(Year(1970), Month.JANUARY, DayOfMonth(1)), Time(Hour(0), Minutes(0))) + 1.minutes
        )
        assertEquals(
            expected = DateTime(Date(Year(1970), Month.JANUARY, DayOfMonth(1)), Time(Hour(1), Minutes(0))),
            actual = DateTime(Date(Year(1970), Month.JANUARY, DayOfMonth(1)), Time(Hour(0), Minutes(0))) + 1.hours
        )
        assertEquals(
            expected = DateTime(Date(Year(1970), Month.JANUARY, DayOfMonth(2)), Time(Hour(0), Minutes(0))),
            actual = DateTime(Date(Year(1970), Month.JANUARY, DayOfMonth(1)), Time(Hour(0), Minutes(0))) + 1.days
        )
        assertEquals(
            expected = DateTime(Date(Year(1970), Month.FEBRUARY, DayOfMonth(12)), Time(Hour(0), Minutes(0))),
            actual = DateTime(Date(Year(1970), Month.JANUARY, DayOfMonth(1)), Time(Hour(0), Minutes(0))) + 42.days
        )
    }
}
