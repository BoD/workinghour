/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2018 Benoit 'BoD' Lubek (BoD@JRAF.org)
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

@file:Suppress("NOTHING_TO_INLINE")

package org.jraf.workinghour.util

const val ANSI_ESC = "\u001B["

const val ANSI_CLEAR_SCREEN = "${ANSI_ESC}2J${ANSI_ESC}H"

const val ANSI_RESET_BACKGROUND_COLOR = "${ANSI_ESC}49m"
const val ANSI_RESET_FOREGROUND_COLOR = "${ANSI_ESC}39m"
const val ANSI_RESET_COLORS = "${ANSI_ESC}0m"

const val ANSI_BOLD_ON = "${ANSI_ESC}1m"
const val ANSI_BOLD_OFF = "${ANSI_ESC}22m"

const val ANSI_UNDERLINE_ON = "${ANSI_ESC}4m"
const val ANSI_UNDERLINE_OFF = "${ANSI_ESC}24m"

const val ANSI_BLACK = "${ANSI_ESC}30m"
const val ANSI_RED = "${ANSI_ESC}31m"
const val ANSI_GREEN = "${ANSI_ESC}32m"
const val ANSI_YELLOW = "${ANSI_ESC}33m"
const val ANSI_BLUE = "${ANSI_ESC}34m"
const val ANSI_PURPLE = "${ANSI_ESC}35m"
const val ANSI_CYAN = "${ANSI_ESC}36m"
const val ANSI_WHITE = "${ANSI_ESC}37m"

const val ANSI_BLACK_BOLD = "${ANSI_ESC}1;30m"
const val ANSI_RED_BOLD = "${ANSI_ESC}1;31m"
const val ANSI_GREEN_BOLD = "${ANSI_ESC}1;32m"
const val ANSI_YELLOW_BOLD = "${ANSI_ESC}1;33m"
const val ANSI_BLUE_BOLD = "${ANSI_ESC}1;34m"
const val ANSI_PURPLE_BOLD = "${ANSI_ESC}1;35m"
const val ANSI_CYAN_BOLD = "${ANSI_ESC}1;36m"
const val ANSI_WHITE_BOLD = "${ANSI_ESC}1;37m"

// Calculate the nearest 0-based color index at 16 .. 231
private fun v2ci(v: Int) = if (v < 48) 0 else if (v < 115) 1 else (v - 35) / 40

// 0..215, lazy evaluation
private fun colorIndex(ir: Int, ig: Int, ib: Int) = (36 * ir + 6 * ig + ib)

private fun distSquare(A: Int, B: Int, C: Int, a: Int, b: Int, c: Int) = (A - a) * (A - a) + (B - b) * (B - b) + (C - c) * (C - c)

fun rgbToAnsi256(r: Int, g: Int, b: Int): Int {
    // 0..5 each
    val ir = v2ci(r)
    val ig = v2ci(g)
    val ib = v2ci(b)

    // Calculate the nearest 0-based gray index at 232 .. 255
    val average = (r + g + b) / 3
    val grayIndex = if (average > 238) 23 else (average - 3) / 10  // 0..23

    val i2cv = intArrayOf(0, 0x5f, 0x87, 0xaf, 0xd7, 0xff)
    // r/g/b, 0..255 each
    val cr = i2cv[ir]
    val cg = i2cv[ig]
    val cb = i2cv[ib]
    val gv = 8 + 10 * grayIndex  // same value for r/g/b, 0..255

    val colorErr = distSquare(cr, cg, cb, r, g, b)
    val grayErr = distSquare(gv, gv, gv, r, g, b)

    return if (colorErr <= grayErr) 16 + colorIndex(ir, ig, ib) else 232 + grayIndex
}

fun Color.toAnsi256Foreground() = "${ANSI_ESC}38;5;${rgbToAnsi256(red, green, blue)}m"

fun Color.toAnsi256Background() = "${ANSI_ESC}48;5;${rgbToAnsi256(red, green, blue)}m"

inline fun bold(s: String, ansiSupported: Boolean) = if (ansiSupported) "$ANSI_BOLD_ON$s$ANSI_BOLD_OFF" else s
inline fun underline(s: String, ansiSupported: Boolean) = if (ansiSupported) "$ANSI_UNDERLINE_ON$s$ANSI_UNDERLINE_OFF" else s

inline fun yellow(s: String, ansiSupported: Boolean) = if (ansiSupported) "$ANSI_YELLOW$s$ANSI_RESET_COLORS" else s
inline fun purple(s: String, ansiSupported: Boolean) = if (ansiSupported) "$ANSI_PURPLE$s$ANSI_RESET_COLORS" else s
inline fun blue(s: String, ansiSupported: Boolean) = if (ansiSupported) "$ANSI_BLUE$s$ANSI_RESET_COLORS" else s
