package com.vadimg778.signalmonitor.feature.monitor.data.time

import android.os.SystemClock

object SystemTimeProviderImpl : TimeProvider {

    override fun currentTimeMillis(): Long = System.currentTimeMillis()

    override fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
}
