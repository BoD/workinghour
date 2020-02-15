/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2020-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.workinghour.daemon

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jraf.workinghour.activitydetection.ActivityDetector
import org.jraf.workinghour.datetime.CalendarDate
import org.jraf.workinghour.db.Database
import java.io.File
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

@ExperimentalTime
class Daemon(
    databaseFile: File,
    private val activityMonitoringPeriod: Duration = DEFAULT_ACTIVITY_MONITORING_PERIOD
) {
    var started: Boolean = false
        private set

    private val activityDetector = ActivityDetector(activityMonitoringPeriod)
    private val database = Database(databaseFile)

    private var activityLoggingJob: Job? = null
    private var latestActive = true

    fun start(): Boolean {
        if (started) return false
        started = true
        startLoggingActivity()
        return true
    }

    fun stop(): Boolean {
        if (!started) return false
        started = false
        stopLoggingActivity()
        return true
    }

    private fun startLoggingActivity() {
        activityDetector.start()
        activityLoggingJob = GlobalScope.launch {
            while (true) {
                val newActive = activityDetector.isActive
                if (newActive != latestActive) {
                    if (newActive) {
                        database.logActive()
                    } else {
                        database.logInactive()
                    }
                }
                latestActive = newActive

                val today = CalendarDate.now()
                println("🛬 ${database.startOfWorkDay(today)}  🛫 ${database.endOfWorkDay(today)}")

                delay(activityMonitoringPeriod.toLongMilliseconds())
            }
        }
    }

    private fun stopLoggingActivity() {
        activityLoggingJob?.cancel()
        activityDetector.stop()
    }

    companion object {
        private val DEFAULT_ACTIVITY_MONITORING_PERIOD = 1.minutes
    }
}