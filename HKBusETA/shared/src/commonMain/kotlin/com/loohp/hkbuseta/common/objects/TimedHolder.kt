package com.loohp.hkbuseta.common.objects

import kotlinx.datetime.LocalDateTime

data class TimedHolder<T>(val time: LocalDateTime, val value: T)

fun <T> T.atTime(time: LocalDateTime): TimedHolder<T> {
    return TimedHolder(time, this)
}