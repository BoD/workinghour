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

package org.jraf.workinghour.repository

import org.jraf.workinghour.datetime.DateTime

data class Log(
    val id: LogId,
    val logType: LogType,
    val dateTime: DateTime
)

inline class LogId(val id: Int)

enum class LogType(val dbRepresentation: Long) {
    FIRST_OF_DAY(0L),
    LAST_OF_MORNING(1L),
    FIRST_OF_AFTERNOON(2L),
    LAST_OF_DAY(3L),
    ;

    companion object {
        fun fromDbRepresentation(dbRepresentation: Long): LogType = when (dbRepresentation) {
            0L -> FIRST_OF_DAY
            1L -> LAST_OF_MORNING
            2L -> FIRST_OF_AFTERNOON
            3L -> LAST_OF_DAY
            else -> throw IllegalArgumentException("Unknown EventType $dbRepresentation")
        }
    }
}
