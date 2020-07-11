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

data class DateTime(
    val date: Date,
    val time: Time
) {

    operator fun minus(duration: Duration) = plus(-duration)

    companion object {
        fun todayNow(): DateTime =
            DateTime(
                Date.today(),
                Time.now()
            )

        fun build(year: Int, month: Int, day: Int, hour: Int, minutes: Int) =
            DateTime(
                Date.build(
                    year,
                    month,
                    day
                ), Time.build(hour, minutes)
            )
    }
}

expect operator fun DateTime.plus(duration: Duration): DateTime
