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
import java.io.File
import java.lang.Thread.sleep
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

@Throws(Throwable::class)
fun main(av: Array<String>) {
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

    val database = Database(File("workinghour.db"))
//    database.logTestData()

    while (true) {
        database.logMinute()
        printStats(database)
        sleep(TimeUnit.MINUTES.toMillis(1))
    }
}

private fun printStats(database: Database) {
    print(ANSI_CLEAR_SCREEN)

    // Today
    val now = Calendar.getInstance()
    var firstLog = database.firstLogOfDay(now)
    println(yellow("Today: ") + formatDuration(database.minutesWorkedOnDay(now)) + if (firstLog != null) purple(" ${firstLog.formatHourMinute()}") else "")

    // Yesterday
    val yesterday = workingDayAgo(1)
    firstLog = database.firstLogOfDay(yesterday)
    var lastLog = database.lastLogOfDay(yesterday)
    println(yellow("Yesterday: ") + formatDuration(database.minutesWorkedOnDay(yesterday)) + arrivedAtLeftAt(firstLog, lastLog))

    // 2 days ago
    val dayAgo2 = workingDayAgo(2)
    firstLog = database.firstLogOfDay(dayAgo2)
    lastLog = database.lastLogOfDay(dayAgo2)
    println(
        yellow(dayAgo2.time.formatWeekDay() + ": ") + formatDuration(database.minutesWorkedOnDay(dayAgo2)) + arrivedAtLeftAt(firstLog, lastLog)
    )

    // 3 days ago
    val dayAgo3 = workingDayAgo(3)
    firstLog = database.firstLogOfDay(dayAgo3)
    lastLog = database.lastLogOfDay(dayAgo3)
    println(
        yellow(dayAgo3.time.formatWeekDay() + ": ") + formatDuration(database.minutesWorkedOnDay(dayAgo3)) + arrivedAtLeftAt(firstLog, lastLog)
    )

    // 4 days ago
    val dayAgo4 = workingDayAgo(4)
    firstLog = database.firstLogOfDay(dayAgo4)
    lastLog = database.lastLogOfDay(dayAgo4)
    println(
        yellow(dayAgo4.time.formatWeekDay() + ": ") + formatDuration(database.minutesWorkedOnDay(dayAgo4)) + arrivedAtLeftAt(firstLog, lastLog)
    )

    println()

    // This week
    println(
        yellow("This week: ") + formatDuration(database.minutesWorkedOnWeek(now))
    )

    // Last week
    val lastWeek = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -1) }
    println(
        yellow("Last week: ") + formatDuration(database.minutesWorkedOnWeek(lastWeek))
    )

    // 2 weeks ago
    val weekAgo2 = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -2) }
    println(
        yellow("2 weeks ago: ") + formatDuration(database.minutesWorkedOnWeek(weekAgo2))
    )

    // 3 weeks ago
    val weekAgo3 = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -3) }
    println(
        yellow("3 weeks ago: ") + formatDuration(database.minutesWorkedOnWeek(weekAgo3))
    )

    // 4 weeks ago
    val weekAgo4 = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -4) }
    println(
        yellow("4 weeks ago: ") + formatDuration(database.minutesWorkedOnWeek(weekAgo4))
    )

    println()

    val averageMinutesPerDay = database.averageMinutesPerDay()
    println(yellow(underline(bold("Average")) + ": ") + bold(formatDuration(averageMinutesPerDay) + " per day") + " (${formatDuration(averageMinutesPerDay * 5)} per week) since ${database.firstLog().formatDay()}")
}

@Suppress("NOTHING_TO_INLINE")
private inline fun arrivedAtLeftAt(firstLog: Date?, lastLog: Date?) =
    if (firstLog != null && lastLog != null) " ${purple(firstLog.formatHourMinute())} ${blue(lastLog.formatHourMinute())}" else ""

