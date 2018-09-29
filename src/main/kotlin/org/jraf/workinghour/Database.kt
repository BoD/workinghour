package org.jraf.workinghour

import java.io.File
import java.sql.DriverManager
import java.util.Calendar

class Database(private val path: File) {
    private val connection by lazy {
        val connection = DriverManager.getConnection("jdbc:sqlite:${path.canonicalPath}")
        connection.createStatement()
            .executeUpdate(
                """
                    CREATE TABLE IF NOT EXISTS minute (
                        id INTEGER PRIMARY KEY ON CONFLICT IGNORE NOT NULL
                    )
                    """.trimIndent()
            )
        connection
    }

    private val insertMinutePreparedStatement by lazy {
        connection.prepareStatement("INSERT INTO minute (id) VALUES (?)")
    }

    fun logMinute() {
        val now = Calendar.getInstance()
        with(insertMinutePreparedStatement) {
            setLong(
                1,
                String.format(
                    "%d%02d%d%d%d",
                    now[Calendar.YEAR],
                    now[Calendar.MONTH] + 1,
                    now[Calendar.DAY_OF_MONTH],
                    now[Calendar.HOUR_OF_DAY],
                    now[Calendar.MINUTE]
                ).toLong()
            )
            execute()
        }
    }

}