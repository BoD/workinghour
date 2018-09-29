package org.jraf.workinghour

import com.beust.jcommander.JCommander
import org.apache.commons.lang3.time.DurationFormatUtils
import java.io.File
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
    database.logMinute()
    println("Today: ${formatDuration(database.minutesWorkedToday())}")
    println("This week: ${formatDuration(database.minutesWorkedThisWeek())}")
    println("This month: ${formatDuration(database.minutesWorkedThisMonth())}")
    println("Last 30 days: ${formatDuration(database.minutesWorkedLast30Days())}")
    println()
    println("Total: ${formatDuration(database.minutesWorkedTotal())} (since ${database.firstLog()})")

}

private fun formatDuration(minutes: Number): String {
    return DurationFormatUtils.formatDurationWords(TimeUnit.MINUTES.toMillis(minutes.toLong()), true, true)
}
