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
import org.apache.commons.lang3.StringUtils
import org.jraf.workinghour.util.ANSI_CLEAR_SCREEN
import org.jraf.workinghour.util.blue
import org.jraf.workinghour.util.bold
import org.jraf.workinghour.util.darkGrey
import org.jraf.workinghour.util.formatDay
import org.jraf.workinghour.util.formatDuration
import org.jraf.workinghour.util.formatHourMinute
import org.jraf.workinghour.util.formatWeekDay
import org.jraf.workinghour.util.minus
import org.jraf.workinghour.util.purple
import org.jraf.workinghour.util.underline
import org.jraf.workinghour.util.workingDayAgo
import org.jraf.workinghour.util.workingDaysAgo
import org.jraf.workinghour.util.yellow
import java.awt.MouseInfo
import java.awt.Point
import java.io.File
import java.io.PrintStream
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
            .programName(PROGRAM_NAME)
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

        for (i in 0..7) {
            val dayAgo = workingDayAgo(i)
            val firstLogOfMorning = database.firstLogOfMorning(dayAgo, MORNING_MIN_HOUR, MORNING_MIN_MINUTE)
            val lastLogOfMorning = database.lastLogOfMorning(dayAgo, MORNING_MAX_HOUR, MORNING_MAX_MINUTE)
            val firstLogOfEvening = database.firstLogOfEvening(dayAgo, EVENING_MIN_HOUR, EVENING_MIN_MINUTE)
            val lastLogOfEvening = database.lastLogOfEvening(dayAgo, EVENING_MAX_HOUR, EVENING_MAX_MINUTE)
            val minutes = (lastLogOfMorning - firstLogOfMorning) + (lastLogOfEvening - firstLogOfEvening)
            out.println(
                yellow(StringUtils.rightPad(dayAgo.time.formatWeekDay() + ": ", 11), ansiSupported) +
                        formatDuration(minutes) +
                        arrivedAtLeftAt(
                            firstLogOfMorning,
                            lastLogOfMorning,
                            firstLogOfEvening,
                            lastLogOfEvening,
                            ansiSupported
                        )
            )
        }
        out.println()

        for (i in 0..4) {
            val daysAgo = workingDaysAgo(i)
            var totalMinutes = 0L
            for (dayAgo in daysAgo) {
                val firstLogOfMorning = database.firstLogOfMorning(dayAgo, MORNING_MIN_HOUR, MORNING_MIN_MINUTE)
                val lastLogOfMorning = database.lastLogOfMorning(dayAgo, MORNING_MAX_HOUR, MORNING_MAX_MINUTE)
                val firstLogOfEvening = database.firstLogOfEvening(dayAgo, EVENING_MIN_HOUR, EVENING_MIN_MINUTE)
                val lastLogOfEvening = database.lastLogOfEvening(dayAgo, EVENING_MAX_HOUR, EVENING_MAX_MINUTE)
                val minutes = (lastLogOfMorning - firstLogOfMorning) + (lastLogOfEvening - firstLogOfEvening)
                totalMinutes += minutes
            }
            out.println(
                yellow(
                    StringUtils.rightPad(
                        when (i) {
                            0 -> "This week: "
                            1 -> "Last week: "
                            else -> "$i weeks ago: "
                        }, 13
                    ), ansiSupported
                ) + formatDuration(totalMinutes)
            )
        }

        out.println()

        var i = 0
        var nbDays = 0
        var totalMinutes = 0L
        val firstLog = database.firstLog()
        while (true) {
            val dayAgo = workingDayAgo(i)
            if (dayAgo.time.before(firstLog)) break
            val firstLogOfMorning = database.firstLogOfMorning(dayAgo, MORNING_MIN_HOUR, MORNING_MIN_MINUTE)
            val lastLogOfMorning = database.lastLogOfMorning(dayAgo, MORNING_MAX_HOUR, MORNING_MAX_MINUTE)
            val firstLogOfEvening = database.firstLogOfEvening(dayAgo, EVENING_MIN_HOUR, EVENING_MIN_MINUTE)
            val lastLogOfEvening = database.lastLogOfEvening(dayAgo, EVENING_MAX_HOUR, EVENING_MAX_MINUTE)
            val minutes = (lastLogOfMorning - firstLogOfMorning) + (lastLogOfEvening - firstLogOfEvening)
            // Discard anomalous days, and days off
            if (minutes >= TimeUnit.HOURS.toMinutes(7)) {
                totalMinutes += minutes
                nbDays++
            }
            i++
        }

        val averageMinutesPerDay = totalMinutes.toFloat() / nbDays
        out.println(
            yellow(underline(bold("Average", ansiSupported), ansiSupported) + ": ", ansiSupported) + bold(
                formatDuration(averageMinutesPerDay) + "/day",
                ansiSupported
            ) + " (${formatDuration(averageMinutesPerDay * 5)}/week) since ${firstLog.formatDay()}"
        )
    }

    companion object {
        private const val PROGRAM_NAME = "workinghour"
        private const val STATS_FILE_NAME = "workinghour.stats.txt"

        private const val MORNING_MIN_HOUR = 8
        private const val MORNING_MIN_MINUTE = 45

        private const val MORNING_MAX_HOUR = 13
        private const val MORNING_MAX_MINUTE = 0

        private const val EVENING_MIN_HOUR = 13
        private const val EVENING_MIN_MINUTE = 0

        private const val EVENING_MAX_HOUR = 21
        private const val EVENING_MAX_MINUTE = 0

        @Suppress("NOTHING_TO_INLINE")
        private inline fun arrivedAtLeftAt(
            firstLogOfMorning: Date?,
            lastLogOfMorning: Date?,
            firstLogOfEvening: Date?,
            lastLogOfEvening: Date?,
            ansiSupported: Boolean
        ): String {
            return (
                    if (firstLogOfMorning != null) "  ${purple(
                        firstLogOfMorning.formatHourMinute() + "  ",
                        ansiSupported
                    )}" else ""
                    ) +
                    (
                            if (lastLogOfMorning != null) darkGrey(
                                lastLogOfMorning.formatHourMinute(),
                                ansiSupported
                            ) else ""
                            ) +
                    (
                            if (firstLogOfEvening != null) "  ${darkGrey(
                                firstLogOfEvening.formatHourMinute() + "  ",
                                ansiSupported
                            )}" else ""
                            ) +
                    (
                            if (lastLogOfEvening != null) blue(
                                lastLogOfEvening.formatHourMinute(),
                                ansiSupported
                            ) else ""
                            )
        }
    }
}

fun main(av: Array<String>) {
    Main().go(av)
}