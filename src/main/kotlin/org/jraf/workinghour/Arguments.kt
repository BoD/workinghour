package org.jraf.workinghour

import com.beust.jcommander.Parameter
import java.io.File

class Arguments {
    @Parameter(
        names = ["-h", "--help"],
        description = "Show this help",
        help = true
    )
    var help: Boolean = false

    @Parameter(
        names = ["-p", "--path"],
        description = "The path to the directory where the db and stats files will be stored.  This directory must exist.  Default value: current directory"
    )
    var path: File = File(".")
}
