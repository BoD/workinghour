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

import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.jraf.workinghour.SqlDelightDatabase
import org.jraf.workinghour.datetime.Date
import org.jraf.workinghour.datetime.DateTime
import org.jraf.workinghour.datetime.Month

actual class Database actual constructor(databasePath: String) {
    private val sqlDelightDatabase = SqlDelightDatabase(NativeSqliteDriver(SqlDelightDatabase.Schema, databasePath).apply {
        SqlDelightDatabase.Schema.create(this)
    })

    actual fun insertLog(logType: LogType, dateTime: DateTime) {
        sqlDelightDatabase.activityLogQueries.insertLog(
            type = logType.dbRepresentation,
            year = dateTime.date.year.year.toLong(),
            month = dateTime.date.month.ordinal.toLong(),
            day = dateTime.date.day.dayOfMonth.toLong(),
            hour = dateTime.time.hour.hour.toLong(),
            minute = dateTime.time.minutes.minutes.toLong()
        )
    }

    actual fun updateLogDateTime(logId: LogId, dateTime: DateTime) {
        sqlDelightDatabase.activityLogQueries.updateLogDateTime(
            year = dateTime.date.year.year.toLong(),
            month = dateTime.date.month.ordinal.toLong(),
            day = dateTime.date.day.dayOfMonth.toLong(),
            hour = dateTime.time.hour.hour.toLong(),
            minute = dateTime.time.minutes.minutes.toLong(),
            id = logId.id.toLong()
        )
    }

    actual fun firstLogOfDay(date: Date): Log? = logOfDay(date, LogType.FIRST_OF_DAY)

    actual fun lastLogOfDay(date: Date): Log? = logOfDay(date, LogType.LAST_OF_DAY)

    actual fun lastLogOfMorning(date: Date): Log? = logOfDay(date, LogType.LAST_OF_MORNING)

    actual fun firstLogOfAfternoon(date: Date): Log? = logOfDay(date, LogType.FIRST_OF_AFTERNOON)

    private fun logOfDay(date: Date, logType: LogType): Log? {
        return sqlDelightDatabase.activityLogQueries.selectLogOfDayWithType(
            year = date.year.year.toLong(),
            month = date.month.dbRepresentation,
            day = date.day.dayOfMonth.toLong(),
            type = logType.dbRepresentation,
            mapper = ::map
        )
            .executeAsOneOrNull()
    }

    companion object {
        private fun map(
            id: Long,
            type: Long,
            year: Long,
            month: Long,
            day: Long,
            hour: Long,
            minute: Long
        ): Log {
            return Log(
                id = LogId(id.toInt()),
                logType = LogType.fromDbRepresentation(type),
                dateTime = DateTime.build(
                    year = year.toInt(),
                    month = Month.fromDbRepresentation(month).ordinal,
                    day = day.toInt(),
                    hour = hour.toInt(),
                    minutes = minute.toInt()
                )
            )
        }

        private fun Month.Companion.fromDbRepresentation(dbRepresentation: Long) = Month.values()[(dbRepresentation - 1).toInt()]
    }

    private val Month.dbRepresentation: Long
        get() = (ordinal + 1).toLong()
}
