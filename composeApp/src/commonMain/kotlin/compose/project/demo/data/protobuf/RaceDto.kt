package compose.project.demo.data.protobuf

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RaceDto(
    @ProtoNumber(1)
    val id: String,
    @ProtoNumber(2)
    val name: String,
    @ProtoNumber(3)
    val country: String,
    @ProtoNumber(4)
    val stages: List<StageDto> = emptyList(),
    @ProtoNumber(5)
    val teamParticipations: List<TeamParticipation> = emptyList(),
    @ProtoNumber(6)
    val website: String?,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class StageDto(
    @ProtoNumber(1)
    val id: String = "",
    @ProtoNumber(2)
    val startDateTime: TimestampDto? = null,
    @ProtoNumber(3)
    val distance: Float = 0f,
    @ProtoNumber(4)
    val profileType: ProfileType = ProfileType.Unspecified,
    @ProtoNumber(5)
    val departure: String? = null,
    @ProtoNumber(6)
    val arrival: String? = null,
    @ProtoNumber(7)
    val stageType: StageType = StageType.Unspecified,
    @ProtoNumber(8)
    val stageResults: StageResults? = null,
    @ProtoNumber(9)
    val generalResults: GeneralResults? = null,
) {
    enum class ProfileType {
        @ProtoNumber(0)
        Unspecified,

        @ProtoNumber(1)
        Flat,

        @ProtoNumber(2)
        HillsFlatFinish,

        @ProtoNumber(3)
        HillsUphillFinish,

        @ProtoNumber(4)
        MountainsFlatFinish,

        @ProtoNumber(5)
        MountainsUphillFinish,
    }

    enum class StageType {
        @ProtoNumber(0)
        Unspecified,

        @ProtoNumber(1)
        Regular,

        @ProtoNumber(2)
        IndividualTimeTrial,

        @ProtoNumber(3)
        TeamTimeTrial,
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class TeamParticipation(
    @ProtoNumber(1)
    val teamId: String,
    @ProtoNumber(2)
    val riderParticipations: List<RiderParticipation> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RiderParticipation(
    @ProtoNumber(1)
    val riderId: String,
    @ProtoNumber(2)
    val number: Int? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class StageResults(
    @ProtoNumber(1)
    val time: List<ParticipantResultTime> = emptyList(),
    @ProtoNumber(2)
    val youth: List<ParticipantResultTime> = emptyList(),
    @ProtoNumber(3)
    val teams: List<ParticipantResultTime> = emptyList(),
    @ProtoNumber(4)
    val kom: List<PlacePoints> = emptyList(),
    @ProtoNumber(5)
    val points: List<PlacePoints> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GeneralResults(
    @ProtoNumber(1)
    val time: List<ParticipantResultTime> = emptyList(),
    @ProtoNumber(2)
    val youth: List<ParticipantResultTime> = emptyList(),
    @ProtoNumber(3)
    val teams: List<ParticipantResultTime> = emptyList(),
    @ProtoNumber(4)
    val kom: List<ParticipantResultPoints> = emptyList(),
    @ProtoNumber(5)
    val points: List<ParticipantResultPoints> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ParticipantResultTime(
    @ProtoNumber(1)
    val position: Int,
    @ProtoNumber(2)
    val participantId: String,
    @ProtoNumber(3)
    val time: Int,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ParticipantResultPoints(
    @ProtoNumber(1)
    val position: Int,
    @ProtoNumber(2)
    val participantId: String,
    @ProtoNumber(3)
    val points: Int,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PlacePoints(
    @ProtoNumber(1)
    val place: Place,
    @ProtoNumber(2)
    val points: List<ParticipantResultPoints> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Place(
    @ProtoNumber(1)
    val name: String,
    @ProtoNumber(2)
    val distance: Float,
)