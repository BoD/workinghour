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

package org.jraf.workinghour.activitydetection

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jraf.workinghour.util.mouse.MouseLocation
import org.jraf.workinghour.util.mouse.getMouseLocation
import kotlin.time.Duration

class ActivityDetector(
    private val monitoringPeriod: Duration
) {
    var isActive = true
        private set

    var started: Boolean = false
        private set

    private var mouseMonitoringJob: Job? = null
    private var latestMouseLocation: MouseLocation? = null

    fun start(): Boolean {
        if (started) return false
        started = true
        startMonitoringMouse()
        return true
    }

    fun stop(): Boolean {
        if (!started) return false
        started = false
        stopMonitoringMouse()
        return true
    }

    private fun startMonitoringMouse() {
        mouseMonitoringJob = GlobalScope.launch {
            while (true) {
                val newMouseLocation = getMouseLocation()
                isActive = newMouseLocation != latestMouseLocation
                latestMouseLocation = newMouseLocation

                delay(monitoringPeriod)
            }
        }
    }

    private fun stopMonitoringMouse() {
        mouseMonitoringJob?.cancel()
    }
}
