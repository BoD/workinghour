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
import org.jraf.workinghour.datetime.TimeOfDay
import java.io.File
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class Database(databaseFile: File) {
    private val sqliteDatabase = SqliteDatabase(databaseFile)

    fun logActive() {
        sqliteDatabase.logEvent(DateTime.now(), SqliteDatabase.EventType.ACTIVE)
    }

    fun logInactive() {
        sqliteDatabase.logEvent(DateTime.now(), SqliteDatabase.EventType.INACTIVE)
    }

    fun startOfWorkDay(date: CalendarDate): TimeOfDay? {
        return sqliteDatabase.startOfWorkDay(date)
    }

    fun startOfLunchBreak(date: CalendarDate): TimeOfDay {
        TODO()
    }

    fun endOfLunchBreak(date: CalendarDate): TimeOfDay {
        TODO()
    }

    fun endOfWorkDay(date: CalendarDate): TimeOfDay? {
        return sqliteDatabase.endOfWorkDay(date)
    }

    fun workDurationForDate(date: CalendarDate): Duration {
        TODO()
    }

    fun workDurationForWeek(dayIncludedInWeek: CalendarDate): Duration {
        TODO()
    }

    fun averageWorkDurationPerDay(since: CalendarDate): Duration {
        TODO()
    }
}