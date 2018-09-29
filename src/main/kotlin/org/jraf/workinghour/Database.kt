package org.jraf.workinghour

import java.io.File
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Database(private val path: File) {
    companion object {
        private val ID_DATE_FORMAT = SimpleDateFormat("yyyyMMddHHmm")
    }

    private val connection by lazy {
        Class.forName("net.sf.log4jdbc.DriverSpy")
        val connection = DriverManager.getConnection("jdbc:log4jdbc:sqlite:${path.canonicalPath}")
        connection.createStatement()
            .executeUpdate(
                """
                    CREATE TABLE IF NOT EXISTS minute (
                        id INTEGER PRIMARY KEY ON CONFLICT IGNORE NOT NULL,
                        year INTEGER NOT NULL,
                        month INTEGER NOT NULL,
                        day INTEGER NOT NULL,
                        hour INTEGER NOT NULL,
                        minute INTEGER NOT NULL
                    )
                    """.trimIndent()
            )
        connection
    }

    private val insertMinute by lazy {
        connection.prepareStatement("INSERT INTO minute (id, year, month, day, hour, minute) VALUES (?, ?, ?, ?, ?, ?)")
    }

    private val selectMinutesWorkedToday by lazy {
        connection.prepareStatement(
            """
            SELECT count(*) FROM minute WHERE
                year=?
                AND month=?
                AND day=?
             """.trimIndent()
        )
    }

    private val selectMinutesWorkedThisMonth by lazy {
        connection.prepareStatement(
            """
            SELECT count(*) FROM minute WHERE
                year=?
                AND month=?
             """.trimIndent()
        )
    }

    private val selectMinutesWorkedSinceDate by lazy {
        connection.prepareStatement("SELECT count(*) FROM minute WHERE id>=?")
    }

    private val selectFirstLog by lazy {
        connection.prepareStatement("SELECT year, month, day, hour, minute FROM minute ORDER BY id")
    }


    @Suppress("NOTHING_TO_INLINE")
    private inline fun getIdForCalendar(cal: Calendar) = ID_DATE_FORMAT.format(cal.time).toLong()

    fun logMinute() {
        val now = Calendar.getInstance()
        insertMinute.apply {
            setLong(1, getIdForCalendar(now))
            setInt(2, now[Calendar.YEAR])
            setInt(3, now[Calendar.MONTH] + 1)
            setInt(4, now[Calendar.DAY_OF_MONTH])
            setInt(5, now[Calendar.HOUR_OF_DAY])
            setInt(6, now[Calendar.MINUTE])
        }.execute()
    }

    fun minutesWorkedToday(): Int {
        val now = Calendar.getInstance()
        return selectMinutesWorkedToday.apply {
            setInt(1, now[Calendar.YEAR])
            setInt(2, now[Calendar.MONTH] + 1)
            setInt(3, now[Calendar.DAY_OF_MONTH])
        }.executeQuery().getInt(1)
    }

    fun minutesWorkedThisMonth(): Int {
        val now = Calendar.getInstance()
        return selectMinutesWorkedThisMonth.apply {
            setInt(1, now[Calendar.YEAR])
            setInt(2, now[Calendar.MONTH] + 1)
        }.executeQuery().getInt(1)
    }

    fun minutesWorkedLast30Days(): Int {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -30)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        return selectMinutesWorkedSinceDate.apply {
            setLong(1, getIdForCalendar(cal))
        }.executeQuery().getInt(1)
    }

    fun minutesWorkedThisWeek(): Int {
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        return selectMinutesWorkedSinceDate.apply {
            setLong(1, getIdForCalendar(cal))
        }.executeQuery().getInt(1)
    }

    fun minutesWorkedTotal(): Int {
        return selectMinutesWorkedSinceDate.apply {
            setInt(1, 0)
        }.executeQuery().getInt(1)
    }

    fun firstLog(): Date {
        val cal = Calendar.getInstance()
        val resultSet = selectFirstLog.executeQuery()
        cal[Calendar.YEAR] = resultSet.getInt(1)
        cal[Calendar.MONTH] = resultSet.getInt(2) - 1
        cal[Calendar.DAY_OF_WEEK] = resultSet.getInt(3)
        cal[Calendar.HOUR_OF_DAY] = resultSet.getInt(4)
        cal[Calendar.MINUTE] = resultSet.getInt(5)
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.time
    }

}