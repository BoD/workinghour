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

package org.jraf.workinghour.main

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jraf.workinghour.conf.Configuration
import org.jraf.workinghour.daemon.Daemon
import org.jraf.workinghour.datetime.DateTime
import org.jraf.workinghour.datetime.Time
import org.jraf.workinghour.db.Database
import org.jraf.workinghour.db.legacy.LegacySqliteDatabase
import org.jraf.workinghour.util.ansi.ANSI_CLEAR_SCREEN
import org.jraf.workinghour.util.duration.formatHourMinutes
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.ExperimentalTime
import kotlin.time.days
import kotlin.time.hours
import kotlin.time.minutes

@ExperimentalTime
private val configuration by lazy {
    Configuration(
        databaseFile = File(
            "workinghour.db"
        ),
        startOfDay = Time.build(8, 30),
        endOfMorning = Time.build(13, 0),
        startOfAfternoon = Time.build(13, 0),
        endOfDay = Time.build(21, 30),
        validDayMinimumDuration = 6.hours
    )
}

@ExperimentalStdlibApi
@ExperimentalTime
fun main() {
    println("Hello, World!")
    val db = Database(configuration)
    val daemon = Daemon(db).apply { start() }

    // Display stats every minute
    GlobalScope.launch {
        while (true) {
            displayStats(db)
            delay(1.minutes.toLongMilliseconds())
        }
    }

    waitForever()
}

@ExperimentalStdlibApi
@ExperimentalTime
private fun displayStats(db: Database) {
    // Clear screen
    print(ANSI_CLEAR_SCREEN)

    // Days
    val todayNow = DateTime.todayNow()
    var dateTime = todayNow + 1.days
    for (i in 0..7) {
        dateTime -= 1.days
        while (dateTime.date.isWeekend) dateTime -= 1.days
        val date = dateTime.date
        val formattedWeekDay = date.toFormattedWeekDay()
        val firstLogOfDay = db.firstLogOfDay(date)
        val lastLogOfMorning = db.lastLogOfMorning(date)
        val firstLogOfAfternoon = db.firstLogOfAfternoon(date)
        val lastLogOfDay = db.lastLogOfDay(date)
        val workDurationForDate = db.workDurationForDay(firstLogOfDay, lastLogOfMorning, firstLogOfAfternoon, lastLogOfDay)
        println(
            "$formattedWeekDay:".padEnd(11)
                    + workDurationForDate.formatHourMinutes().padEnd(8)
                    + (firstLogOfDay?.toFormattedString() ?: "?")
                    + " - ${lastLogOfMorning?.toFormattedString() ?: "?"}"
                    + "  ${firstLogOfAfternoon?.toFormattedString() ?: "?"}"
                    + " - ${lastLogOfDay?.toFormattedString() ?: "?"}"
        )
    }

    println()

    // Weeks
    var day = todayNow.date
    for (i in 0..4) {
        val workDurationForWeek = db.workDurationForWeek(day)
        val weekAgoStr = when (i) {
            0 -> "This week:"
            1 -> "Last week:"
            else -> "$i weeks ago:"
        }.padEnd(12)
        println("$weekAgoStr ${workDurationForWeek.formatHourMinutes()}")
        day -= 7.days
    }

    println()

    // Total average
    val startDate = todayNow.date - 365.days
    val averageWorkDurationPerDayResults = db.averageWorkDurationPerDay(startDate = startDate, endDate = todayNow.date)
    val averageWorkDurationPerDay = averageWorkDurationPerDayResults.averageWorkDurationPerDay
    val averageWorkDurationPerWeek = averageWorkDurationPerDay * 5
    val numberOfWorkingDays = averageWorkDurationPerDayResults.numberOfWorkingDays
    val earliestDay = averageWorkDurationPerDayResults.earliestDay
    println(
        "Average:  ${averageWorkDurationPerDay.formatHourMinutes()}/day (${averageWorkDurationPerWeek.formatHourMinutes()}/week) "
                + "since ${earliestDay?.toFormattedFullDate()} ($numberOfWorkingDays working days)"
    )
}

private fun waitForever() {
    Object().let {
        synchronized(it) {
            it.wait()
        }
    }
}

@ExperimentalTime
fun migrateLegacyDb(legacyDbFile: File, db: Database) {
    val legacySqliteDatabase = LegacySqliteDatabase(legacyDbFile, db)
    legacySqliteDatabase.logEverything()
}

@ExperimentalTime
private fun createTestDb(db: Database) {
    val todayNow = DateTime.todayNow()
    val random = Random(System.currentTimeMillis())
    for (i in 0..400) {
        val dateTime = todayNow - i.days
        db.logActive(dateTime.copy(time = Time.build(9, random.nextInt(0..49))))
        db.logActive(dateTime.copy(time = Time.build(10, 20)))
        db.logActive(dateTime.copy(time = Time.build(11, 30)))
        db.logActive(dateTime.copy(time = Time.build(12, random.nextInt(0..40))))
        db.logActive(dateTime.copy(time = Time.build(13, random.nextInt(0..59))))
        db.logActive(dateTime.copy(time = Time.build(14, 20)))
        db.logActive(dateTime.copy(time = Time.build(15, 30)))
        db.logActive(dateTime.copy(time = Time.build(16, 30)))
        db.logActive(dateTime.copy(time = Time.build(16, 30)))
        db.logActive(dateTime.copy(time = Time.build(17, 30)))
        db.logActive(dateTime.copy(time = Time.build(18, 45)))
        db.logActive(dateTime.copy(time = Time.build(19, random.nextInt(0..59))))
    }
}