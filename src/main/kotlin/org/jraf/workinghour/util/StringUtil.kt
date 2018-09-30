package org.jraf.workinghour.util

import org.apache.commons.lang3.time.DurationFormatUtils
import java.util.concurrent.TimeUnit

fun formatDuration(minutes: Number): String {
    return DurationFormatUtils.formatDuration(TimeUnit.MINUTES.toMillis(minutes.toLong()), "H'h'm'm'")
        .replace("h0m", "h")
        .replace(Regex("^0h(.+)"), "$1")

}