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

package org.jraf.workinghour

import java.io.File
import java.sql.DriverManager
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Database(private val directory: File) {
    companion object {
        private val ID_DATE_FORMAT = SimpleDateFormat("yyyyMMddHHmm")
        private const val MAX_ID = 999999999999
        private const val FILE_NAME = "workinghour.db"
    }

    private val connection by lazy {
        Class.forName("net.sf.log4jdbc.DriverSpy")
        val connection = DriverManager.getConnection("jdbc:log4jdbc:sqlite:${File(directory, FILE_NAME).canonicalPath}")
        connection.createStatement()
            .executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS minute (
                    id INTEGER PRIMARY KEY ON CONFLICT IGNORE NOT NULL,
                    year INTEGER NOT NULL,
                    month INTEGER NOT NULL,
                    day INTEGER NOT NULL,
                    hour INTEGER NOT NULL,
                    minute INTEGER NOT NULL,
                    day_of_week INTEGER NOT NULL
                )
                """.trimIndent()
            )
        connection
    }

    private val insertMinute by lazy {
        connection.prepareStatement("INSERT INTO minute (id, year, month, day, hour, minute, day_of_week) VALUES (?, ?, ?, ?, ?, ?, ?)")
    }

    private val selectMinutesWorkedOnDay by lazy {
        connection.prepareStatement(
            """
            SELECT count(*)
            FROM minute
            WHERE
            year = ?
            AND month = ?
            AND day = ?
             """.trimIndent()
        )
    }

    private val selectMinutesWorkedThisMonth by lazy {
        connection.prepareStatement(
            """
            SELECT count(*)
            FROM minute
            WHERE
            year = ?
            AND month = ?
            """.trimIndent()
        )
    }

    private val selectMinutesWorkedBetweenDates by lazy {
        connection.prepareStatement("SELECT count(*) FROM minute WHERE id >= ? AND id < ?")
    }

    private val selectFirstLog by lazy {
        connection.prepareStatement("SELECT year, month, day, hour, minute FROM minute ORDER BY id")
    }

    private val selectFirstLogAfter by lazy {
        connection.prepareStatement(
            """
            SELECT year, month, day, hour, minute
            FROM minute
            WHERE
            year = ?
            AND month = ?
            AND day = ?
            AND ((hour = ? AND minute >= ?) OR (hour > ?))
            ORDER BY id
            LIMIT 1
            """.trimIndent()
        )
    }

    private val selectLastLogBefore by lazy {
        connection.prepareStatement(
            """
            SELECT year, month, day, hour, minute
            FROM minute
            WHERE
            year = ?
            AND month = ?
            AND day = ?
            AND ((hour = ? AND minute <= ?) OR (hour < ?))
            ORDER BY id DESC
            LIMIT 1
            """.trimIndent()
        )
    }

    private val selectAverageMinutesPerDay by lazy {
        connection.prepareStatement(
            """
            SELECT minutes / days
            FROM (
                SELECT count(*) as days,
                (SELECT count(*) FROM minute) as minutes
                FROM
                (SELECT DISTINCT year, month, day FROM minute)
            )
            """.trimIndent()
        )
    }

    private val selectTotalRows by lazy {
        connection.prepareStatement(
            """
            SELECT count(*)
            FROM minute
            """.trimIndent()
        )
    }

    private val selectTotalDays by lazy {
        connection.prepareStatement(
            """
            SELECT count(*)
            FROM
            (SELECT DISTINCT year, month, day FROM minute)
            """.trimIndent()
        )
    }


    @Suppress("NOTHING_TO_INLINE")
    private inline fun getIdForCalendar(cal: Calendar) = ID_DATE_FORMAT.format(cal.time).toLong()

    @Synchronized
    fun logMinute() {
        val now = Calendar.getInstance()
        logMinute(now)
    }

    @Synchronized
    fun logTestData() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -60)
        for (i in 1..60) {
            cal.add(Calendar.DAY_OF_WEEK, 1)
            // Skip weekends
            if (cal[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || cal[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) continue
            // Morning
            cal[Calendar.HOUR_OF_DAY] = 9
            cal[Calendar.MINUTE] = 12
            while (true) {
                cal.add(Calendar.MINUTE, 1)
                if (Math.random() < 1F / 180F) {
                    // Random 20 min break
                    cal.add(Calendar.MINUTE, 10)
                }
                logMinute(cal)

                if (cal[Calendar.HOUR_OF_DAY] >= 12) break
            }
            // Afternoon
            cal[Calendar.HOUR_OF_DAY] = 13
            cal[Calendar.MINUTE] = 32
            while (true) {
                cal.add(Calendar.MINUTE, 1)
                if (Math.random() < 1F / 180F) {
                    // Random 15 min break
                    cal.add(Calendar.MINUTE, 20)
                }
                logMinute(cal)

                if (cal[Calendar.HOUR_OF_DAY] >= 19) break
            }
        }
    }

    @Synchronized
    private fun logMinute(cal: Calendar) {
        insertMinute.apply {
            setLong(1, getIdForCalendar(cal))
            setInt(2, cal[Calendar.YEAR])
            setInt(3, cal[Calendar.MONTH] + 1)
            setInt(4, cal[Calendar.DAY_OF_MONTH])
            setInt(5, cal[Calendar.HOUR_OF_DAY])
            setInt(6, cal[Calendar.MINUTE])
            setInt(7, cal[Calendar.DAY_OF_WEEK])
        }.execute()
    }

    @Synchronized
    fun minutesWorkedOnDay(cal: Calendar): Int {
        return selectMinutesWorkedOnDay.apply {
            setInt(1, cal[Calendar.YEAR])
            setInt(2, cal[Calendar.MONTH] + 1)
            setInt(3, cal[Calendar.DAY_OF_MONTH])
        }.executeQuery().getInt(1)
    }

    @Synchronized
    fun minutesWorkedThisMonth(): Int {
        val now = Calendar.getInstance()
        return selectMinutesWorkedThisMonth.apply {
            setInt(1, now[Calendar.YEAR])
            setInt(2, now[Calendar.MONTH] + 1)
        }.executeQuery().getInt(1)
    }

    @Synchronized
    fun minutesWorkedLast30Days(): Int {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -30)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        return selectMinutesWorkedBetweenDates.apply {
            setLong(1, getIdForCalendar(cal))
            setLong(2, MAX_ID)
        }.executeQuery().getInt(1)
    }

    @Synchronized
    fun minutesWorkedOnWeek(dayInWeek: Calendar): Int {
        val from = dayInWeek.clone() as Calendar
        from[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        from[Calendar.HOUR_OF_DAY] = 0
        from[Calendar.MINUTE] = 0
        val to = from.clone() as Calendar
        to.add(Calendar.WEEK_OF_YEAR, 1)
        return selectMinutesWorkedBetweenDates.apply {
            setLong(1, getIdForCalendar(from))
            setLong(2, getIdForCalendar(to))
        }.executeQuery().getInt(1)
    }

    @Synchronized
    fun minutesWorkedTotal(): Int {
        return selectMinutesWorkedBetweenDates.apply {
            setInt(1, 0)
            setLong(2, MAX_ID)
        }.executeQuery().getInt(1)
    }

    @Synchronized
    fun firstLog(): Date {
        val resultSet = selectFirstLog.executeQuery()
        return dateFromResultSet(resultSet)!!
    }

    @Synchronized
    fun averageMinutesPerDay(): Int {
        return selectAverageMinutesPerDay.executeQuery().getInt(1)
    }

    @Synchronized
    fun firstLogOfMorning(cal: Calendar, atLeastHour: Int, atLeastMinute: Int): Date? {
        val resultSet = selectFirstLogAfter.apply {
            setInt(1, cal[Calendar.YEAR])
            setInt(2, cal[Calendar.MONTH] + 1)
            setInt(3, cal[Calendar.DAY_OF_MONTH])
            setInt(4, atLeastHour)
            setInt(5, atLeastMinute)
            setInt(6, atLeastHour)
        }.executeQuery()
        return dateFromResultSet(resultSet)
    }

    @Synchronized
    fun lastLogOfMorning(cal: Calendar, atMostHour: Int, atMostMinute: Int): Date? {
        val resultSet = selectLastLogBefore.apply {
            setInt(1, cal[Calendar.YEAR])
            setInt(2, cal[Calendar.MONTH] + 1)
            setInt(3, cal[Calendar.DAY_OF_MONTH])
            setInt(4, atMostHour)
            setInt(5, atMostMinute)
            setInt(6, atMostHour)
        }.executeQuery()
        return dateFromResultSet(resultSet)
    }

    @Synchronized
    fun firstLogOfEvening(cal: Calendar, atLeastHour: Int, atLeastMinute: Int): Date? {
        val resultSet = selectFirstLogAfter.apply {
            setInt(1, cal[Calendar.YEAR])
            setInt(2, cal[Calendar.MONTH] + 1)
            setInt(3, cal[Calendar.DAY_OF_MONTH])
            setInt(4, atLeastHour)
            setInt(5, atLeastMinute)
            setInt(6, atLeastHour)
        }.executeQuery()
        return dateFromResultSet(resultSet)
    }

    @Synchronized
    fun lastLogOfEvening(cal: Calendar, atMostHour: Int, atMostMinute: Int): Date? {
        val resultSet = selectLastLogBefore.apply {
            setInt(1, cal[Calendar.YEAR])
            setInt(2, cal[Calendar.MONTH] + 1)
            setInt(3, cal[Calendar.DAY_OF_MONTH])
            setInt(4, atMostHour)
            setInt(5, atMostMinute)
            setInt(6, atMostHour)
        }.executeQuery()
        return dateFromResultSet(resultSet)
    }

    @Synchronized
    fun totalRows(): Long {
        return selectTotalRows.executeQuery().getLong(1)
    }

    @Synchronized
    fun totalDays(): Long {
        return selectTotalDays.executeQuery().getLong(1)
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