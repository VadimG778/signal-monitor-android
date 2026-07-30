package com.vadimg778.signalmonitor.feature.monitor.data.time

interface TimeProvider {

    fun currentTimeMillis(): Long

    fun elapsedRealtimeMillis(): Long
}
