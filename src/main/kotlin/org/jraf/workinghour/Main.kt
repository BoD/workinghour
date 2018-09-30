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

import com.beust.jcommander.JCommander
import org.jraf.workinghour.util.ANSI_CLEAR_SCREEN
import org.jraf.workinghour.util.blue
import org.jraf.workinghour.util.bold
import org.jraf.workinghour.util.formatDay
import org.jraf.workinghour.util.formatDuration
import org.jraf.workinghour.util.formatHourMinute
import org.jraf.workinghour.util.formatWeekDay
import org.jraf.workinghour.util.purple
import org.jraf.workinghour.util.underline
import org.jraf.workinghour.util.workingDayAgo
import org.jraf.workinghour.util.yellow
import java.awt.MouseInfo
import java.awt.Point
import java.io.File
import java.io.PrintStream
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class Main {
    private var latestMouseLocation: Point? = null
    private lateinit var database: Database
    private lateinit var statsFile: File

    fun go(av: Array<String>) {
//    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn")

        val arguments = Arguments()
        val jCommander = JCommander.newBuilder()
            .addObject(arguments)
            .build()
        jCommander.parse(*av)

        if (arguments.help) {
            jCommander.usage()
            return
        }

        if (!arguments.path.isDirectory) {
            System.err.println("Could not find ${arguments.path} or it is not a directory")
            System.exit(-1)
            return
        }

        statsFile = File(arguments.path, STATS_FILE_NAME)

        database = Database(arguments.path)
//    database.logTestData()

        println("Logging to ${arguments.path}")
        while (true) {
            logIfActivityDetected()
            Thread.sleep(TimeUnit.MINUTES.toMillis(1))
        }
    }

    private fun logIfActivityDetected() {
        val newMouseLocation = MouseInfo.getPointerInfo().location
        if (latestMouseLocation != newMouseLocation) {
            latestMouseLocation = newMouseLocation
            database.logMinute()
            printStats(System.out, true)
            PrintStream(statsFile).use {
                printStats(it, false)
            }
        }
    }

    private fun printStats(out: PrintStream, ansiSupported: Boolean) {
        if (ansiSupported) out.print(ANSI_CLEAR_SCREEN)

        // Today
        val now = Calendar.getInstance()
        var firstLog = database.firstLogOfDay(now)
        out.println(yellow("Today: ", ansiSupported) + formatDuration(database.minutesWorkedOnDay(now)) + arrivedAtLeftAt(firstLog, null, ansiSupported))

        // Yesterday
        val yesterday = workingDayAgo(1)
        firstLog = database.firstLogOfDay(yesterday)
        var lastLog = database.lastLogOfDay(yesterday)
        out.println(
            yellow("Yesterday: ", ansiSupported) + formatDuration(database.minutesWorkedOnDay(yesterday)) + arrivedAtLeftAt(
                firstLog,
                lastLog,
                ansiSupported
            )
        )

        // 2 days ago
        val dayAgo2 = workingDayAgo(2)
        firstLog = database.firstLogOfDay(dayAgo2)
        lastLog = database.lastLogOfDay(dayAgo2)
        out.println(
            yellow(dayAgo2.time.formatWeekDay() + ": ", ansiSupported) + formatDuration(database.minutesWorkedOnDay(dayAgo2)) + arrivedAtLeftAt(
                firstLog, lastLog,
                ansiSupported
            )
        )

        // 3 days ago
        val dayAgo3 = workingDayAgo(3)
        firstLog = database.firstLogOfDay(dayAgo3)
        lastLog = database.lastLogOfDay(dayAgo3)
        out.println(
            yellow(dayAgo3.time.formatWeekDay() + ": ", ansiSupported) + formatDuration(database.minutesWorkedOnDay(dayAgo3)) + arrivedAtLeftAt(
                firstLog, lastLog,
                ansiSupported
            )
        )

        // 4 days ago
        val dayAgo4 = workingDayAgo(4)
        firstLog = database.firstLogOfDay(dayAgo4)
        lastLog = database.lastLogOfDay(dayAgo4)
        out.println(
            yellow(dayAgo4.time.formatWeekDay() + ": ", ansiSupported) + formatDuration(database.minutesWorkedOnDay(dayAgo4)) + arrivedAtLeftAt(
                firstLog, lastLog,
                ansiSupported
            )
        )

        out.println()

        // This week
        out.println(
            yellow("This week: ", ansiSupported) + formatDuration(database.minutesWorkedOnWeek(now))
        )

        // Last week
        val lastWeek = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -1) }
        out.println(
            yellow("Last week: ", ansiSupported) + formatDuration(database.minutesWorkedOnWeek(lastWeek))
        )

        // 2 weeks ago
        val weekAgo2 = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -2) }
        out.println(
            yellow("2 weeks ago: ", ansiSupported) + formatDuration(database.minutesWorkedOnWeek(weekAgo2))
        )

        // 3 weeks ago
        val weekAgo3 = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -3) }
        out.println(
            yellow("3 weeks ago: ", ansiSupported) + formatDuration(database.minutesWorkedOnWeek(weekAgo3))
        )

        // 4 weeks ago
        val weekAgo4 = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -4) }
        out.println(
            yellow("4 weeks ago: ", ansiSupported) + formatDuration(database.minutesWorkedOnWeek(weekAgo4))
        )

        out.println()

        val averageMinutesPerDay = database.averageMinutesPerDay()
        out.println(
            yellow(underline(bold("Average", ansiSupported), ansiSupported) + ": ", ansiSupported) + bold(
                formatDuration(averageMinutesPerDay) + "/day",
                ansiSupported
            ) + " (${formatDuration(averageMinutesPerDay * 5)}/week) since ${database.firstLog().formatDay()}"
        )
    }

    companion object {
        private const val STATS_FILE_NAME = "workinghour.stats.txt"

        @Suppress("NOTHING_TO_INLINE")
        private inline fun arrivedAtLeftAt(firstLog: Date?, lastLog: Date?, ansiSupported: Boolean): String {
            return (
                    if (firstLog != null) " ${purple(
                        "↓" + firstLog.formatHourMinute(),
                        ansiSupported
                    )}" else ""
                    ) +
                    if (lastLog != null) " ${blue(
                        "↑" + lastLog.formatHourMinute(),
                        ansiSupported
                    )}" else ""
        }
    }
}

fun main(av: Array<String>) {
    Main().go(av)
}