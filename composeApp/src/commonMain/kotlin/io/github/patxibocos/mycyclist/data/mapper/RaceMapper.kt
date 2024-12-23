package io.github.patxibocos.mycyclist.data.mapper

import io.github.patxibocos.mycyclist.data.protobuf.ParticipantResultPointsDto
import io.github.patxibocos.mycyclist.data.protobuf.ParticipantResultTimeDto
import io.github.patxibocos.mycyclist.data.protobuf.PlaceDto
import io.github.patxibocos.mycyclist.data.protobuf.PlacePointsDto
import io.github.patxibocos.mycyclist.data.protobuf.RaceDto
import io.github.patxibocos.mycyclist.data.protobuf.RiderParticipationDto
import io.github.patxibocos.mycyclist.data.protobuf.StageDto
import io.github.patxibocos.mycyclist.data.protobuf.TeamParticipationDto
import io.github.patxibocos.mycyclist.domain.GeneralResults
import io.github.patxibocos.mycyclist.domain.ParticipantResultPoints
import io.github.patxibocos.mycyclist.domain.ParticipantResultTime
import io.github.patxibocos.mycyclist.domain.Place
import io.github.patxibocos.mycyclist.domain.PlaceResult
import io.github.patxibocos.mycyclist.domain.ProfileType
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.domain.RiderParticipation
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.domain.StageResults
import io.github.patxibocos.mycyclist.domain.StageType
import io.github.patxibocos.mycyclist.domain.TeamParticipation
import kotlinx.datetime.Instant

internal object RaceMapper {

    internal fun List<RaceDto>.toRaces(): List<Race> =
        map { it.toDomain() }

    private fun RaceDto.toDomain(): Race {
        return Race(
            id = this.id,
            name = this.name,
            country = this.country,
            stages = this.stages.map { it.toDomain() },
            website = this.website,
            teamParticipations = this.teamParticipations.map { it.toDomain() }
        )
    }

    private fun TeamParticipationDto.toDomain(): TeamParticipation {
        return TeamParticipation(
            teamId = this.teamId,
            riderParticipations = this.riderParticipations.mapNotNull { it.toDomain() },
        )
    }

    private fun RiderParticipationDto.toDomain(): RiderParticipation? {
        return RiderParticipation(
            riderId = this.riderId,
            number = this.number ?: return null,
        )
    }

    private fun StageDto.toDomain(): Stage {
        return Stage(
            id = this.id,
            distance = this.distance,
            startDateTime = Instant.Companion.fromEpochSeconds(this.startDateTime?.seconds ?: 0),
            departure = this.departure,
            arrival = this.arrival,
            profileType = when (this.profileType) {
                StageDto.ProfileTypeDto.Unspecified -> null
                StageDto.ProfileTypeDto.Flat -> ProfileType.FLAT
                StageDto.ProfileTypeDto.HillsFlatFinish -> ProfileType.HILLS_FLAT_FINISH
                StageDto.ProfileTypeDto.HillsUphillFinish -> ProfileType.HILLS_UPHILL_FINISH
                StageDto.ProfileTypeDto.MountainsFlatFinish -> ProfileType.MOUNTAINS_FLAT_FINISH
                StageDto.ProfileTypeDto.MountainsUphillFinish -> ProfileType.MOUNTAINS_UPHILL_FINISH
            },
            stageType = when (this.stageType) {
                StageDto.StageTypeDto.Unspecified -> StageType.REGULAR
                StageDto.StageTypeDto.Regular -> StageType.REGULAR
                StageDto.StageTypeDto.IndividualTimeTrial -> StageType.INDIVIDUAL_TIME_TRIAL
                StageDto.StageTypeDto.TeamTimeTrial -> StageType.TEAM_TIME_TRIAL
            },
            stageResults = StageResults(
                this.stageResults.time.map { it.toDomain() },
                this.stageResults.youth.map { it.toDomain() },
                this.stageResults.teams.map { it.toDomain() },
                this.stageResults.kom.map { it.toDomain() },
                this.stageResults.points.map { it.toDomain() },
            ),
            generalResults = GeneralResults(
                this.generalResults.time.map { it.toDomain() },
                this.generalResults.youth.map { it.toDomain() },
                this.generalResults.teams.map { it.toDomain() },
                this.generalResults.kom.map { it.toDomain() },
                this.generalResults.points.map { it.toDomain() },
            ),
        )
    }

    private fun PlacePointsDto.toDomain(): PlaceResult {
        return PlaceResult(
            place = this.place.toDomain(),
            points = this.points.map { it.toDomain() },
        )
    }

    private fun ParticipantResultTimeDto.toDomain(): ParticipantResultTime {
        return ParticipantResultTime(
            position = this.position,
            participantId = this.participantId,
            time = this.time.toLong(),
        )
    }

    private fun ParticipantResultPointsDto.toDomain(): ParticipantResultPoints {
        return ParticipantResultPoints(
            position = this.position,
            participant = this.participantId,
            points = this.points,
        )
    }

    private fun PlaceDto.toDomain(): Place {
        return Place(
            name = this.name,
            distance = this.distance,
        )
    }
}
