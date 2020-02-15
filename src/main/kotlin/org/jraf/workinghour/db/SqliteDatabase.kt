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

import org.jraf.workinghour.datetime.DateTime
import java.io.File
import java.sql.DriverManager

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

    private val insertEventStatement by lazy {
        connection.prepareStatement("INSERT INTO activity_event (year, month, day, hour, minute, event_type) VALUES (?, ?, ?, ?, ?, ?)")
    }

    @Synchronized
    fun logEvent(dateTime: DateTime, event: Event) {
        insertEventStatement.apply {
            setInt(1, dateTime.date.year.year)
            setInt(2, dateTime.date.month.ordinal)
            setInt(3, dateTime.date.day.dayOfMonth)
            setInt(4, dateTime.timeOfDay.hour.hour)
            setInt(5, dateTime.timeOfDay.minutes.minutes)
            setInt(6, event.dbRepresentation)
        }.execute()
    }

    enum class Event(val dbRepresentation: Int) {
        INACTIVE(0),
        ACTIVE(1)
    }
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
