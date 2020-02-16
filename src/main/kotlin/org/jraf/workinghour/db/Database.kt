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

import org.jraf.workinghour.conf.Configuration
import org.jraf.workinghour.datetime.Date
import org.jraf.workinghour.datetime.DateTime
import org.jraf.workinghour.datetime.Time
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class Database(
    private val configuration: Configuration
) {
    private val sqliteDatabase = SqliteDatabase(configuration.databaseFile)

    fun logActive(dateTime: DateTime) {
        // Ignore "very late" (early the next day) logs
        if (dateTime.time < configuration.startOfDay) return

        // Ignore late logs
        if (dateTime.time > configuration.endOfDay) return

        updateFirstLogOfDay(dateTime)
        updateLastLogOfMorning(dateTime, configuration.endOfMorning)
        updateFirstLogOfAfternoon(dateTime, configuration.startOfAfternoon)
        updateLastLogOfDay(dateTime)
    }

    private fun updateFirstLogOfDay(todayNow: DateTime) {
        val firstLogOfDay = sqliteDatabase.firstLogOfDay(todayNow.date)
        if (firstLogOfDay == null) sqliteDatabase.insertLog(LogType.FIRST_OF_DAY, todayNow)
    }

    private fun updateLastLogOfMorning(todayNow: DateTime, endOfMorning: Time) {
        val lastLogOfMorning = sqliteDatabase.lastLogOfMorning(todayNow.date)
        if (todayNow.time <= endOfMorning) {
            if (lastLogOfMorning == null) {
                sqliteDatabase.insertLog(LogType.LAST_OF_MORNING, todayNow)
            } else {
                sqliteDatabase.updateLogDateTime(lastLogOfMorning.id, todayNow)
            }
        }
    }

    private fun updateFirstLogOfAfternoon(todayNow: DateTime, startOfAfternoon: Time) {
        val firstLogOfAfternoon = sqliteDatabase.firstLogOfAfternoon(todayNow.date)
        if (firstLogOfAfternoon == null && todayNow.time >= startOfAfternoon) sqliteDatabase.insertLog(LogType.FIRST_OF_AFTERNOON, todayNow)
    }

    private fun updateLastLogOfDay(todayNow: DateTime) {
        val lastLogOfDay = sqliteDatabase.lastLogOfDay(todayNow.date)
        if (lastLogOfDay == null) {
            sqliteDatabase.insertLog(LogType.LAST_OF_DAY, todayNow)
        } else {
            sqliteDatabase.updateLogDateTime(lastLogOfDay.id, todayNow)
        }
    }


    fun firstLogOfDay(date: Date): Time? {
        return sqliteDatabase.firstLogOfDay(date)?.dateTime?.time
    }

    fun lastLogOfMorning(date: Date): Time? {
        return sqliteDatabase.lastLogOfMorning(date)?.dateTime?.time
    }

    fun firstLogOfAfternoon(date: Date): Time? {
        return sqliteDatabase.firstLogOfAfternoon(date)?.dateTime?.time
    }

    fun lastLogOfDay(date: Date): Time? {
        return sqliteDatabase.lastLogOfDay(date)?.dateTime?.time
    }

    fun workDurationForDate(date: Date): Duration {
        TODO()
    }

    fun workDurationForWeek(dayIncludedInWeek: Date): Duration {
        TODO()
    }

    fun averageWorkDurationPerDay(since: Date): Duration {
        TODO()
    }
}