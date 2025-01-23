package io.github.patxibocos.mycyclist.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfigException
import dev.gitlive.firebase.remoteconfig.remoteConfig
import io.github.patxibocos.mycyclist.data.mapper.RaceMapper.toRaces
import io.github.patxibocos.mycyclist.data.mapper.RiderMapper.toRiders
import io.github.patxibocos.mycyclist.data.mapper.TeamMapper.toTeams
import io.github.patxibocos.mycyclist.data.protobuf.CyclingDataDto
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.domain.Rider
import io.github.patxibocos.mycyclist.domain.Team
import io.github.patxibocos.mycyclist.expect.unGZip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.coroutines.CoroutineContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal class FirebaseDataRepository(
    private val firebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
    refreshInterval: Duration = 1.hours,
) :
    DataRepository {

    init {
        MainScope().launch {
            // Emit the cached value if available
            emitRemoteConfigValue()
            firebaseRemoteConfig.settings {
                minimumFetchInterval = refreshInterval
            }
            while (isActive) {
                refresh()
                delay(refreshInterval)
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class, ExperimentalSerializationApi::class)
    private suspend fun emitRemoteConfigValue() = withContext(defaultDispatcher) {
        val remoteConfigValue = firebaseRemoteConfig.getValue(REMOTE_CONFIG_KEY).asString()
        if (remoteConfigValue.isEmpty()) {
            return@withContext
        }
        val unzipped = unGZip(Base64.decode(remoteConfigValue))
        val cyclingData = ProtoBuf.decodeFromByteArray<CyclingDataDto>(unzipped)
        _teams.emit(cyclingData.teams.toTeams())
        _riders.emit(cyclingData.riders.toRiders())
        _races.emit(cyclingData.races.toRaces())
    }

    private val _teams = MutableSharedFlow<List<Team>>(replay = 1)
    private val _riders = MutableSharedFlow<List<Rider>>(replay = 1)
    private val _races = MutableSharedFlow<List<Race>>(replay = 1)

    override val teams = _teams
    override val riders = _riders
    override val races = _races

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
