package org.jraf.workinghour

import com.beust.jcommander.JCommander
import java.io.File

@Throws(Throwable::class)
fun main(av: Array<String>) {
    val arguments = Arguments()
    val jCommander = JCommander.newBuilder()
        .addObject(arguments)
        .build()
    jCommander.parse(*av)

    if (arguments.help) {
        jCommander.usage()
        return
    }

    println("Hello, World!")

    val database = Database(File("workinghour.db"))
    println(database.logMinute())
    println(database.logMinute())
}
