package compose.project.demo.data.firebase

import compose.project.demo.data.mapper.RaceMapper.toRaces
import compose.project.demo.data.mapper.RiderMapper.toRiders
import compose.project.demo.data.mapper.TeamMapper.toTeams
import compose.project.demo.data.protobuf.CyclingDataDto
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.Race
import compose.project.demo.domain.Rider
import compose.project.demo.domain.Team
import compose.project.demo.expect.unGZip
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfigException
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal class FirebaseDataRepository(
    private val firebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig,
) :
    DataRepository {

    init {
        CoroutineScope(Dispatchers.Default).launch {
            emitData(firebaseRemoteConfig.getValue(REMOTE_CONFIG_KEY).asString())
            firebaseRemoteConfig.settings {
                minimumFetchInterval = 1.hours
            }
            try {
                withContext(Dispatchers.IO) {
                    firebaseRemoteConfig.fetchAndActivate()
                }
                emitData(firebaseRemoteConfig.getValue(REMOTE_CONFIG_KEY).asString())
            } catch (_: FirebaseRemoteConfigException) {
                return@launch
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class, ExperimentalSerializationApi::class)
    private suspend fun emitData(serializedContent: String) = withContext(Dispatchers.Default) {
        if (serializedContent.isEmpty()) {
            return@withContext
        }
        val unzipped = unGZip(Base64.decode(serializedContent))
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

    override suspend fun refresh(): Boolean {
        return try {
            firebaseRemoteConfig.fetch(Duration.ZERO)
            if (firebaseRemoteConfig.activate()) {
                emitData(firebaseRemoteConfig.getValue(REMOTE_CONFIG_KEY).asString())
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
