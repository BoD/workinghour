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
import kotlin.time.minutes

data class Time(
    val hour: Hour,
    val minutes: Minutes
) {
    init {
        if (!hour.isValid) throw IllegalArgumentException("Invalid hour")
        if (!minutes.isValid) throw IllegalArgumentException("Invalid minutes")
    }

    operator fun compareTo(other: Time) = if (hour == other.hour) {
        minutes.compareTo(other.minutes)
    } else {
        hour.compareTo(other.hour)
    }

    operator fun minus(other: Time): Duration {
        return ((hour.hour * 60 + minutes.minutes) - (other.hour.hour * 60 + other.minutes.minutes)).minutes
    }

    companion object {
        fun build(hour: Int, minutes: Int) = Time(
            Hour(hour),
            Minutes(minutes)
        )
    }
}

expect fun Time.toFormattedString(): String

expect fun Time.Companion.now(): Time

inline class Hour(val hour: Int) {
    val isValid get() = hour in 0..23

    operator fun compareTo(other: Hour) = hour.compareTo(other.hour)
}

inline class Minutes(val minutes: Int) {
    val isValid get() = minutes in 0..59

    operator fun compareTo(other: Minutes) = minutes.compareTo(other.minutes)
}
