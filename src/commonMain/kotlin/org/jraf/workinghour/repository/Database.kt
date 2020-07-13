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

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.logs.LogSqliteDriver
import org.jraf.workinghour.SqlDelightDatabase
import org.jraf.workinghour.datetime.Date
import org.jraf.workinghour.datetime.DateTime
import org.jraf.workinghour.datetime.Month

class Database(private val debugLogs: Boolean, databasePath: String) {
    private val sqlDelightDatabase = SqlDelightDatabase(
        LogSqliteDriver(getSqliteDriver(SqlDelightDatabase.Schema, databasePath), ::log).apply {
            SqlDelightDatabase.Schema.create(this)
        })

    fun insertLog(logType: LogType, dateTime: DateTime) {
        sqlDelightDatabase.activityLogQueries.insertLog(
            type = logType.dbRepresentation,
            year = dateTime.date.year.year.toLong(),
            month = dateTime.date.month.dbRepresentation,
            day = dateTime.date.day.dayOfMonth.toLong(),
            hour = dateTime.time.hour.hour.toLong(),
            minute = dateTime.time.minutes.minutes.toLong()
        )
    }

    fun updateLogDateTime(logId: LogId, dateTime: DateTime) {
        sqlDelightDatabase.activityLogQueries.updateLogDateTime(
            year = dateTime.date.year.year.toLong(),
            month = dateTime.date.month.dbRepresentation,
            day = dateTime.date.day.dayOfMonth.toLong(),
            hour = dateTime.time.hour.hour.toLong(),
            minute = dateTime.time.minutes.minutes.toLong(),
            id = logId.id.toLong()
        )
    }

    fun firstLogOfDay(date: Date): Log? = logOfDay(date, LogType.FIRST_OF_DAY)

    fun lastLogOfDay(date: Date): Log? = logOfDay(date, LogType.LAST_OF_DAY)

    fun lastLogOfMorning(date: Date): Log? = logOfDay(date, LogType.LAST_OF_MORNING)

    fun firstLogOfAfternoon(date: Date): Log? = logOfDay(date, LogType.FIRST_OF_AFTERNOON)

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

    private fun log(log: String) {
        if (debugLogs) println(log)
    }
}

expect fun getSqliteDriver(schema: SqlDriver.Schema, databasePath: String): SqlDriver

private fun map(
    id: Long,
    type: Long,
    year: Long,
    month: Long,
    day: Long,
    hour: Long,
    minute: Long
) = Log(
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

private fun Month.Companion.fromDbRepresentation(dbRepresentation: Long): Month = Month.values()[(dbRepresentation - 1).toInt()]

private val Month.dbRepresentation: Long
    get() = (ordinal + 1).toLong()

private fun LogType.Companion.fromDbRepresentation(dbRepresentation: Long): LogType = LogType.values()[dbRepresentation.toInt()]

private val LogType.dbRepresentation: Long
    get() = ordinal.toLong()
