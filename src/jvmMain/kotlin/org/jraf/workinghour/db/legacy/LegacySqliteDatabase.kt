/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2018 Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.workinghour.db.legacy

import org.jraf.workinghour.datetime.DateTime
import org.jraf.workinghour.db.Database
import java.io.File
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Calendar
import java.util.Date
import kotlin.time.ExperimentalTime

@ExperimentalTime
class LegacySqliteDatabase(
    private val file: File,
    private val database: Database
) {
    private val connection by lazy {
        Class.forName("net.sf.log4jdbc.DriverSpy")
        val connection = DriverManager.getConnection("jdbc:log4jdbc:sqlite:${file.canonicalPath}")
        connection
    }

    private val selectFirstLogAfter by lazy {
        connection.prepareStatement(
            """
            SELECT year, month, day, hour, minute
            FROM minute
            ORDER BY id
            """.trimIndent()
        )
    }

    fun logEverything() {
        val resultSet = selectFirstLogAfter.executeQuery()
        while (!resultSet.isAfterLast) {
            resultSet.next()

            val dateTime = DateTime.build(
                resultSet.getInt(1),
                resultSet.getInt(2) - 1,
                resultSet.getInt(3),
                resultSet.getInt(4),
                resultSet.getInt(5)
            )

            database.logActive(dateTime)
        }
    }

    private fun dateFromResultSet(resultSet: ResultSet): Date? {
        if (!resultSet.isBeforeFirst) return null
        val res = Calendar.getInstance()
        res[Calendar.YEAR] = resultSet.getInt(1)
        res[Calendar.MONTH] = resultSet.getInt(2) - 1
        res[Calendar.DAY_OF_MONTH] = resultSet.getInt(3)
        res[Calendar.HOUR_OF_DAY] = resultSet.getInt(4)
        res[Calendar.MINUTE] = resultSet.getInt(5)
        res[Calendar.SECOND] = 0
        res[Calendar.MILLISECOND] = 0
        return res.time
    }
}