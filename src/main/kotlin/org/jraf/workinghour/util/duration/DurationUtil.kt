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

package org.jraf.workinghour.util.duration

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Duration.formatHourMinutes(): String {
    val totalMinutes = inMinutes.toInt()
    val hours = totalMinutes / 60
    val remainMinutes = totalMinutes % 60
    return when {
        hours == 0 -> "${remainMinutes}m"
        remainMinutes == 0 -> "${hours}h"
        else -> "%1\$dh%2\$02dm".format(hours, remainMinutes)
    }
}