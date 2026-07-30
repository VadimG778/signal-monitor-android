package com.vadimg778.signalmonitor.app.startup

import android.content.Context
import androidx.startup.Initializer
import com.scichart.charting.visuals.SciChartSurface
import com.vadimg778.signalmonitor.R

class SciChartInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val licenseKey = context.getString(R.string.scichart_license_key)
        if (licenseKey.isNotBlank()) {
            SciChartSurface.setRuntimeLicenseKey(licenseKey)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
