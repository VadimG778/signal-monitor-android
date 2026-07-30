package com.vadimg778.signalmonitor.feature.monitor.data.source

import com.vadimg778.signalmonitor.feature.monitor.data.time.TimeProvider
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.source.SignalPointSource
import com.vadimg778.signalmonitor.feature.monitor.domain.source.SignalPointSourceFactory
import kotlin.random.Random

class RandomWalkPointSourceFactoryImpl(private val timeProvider: TimeProvider) :
    SignalPointSourceFactory {

    override fun create(generatorId: GeneratorId): SignalPointSource = RandomWalkPointSourceImpl(
        timeProvider = timeProvider,
        random = Random(Random.nextInt() xor generatorId.value),
    )
}
