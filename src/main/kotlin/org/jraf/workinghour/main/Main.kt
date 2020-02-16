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

import org.jraf.workinghour.conf.Configuration
import org.jraf.workinghour.datetime.DateTime
import org.jraf.workinghour.datetime.Hour
import org.jraf.workinghour.datetime.Minutes
import org.jraf.workinghour.datetime.Time
import org.jraf.workinghour.db.Database
import org.jraf.workinghour.util.duration.formatHourMinutes
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.ExperimentalTime
import kotlin.time.days

@ExperimentalTime
fun main() {
    println("Hello, World!")
//    val daemon = Daemon(
//        configuration = Configuration(
//            databaseFile = File(
//                "workinghour.db"
//            ),
//            startOfDay = Time(Hour(6), Minutes(0)),
//            endOfMorning = Time(Hour(13), Minutes(0)),
//            startOfAfternoon = Time(Hour(13), Minutes(0)),
//            endOfDay = Time(Hour(23), Minutes(50))
//        )//,
////        activityMonitoringPeriod = 1.seconds
//    ).apply { start() }
//    Object().let {
//        synchronized(it) {
//            it.wait()
//        }
//    }

//    createTestDb()

    val db = Database(
        Configuration(
            databaseFile = File(
                "workinghour.db"
            ),
            startOfDay = Time(Hour(6), Minutes(0)),
            endOfMorning = Time(Hour(13), Minutes(0)),
            startOfAfternoon = Time(Hour(13), Minutes(0)),
            endOfDay = Time(Hour(23), Minutes(50))
        )
    )

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
        val workDurationForDate = db.workDurationForDate(firstLogOfDay, lastLogOfMorning, firstLogOfAfternoon, lastLogOfDay)
        println(
            "$formattedWeekDay:"
                    + "  🛬 ${firstLogOfDay?.toFormattedString()}"
                    + "  🔜 ${lastLogOfMorning?.toFormattedString()}"
                    + "  🔙 ${firstLogOfAfternoon?.toFormattedString()}"
                    + "  🛫 ${lastLogOfDay?.toFormattedString()}"
                    + "  ${workDurationForDate.formatHourMinutes()}"
        )
    }
}

@ExperimentalTime
private fun createTestDb() {
    val db = Database(
        Configuration(
            databaseFile = File(
                "workinghour.db"
            ),
            startOfDay = Time(Hour(6), Minutes(0)),
            endOfMorning = Time(Hour(13), Minutes(0)),
            startOfAfternoon = Time(Hour(13), Minutes(0)),
            endOfDay = Time(Hour(23), Minutes(50))
        )
    )
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