package compose.project.demo.data.firebase

import compose.project.demo.data.protobuf.CyclingDataDto
import compose.project.demo.data.protobuf.RaceDto
import compose.project.demo.data.protobuf.RiderDto
import compose.project.demo.data.protobuf.StageDto
import compose.project.demo.data.protobuf.TeamDto
import compose.project.demo.domain.DataRepository
import compose.project.demo.domain.GeneralResults
import compose.project.demo.domain.ProfileType
import compose.project.demo.domain.Race
import compose.project.demo.domain.Rider
import compose.project.demo.domain.Stage
import compose.project.demo.domain.StageResults
import compose.project.demo.domain.StageType
import compose.project.demo.domain.Team
import compose.project.demo.domain.TeamStatus
import compose.project.demo.expect.unGZip
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfigException
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
        val cyclingData = ProtoBuf.decodeFromByteArray<CyclingDataDto>(unzipped)
        _teams.emit(cyclingData.teams.map(TeamDto::toDomain))
        _riders.emit(cyclingData.riders.map(RiderDto::toDomain))
        _races.emit(cyclingData.races.map(RaceDto::toDomain))
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

fun TeamDto.toDomain(): Team {
    return Team(
        id = this.id,
        name = this.name,
        status = when (this.status) {
            TeamDto.Status.WorldTeam -> TeamStatus.WORLD_TEAM
            TeamDto.Status.ProTeam -> TeamStatus.PRO_TEAM
            else -> error("Unexpected team status")
        },
        abbreviation = this.abbreviation,
        jersey = this.jersey,
        bike = this.bike,
        riderIds = this.riderIds,
        country = this.country,
        website = this.website
    )
}

fun RiderDto.toDomain(): Rider {
    return Rider(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        photo = this.photo,
        country = this.country,
        website = this.website.orEmpty(),
        birthDate = Instant.fromEpochSeconds(this.birthDate?.seconds ?: 0)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date,
        birthPlace = this.birthPlace.orEmpty(),
        weight = this.weight ?: 0,
        height = this.height ?: 0,
        uciRankingPosition = this.uciRankingPosition ?: 0,
    )
}

fun RaceDto.toDomain(): Race {
    return Race(
        id = this.id,
        name = this.name,
        country = this.country,
        stages = this.stages.map(StageDto::toDomain),
        website = this.website,
        teamParticipations = emptyList(),
    )
}

fun StageDto.toDomain(): Stage {
    return Stage(
        id = this.id,
        distance = this.distance,
        startDateTime = Instant.fromEpochSeconds(this.startDateTime?.seconds ?: 0),
        departure = this.departure,
        arrival = this.arrival,
        profileType = when (this.profileType) {
            StageDto.ProfileType.Unspecified -> null
            StageDto.ProfileType.Flat -> ProfileType.FLAT
            StageDto.ProfileType.HillsFlatFinish -> ProfileType.HILLS_FLAT_FINISH
            StageDto.ProfileType.HillsUphillFinish -> ProfileType.HILLS_UPHILL_FINISH
            StageDto.ProfileType.MountainsFlatFinish -> ProfileType.MOUNTAINS_FLAT_FINISH
            StageDto.ProfileType.MountainsUphillFinish -> ProfileType.MOUNTAINS_UPHILL_FINISH
        },
        stageType = when (this.stageType) {
            StageDto.StageType.Unspecified -> StageType.REGULAR
            StageDto.StageType.Regular -> StageType.REGULAR
            StageDto.StageType.IndividualTimeTrial -> StageType.INDIVIDUAL_TIME_TRIAL
            StageDto.StageType.TeamTimeTrial -> StageType.TEAM_TIME_TRIAL
        },
        stageResults = StageResults(
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList()
        ),
        generalResults = GeneralResults(
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList()
        ),
    )
}