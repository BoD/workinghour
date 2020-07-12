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

import org.jraf.workinghour.datetime.Date
import org.jraf.workinghour.datetime.DateTime
import org.jraf.workinghour.datetime.DayOfMonth
import org.jraf.workinghour.datetime.Hour
import org.jraf.workinghour.datetime.Minutes
import org.jraf.workinghour.datetime.Month
import org.jraf.workinghour.datetime.Time
import org.jraf.workinghour.datetime.Year
import org.jraf.workinghour.util.jdbc.intParams
import java.io.File
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

actual class Database actual constructor(databasePath: String) {
    private val databaseFile = File(databasePath)

    private val connection by lazy {
        Class.forName("net.sf.log4jdbc.DriverSpy")
        val connection = DriverManager.getConnection("jdbc:log4jdbc:sqlite:${databaseFile.canonicalPath}")
        connection.createStatement()
            .executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_NAME_ACTIVITY_LOG (
                    $COLUMN_ACTIVITY_LOG_ID INTEGER PRIMARY KEY ON CONFLICT REPLACE NOT NULL,
                    $COLUMN_ACTIVITY_LOG_TYPE INTEGER NOT NULL,
                    $COLUMN_ACTIVITY_LOG_YEAR INTEGER NOT NULL,
                    $COLUMN_ACTIVITY_LOG_MONTH INTEGER NOT NULL,
                    $COLUMN_ACTIVITY_LOG_DAY INTEGER NOT NULL,
                    $COLUMN_ACTIVITY_LOG_HOUR INTEGER NOT NULL,
                    $COLUMN_ACTIVITY_LOG_MINUTE INTEGER NOT NULL
                )
                """.trimIndent()
            )
        connection
    }

    private val insertEventStatement: PreparedStatement by lazy {
        connection.prepareStatement(
            """
            INSERT INTO $TABLE_NAME_ACTIVITY_LOG (
                $COLUMN_ACTIVITY_LOG_TYPE, 
                $COLUMN_ACTIVITY_LOG_YEAR, 
                $COLUMN_ACTIVITY_LOG_MONTH, 
                $COLUMN_ACTIVITY_LOG_DAY, 
                $COLUMN_ACTIVITY_LOG_HOUR, 
                $COLUMN_ACTIVITY_LOG_MINUTE
            ) VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
        )
    }

    private val selectLogOfDayWithTypeStatement: PreparedStatement by lazy {
        connection.prepareStatement(
            """
            SELECT $ACTIVITY_LOG_ALL_COLUMNS
            FROM $TABLE_NAME_ACTIVITY_LOG
            WHERE
            $COLUMN_ACTIVITY_LOG_YEAR = ?
            AND $COLUMN_ACTIVITY_LOG_MONTH = ?
            AND $COLUMN_ACTIVITY_LOG_DAY = ?
            AND $COLUMN_ACTIVITY_LOG_TYPE = ?
            """.trimIndent()
        )
    }

    private val updateLogDateTimeStatement: PreparedStatement by lazy {
        connection.prepareStatement(
            """
            UPDATE $TABLE_NAME_ACTIVITY_LOG
            SET
                $COLUMN_ACTIVITY_LOG_YEAR = ?,
                $COLUMN_ACTIVITY_LOG_MONTH = ?,
                $COLUMN_ACTIVITY_LOG_DAY = ?,
                $COLUMN_ACTIVITY_LOG_HOUR = ?,
                $COLUMN_ACTIVITY_LOG_MINUTE = ?
            WHERE
            $COLUMN_ACTIVITY_LOG_ID = ?
            """.trimIndent()
        )
    }

    @Synchronized
    actual fun insertLog(logType: LogType, dateTime: DateTime) {
        insertEventStatement.intParams(
            logType.dbRepresentation,
            dateTime.date.year.year,
            dateTime.date.month.dbRepresentation,
            dateTime.date.day.dayOfMonth,
            dateTime.time.hour.hour,
            dateTime.time.minutes.minutes
        ).execute()
    }

    @Synchronized
    actual fun updateLogDateTime(logId: LogId, dateTime: DateTime) {
        updateLogDateTimeStatement.intParams(
            dateTime.date.year.year,
            dateTime.date.month.dbRepresentation,
            dateTime.date.day.dayOfMonth,
            dateTime.time.hour.hour,
            dateTime.time.minutes.minutes,
            logId.id
        ).execute()
    }

    @Synchronized
    actual fun firstLogOfDay(date: Date): Log? = logOfDayWithType(date, LogType.FIRST_OF_DAY)

    @Synchronized
    actual fun lastLogOfDay(date: Date): Log? = logOfDayWithType(date, LogType.LAST_OF_DAY)

    @Synchronized
    actual fun lastLogOfMorning(date: Date): Log? = logOfDayWithType(date, LogType.LAST_OF_MORNING)

    @Synchronized
    actual fun firstLogOfAfternoon(date: Date): Log? = logOfDayWithType(date, LogType.FIRST_OF_AFTERNOON)

    private fun logOfDayWithType(date: Date, logType: LogType): Log? {
        val resultSet = selectLogOfDayWithTypeStatement.intParams(
            date.year.year,
            date.month.dbRepresentation,
            date.day.dayOfMonth,
            logType.dbRepresentation
        ).executeQuery()
        return logFromResultSet(resultSet)
    }

    private fun logFromResultSet(resultSet: ResultSet): Log? {
        if (!resultSet.isBeforeFirst) return null
        return Log(
            id = LogId(resultSet.getInt(1)),
            logType = LogType.fromDbRepresentation(resultSet.getInt(2)),
            dateTime = DateTime(
                Date(
                    Year(resultSet.getInt(3)),
                    Month.fromDbRepresentation(resultSet.getInt(4)),
                    DayOfMonth(resultSet.getInt(5))
                ),
                Time(
                    Hour(resultSet.getInt(6)),
                    Minutes(resultSet.getInt(7))
                )
            )
        )
    }

    companion object {
        private const val TABLE_NAME_ACTIVITY_LOG = "activity_log"
        private const val COLUMN_ACTIVITY_LOG_ID = "id"
        private const val COLUMN_ACTIVITY_LOG_TYPE = "type"
        private const val COLUMN_ACTIVITY_LOG_YEAR = "year"
        private const val COLUMN_ACTIVITY_LOG_MONTH = "month"
        private const val COLUMN_ACTIVITY_LOG_DAY = "day"
        private const val COLUMN_ACTIVITY_LOG_HOUR = "hour"
        private const val COLUMN_ACTIVITY_LOG_MINUTE = "minute"

        private val ACTIVITY_LOG_ALL_COLUMNS = """
            $COLUMN_ACTIVITY_LOG_ID,
            $COLUMN_ACTIVITY_LOG_TYPE,
            $COLUMN_ACTIVITY_LOG_YEAR,
            $COLUMN_ACTIVITY_LOG_MONTH,
            $COLUMN_ACTIVITY_LOG_DAY,
            $COLUMN_ACTIVITY_LOG_HOUR,
            $COLUMN_ACTIVITY_LOG_MINUTE
        """.trimIndent()
    }
}

private val Month.dbRepresentation: Int
    get() = ordinal + 1

private fun Month.Companion.fromDbRepresentation(dbRepresentation: Int) = Month.values()[dbRepresentation - 1]
