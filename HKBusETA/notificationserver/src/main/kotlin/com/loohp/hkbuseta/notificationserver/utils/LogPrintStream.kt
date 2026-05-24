/*
 * This file is part of HKBusETA.
 *
 * Copyright (C) 2026. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2026. Contributors
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.hkbuseta.notificationserver.utils

import java.io.OutputStream
import java.io.PrintStream
import java.util.Locale


fun PrintStream.asLogging(logs: PrintStream): PrintStream = LogPrintStream(this, logs)

class LogPrintStream(
    output: OutputStream,
    val logs: PrintStream
): PrintStream(output) {

    override fun flush() {
        super.flush()
        logs.flush()
    }

    override fun close() {
        super.close()
        logs.close()
    }

    override fun write(b: Int) {
        super.write(b)
        logs.write(b)
    }

    override fun write(buf: ByteArray?, off: Int, len: Int) {
        super.write(buf, off, len)
        logs.write(buf, off, len)
    }

    override fun write(buf: ByteArray?) {
        super.write(buf)
        logs.write(buf)
    }

    override fun writeBytes(buf: ByteArray?) {
        super.writeBytes(buf)
        logs.writeBytes(buf)
    }

    override fun print(b: Boolean) {
        super.print(b)
        logs.print(b)
    }

    override fun print(c: Char) {
        super.print(c)
        logs.print(c)
    }

    override fun print(i: Int) {
        super.print(i)
        logs.print(i)
    }

    override fun print(l: Long) {
        super.print(l)
        logs.print(l)
    }

    override fun print(f: Float) {
        super.print(f)
        logs.print(f)
    }

    override fun print(d: Double) {
        super.print(d)
        logs.print(d)
    }

    override fun print(s: CharArray?) {
        super.print(s)
        logs.print(s)
    }

    override fun print(s: String?) {
        super.print(s)
        logs.print(s)
    }

    override fun print(obj: Any?) {
        super.print(obj)
        logs.print(obj)
    }

    override fun println() {
        super.println()
        logs.println()
    }

    override fun println(x: Boolean) {
        super.println(x)
        logs.println(x)
    }

    override fun println(x: Char) {
        super.println(x)
        logs.println(x)
    }

    override fun println(x: Int) {
        super.println(x)
        logs.println(x)
    }

    override fun println(x: Long) {
        super.println(x)
        logs.println(x)
    }

    override fun println(x: Float) {
        super.println(x)
        logs.println(x)
    }

    override fun println(x: Double) {
        super.println(x)
        logs.println(x)
    }

    override fun println(x: CharArray?) {
        super.println(x)
        logs.println(x)
    }

    override fun println(x: String?) {
        super.println(x)
        logs.println(x)
    }

    override fun println(x: Any?) {
        super.println(x)
        logs.println(x)
    }

    override fun printf(format: String, vararg args: Any?): PrintStream? {
        return super.printf(format, *args).apply {
            logs.printf(format, *args)
        }
    }

    override fun printf(l: Locale?, format: String, vararg args: Any?): PrintStream? {
        return super.printf(l, format, *args).apply {
            logs.printf(l, format, *args)
        }
    }

    override fun format(format: String, vararg args: Any?): PrintStream? {
        return super.format(format, *args).apply {
            logs.format(format, *args)
        }
    }

    override fun format(l: Locale?, format: String, vararg args: Any?): PrintStream? {
        return super.format(l, format, *args).apply {
            logs.format(l, format, *args)
        }
    }

    override fun append(csq: CharSequence?): PrintStream? {
        return super.append(csq).apply {
            logs.append(csq)
        }
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): PrintStream? {
        return super.append(csq, start, end).apply {
            logs.append(csq, start, end)
        }
    }

    override fun append(c: Char): PrintStream? {
        return super.append(c).apply {
            logs.append(c)
        }
    }
}