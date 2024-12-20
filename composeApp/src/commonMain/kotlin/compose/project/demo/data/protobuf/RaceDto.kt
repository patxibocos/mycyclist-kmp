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
    val teamParticipations: List<TeamParticipationDto> = emptyList(),
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
    val profileType: ProfileTypeDto = ProfileTypeDto.Unspecified,
    @ProtoNumber(5)
    val departure: String? = null,
    @ProtoNumber(6)
    val arrival: String? = null,
    @ProtoNumber(7)
    val stageType: StageTypeDto = StageTypeDto.Unspecified,
    @ProtoNumber(8)
    val stageResults: StageResultsDto,
    @ProtoNumber(9)
    val generalResults: GeneralResultsDto,
) {
    enum class ProfileTypeDto {
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

    enum class StageTypeDto {
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
data class TeamParticipationDto(
    @ProtoNumber(1)
    val teamId: String,
    @ProtoNumber(2)
    val riderParticipations: List<RiderParticipationDto> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RiderParticipationDto(
    @ProtoNumber(1)
    val riderId: String,
    @ProtoNumber(2)
    val number: Int? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class StageResultsDto(
    @ProtoNumber(1)
    val time: List<ParticipantResultTimeDto> = emptyList(),
    @ProtoNumber(2)
    val youth: List<ParticipantResultTimeDto> = emptyList(),
    @ProtoNumber(3)
    val teams: List<ParticipantResultTimeDto> = emptyList(),
    @ProtoNumber(4)
    val kom: List<PlacePointsDto> = emptyList(),
    @ProtoNumber(5)
    val points: List<PlacePointsDto> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GeneralResultsDto(
    @ProtoNumber(1)
    val time: List<ParticipantResultTimeDto> = emptyList(),
    @ProtoNumber(2)
    val youth: List<ParticipantResultTimeDto> = emptyList(),
    @ProtoNumber(3)
    val teams: List<ParticipantResultTimeDto> = emptyList(),
    @ProtoNumber(4)
    val kom: List<ParticipantResultPointsDto> = emptyList(),
    @ProtoNumber(5)
    val points: List<ParticipantResultPointsDto> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ParticipantResultTimeDto(
    @ProtoNumber(1)
    val position: Int,
    @ProtoNumber(2)
    val participantId: String,
    @ProtoNumber(3)
    val time: Int,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ParticipantResultPointsDto(
    @ProtoNumber(1)
    val position: Int,
    @ProtoNumber(2)
    val participantId: String,
    @ProtoNumber(3)
    val points: Int,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PlacePointsDto(
    @ProtoNumber(1)
    val place: PlaceDto,
    @ProtoNumber(2)
    val points: List<ParticipantResultPointsDto> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PlaceDto(
    @ProtoNumber(1)
    val name: String,
    @ProtoNumber(2)
    val distance: Float,
)
