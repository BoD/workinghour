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

package org.jraf.workinghour.db

import org.jraf.workinghour.datetime.CalendarDate
import org.jraf.workinghour.datetime.DateTime
import org.jraf.workinghour.datetime.DayOfMonth
import org.jraf.workinghour.datetime.Hour
import org.jraf.workinghour.datetime.Minutes
import org.jraf.workinghour.datetime.Month
import org.jraf.workinghour.datetime.TimeOfDay
import org.jraf.workinghour.datetime.Year
import org.jraf.workinghour.util.jdbc.intParams
import java.io.File
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class SqliteDatabase(private val databaseFile: File) {

    private val connection by lazy {
        Class.forName("net.sf.log4jdbc.DriverSpy")
        val connection = DriverManager.getConnection("jdbc:log4jdbc:sqlite:${databaseFile.canonicalPath}")
        connection.createStatement()
            .executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS activity_event (
                    id INTEGER PRIMARY KEY ON CONFLICT IGNORE NOT NULL,
                    year INTEGER NOT NULL,
                    month INTEGER NOT NULL,
                    day INTEGER NOT NULL,
                    hour INTEGER NOT NULL,
                    minute INTEGER NOT NULL,
                    event_type INTEGER NOT NULL
                )
                """.trimIndent()
            )
        connection
    }

    private val insertEventStatement: PreparedStatement by lazy {
        connection.prepareStatement("INSERT INTO activity_event (year, month, day, hour, minute, event_type) VALUES (?, ?, ?, ?, ?, ?)")
    }

    private val selectFirstActiveEventForDayStatement: PreparedStatement by lazy {
        connection.prepareStatement(
            """
            SELECT year, month, day, hour, minute, event_type
            FROM activity_event
            WHERE
            year = ?
            AND month = ?
            AND day = ?
            AND event_type = ?
            ORDER BY id
            LIMIT 1
            """.trimIndent()
        )
    }

    private val selectLastActiveEventForDayStatement: PreparedStatement by lazy {
        connection.prepareStatement(
            """
            SELECT year, month, day, hour, minute, event_type
            FROM activity_event
            WHERE
            year = ?
            AND month = ?
            AND day = ?
            AND event_type = ?
            ORDER BY id DESC
            LIMIT 1
            """.trimIndent()
        )
    }


    @Synchronized
    fun logEvent(dateTime: DateTime, eventType: EventType) {
        insertEventStatement.intParams(
            dateTime.date.year.year,
            dateTime.date.month.dbRepresentation,
            dateTime.date.day.dayOfMonth,
            dateTime.timeOfDay.hour.hour,
            dateTime.timeOfDay.minutes.minutes,
            eventType.dbRepresentation
        ).execute()
    }

    fun startOfWorkDay(date: CalendarDate): TimeOfDay? {
        val resultSet = selectFirstActiveEventForDayStatement.intParams(
            date.year.year,
            date.month.dbRepresentation,
            date.day.dayOfMonth,
            EventType.ACTIVE.dbRepresentation
        ).executeQuery()
        return eventFromResultSet(resultSet)?.dateTime?.timeOfDay
    }

    fun endOfWorkDay(date: CalendarDate): TimeOfDay? {
        val resultSet = selectLastActiveEventForDayStatement.intParams(
            date.year.year,
            date.month.dbRepresentation,
            date.day.dayOfMonth,
            EventType.ACTIVE.dbRepresentation
        ).executeQuery()
        return eventFromResultSet(resultSet)?.dateTime?.timeOfDay
    }

    private fun eventFromResultSet(resultSet: ResultSet): Event? {
        if (!resultSet.isBeforeFirst) return null
        return Event(
            DateTime(
                CalendarDate(
                    Year(resultSet.getInt(1)),
                    Month.fromDbRepresentation(resultSet.getInt(2)),
                    DayOfMonth(resultSet.getInt(3))
                ),
                TimeOfDay(
                    Hour(resultSet.getInt(4)),
                    Minutes(resultSet.getInt(5))
                )
            ),
            EventType.fromDbRepresentation(resultSet.getInt(6))
        )
    }

    enum class EventType(val dbRepresentation: Int) {
        INACTIVE(0),
        ACTIVE(1);

        companion object {
            fun fromDbRepresentation(dbRepresentation: Int): EventType = when (dbRepresentation) {
                0 -> INACTIVE
                1 -> ACTIVE
                else -> throw IllegalArgumentException("Unknown EventType $dbRepresentation")
            }
        }
    }

    data class Event(
        val dateTime: DateTime,
        val eventType: EventType
    )
}

private fun DateTime.toId(): Long {
    return "%1\$d%2\$02d%3\$02d%4\$02d%5\$02d".format(
        date.year.year,
        date.month.ordinal,
        date.day.dayOfMonth,
        timeOfDay.hour.hour,
        timeOfDay.minutes.minutes
    ).toLong()
}

private val Month.dbRepresentation: Int
    get() = ordinal + 1

private fun Month.Companion.fromDbRepresentation(dbRepresentation: Int) = Month.values()[dbRepresentation - 1]