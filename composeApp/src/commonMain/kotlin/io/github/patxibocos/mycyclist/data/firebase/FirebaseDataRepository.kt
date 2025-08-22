package io.github.patxibocos.mycyclist.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfigException
import dev.gitlive.firebase.remoteconfig.remoteConfig
import io.github.patxibocos.mycyclist.data.gzip.unGZip
import io.github.patxibocos.mycyclist.data.mapper.RaceMapper.toRaces
import io.github.patxibocos.mycyclist.data.mapper.RiderMapper.toRiders
import io.github.patxibocos.mycyclist.data.mapper.TeamMapper.toTeams
import io.github.patxibocos.mycyclist.data.protobuf.CyclingDataDto
import io.github.patxibocos.mycyclist.domain.entity.CyclingData
import io.github.patxibocos.mycyclist.domain.repository.CyclingDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.coroutines.CoroutineContext
import kotlin.io.encoding.Base64
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal class FirebaseDataRepository(
    private val firebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
    private val refreshInterval: Duration = 1.hours,
    private val mainScope: CoroutineScope = MainScope(),
) :
    CyclingDataRepository {

    @Suppress("TooGenericExceptionCaught")
    override fun initialize() {
        mainScope.launch {
            try {
                // Emit the cached value if available
                emitRemoteConfigValue()
            } catch (e: Throwable) {
                println("Skipping cached configuration value because of: ${e.message}")
            }
            firebaseRemoteConfig.settings {
                minimumFetchInterval = refreshInterval
            }
            while (isActive) {
                refresh()
                delay(refreshInterval)
            }
        }
    }

    private suspend fun emitRemoteConfigValue() = withContext(defaultDispatcher) {
        val remoteConfigValue = firebaseRemoteConfig.getValue(REMOTE_CONFIG_KEY).asString()
        if (remoteConfigValue.isEmpty()) {
            return@withContext
        }
        val unzipped = unGZip(Base64.decode(remoteConfigValue))
        val cyclingDataDto = ProtoBuf.decodeFromByteArray<CyclingDataDto>(unzipped)
        val teams = cyclingDataDto.teams.toTeams()
        val riders = cyclingDataDto.riders.toRiders()
        val races = cyclingDataDto.races.toRaces()
        val cyclingData = CyclingData(races, teams, riders)
        _cyclingData.emit(cyclingData)
    }

    private val _cyclingData = MutableSharedFlow<CyclingData>(replay = 1)

    override val cyclingData = _cyclingData

    override suspend fun refresh(): Boolean = withContext(ioDispatcher) {
        try {
            firebaseRemoteConfig.fetch(Duration.ZERO)
            if (firebaseRemoteConfig.activate()) {
                emitRemoteConfigValue()
            }
            true
        } catch (_: FirebaseRemoteConfigException) {
            false
        }
    }

    companion object {
        private const val REMOTE_CONFIG_KEY = "cycling_data"
    }
}
