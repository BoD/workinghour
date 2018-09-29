package org.jraf.workinghour.util

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DurationFormatUtils
import java.util.concurrent.TimeUnit

fun formatDuration(minutes: Number): String {
    var duration = DurationFormatUtils.formatDuration(TimeUnit.MINUTES.toMillis(minutes.toLong()), "H' hours 'm' minutes'")
    // this is a temporary marker on the front. Like ^ in regexp.
    duration = " $duration"

    // Suppress leading 0 elements
    var tmp = StringUtils.replaceOnce(duration, " 0 hours", StringUtils.EMPTY)
    if (tmp.length != duration.length) {
        duration = tmp
        tmp = StringUtils.replaceOnce(duration, " 0 minutes", StringUtils.EMPTY)
        duration = tmp
    }
    if (!duration.isEmpty()) {
        // strip the space off again
        duration = duration.substring(1)
    }

    // Suppress trailing 0 elements
    if (tmp.length != duration.length) {
        duration = tmp
        tmp = StringUtils.replaceOnce(duration, " 0 minutes", StringUtils.EMPTY)
        if (tmp.length != duration.length) {
            duration = tmp
            tmp = StringUtils.replaceOnce(duration, " 0 hours", StringUtils.EMPTY)
            duration = tmp
        }
    }
    // Handle plurals
    duration = " $duration"
    duration = StringUtils.replaceOnce(duration, " 1 minutes", " 1 minute")
    duration = StringUtils.replaceOnce(duration, " 1 hours", " 1 hour")
    return duration.trim { it <= ' ' }
}