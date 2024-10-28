package compose.project.demo

import compose.project.demo.data.protobuf.CyclingData
import compose.project.demo.data.protobuf.Race
import compose.project.demo.data.protobuf.Rider
import compose.project.demo.data.protobuf.Team
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfigException
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class FirebaseDataRepository(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val firebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig,
) :
    DataRepository {

    init {
        CoroutineScope(defaultDispatcher).launch {
            firebaseRemoteConfig.settings {
                minimumFetchInterval = 1.hours
            }
            try {
                firebaseRemoteConfig.fetchAndActivate()
                emitData(
                    firebaseRemoteConfig.getValue(FIREBASE_REMOTE_CONFIG_CYCLING_DATA_KEY)
                        .asString()
                )
            } catch (e: FirebaseRemoteConfigException) {
                return@launch
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class, ExperimentalSerializationApi::class)
    private suspend fun emitData(serializedContent: String) {
        val unzipped = unGZip(Base64.decode(serializedContent))
        val cyclingData = ProtoBuf.decodeFromByteArray<CyclingData>(unzipped)
        _teams.emit(cyclingData.teams)
        _riders.emit(cyclingData.riders)
        _races.emit(cyclingData.races)
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
                emitData(
                    firebaseRemoteConfig.getValue(FIREBASE_REMOTE_CONFIG_CYCLING_DATA_KEY)
                        .asString()
                )
            }
            true
        } catch (_: FirebaseRemoteConfigException) {
            false
        }
    }

    companion object {
        private const val FIREBASE_REMOTE_CONFIG_CYCLING_DATA_KEY = "cycling_data"
    }
}