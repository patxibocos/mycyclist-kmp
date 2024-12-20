package compose.project.demo.data.firebase

import compose.project.demo.data.protobuf.ParticipantResultPointsDto
import compose.project.demo.data.protobuf.ParticipantResultTimeDto
import compose.project.demo.data.protobuf.PlaceDto
import compose.project.demo.data.protobuf.PlacePointsDto
import compose.project.demo.data.protobuf.RaceDto
import compose.project.demo.data.protobuf.RiderDto
import compose.project.demo.data.protobuf.RiderParticipationDto
import compose.project.demo.data.protobuf.StageDto
import compose.project.demo.data.protobuf.TeamDto
import compose.project.demo.data.protobuf.TeamParticipationDto
import compose.project.demo.domain.GeneralResults
import compose.project.demo.domain.ParticipantResultPoints
import compose.project.demo.domain.ParticipantResultTime
import compose.project.demo.domain.Place
import compose.project.demo.domain.PlaceResult
import compose.project.demo.domain.ProfileType
import compose.project.demo.domain.Race
import compose.project.demo.domain.Rider
import compose.project.demo.domain.RiderParticipation
import compose.project.demo.domain.Stage
import compose.project.demo.domain.StageResults
import compose.project.demo.domain.StageType
import compose.project.demo.domain.Team
import compose.project.demo.domain.TeamParticipation
import compose.project.demo.domain.TeamStatus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal object FirebaseDataMapper {

    internal fun List<TeamDto>.toTeams(): List<Team> =
        map { it.toDomain() }

    internal fun List<RiderDto>.toRiders(): List<Rider> =
        map { it.toDomain() }

    internal fun List<RaceDto>.toRaces(): List<Race> =
        map { it.toDomain() }

    private fun TeamDto.toDomain(): Team {
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

    private fun RiderDto.toDomain(): Rider {
        return Rider(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            photo = this.photo,
            country = this.country,
            website = this.website,
            birthDate = this.birthDate?.let {
                Instant.fromEpochSeconds(it.seconds)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
            },
            birthPlace = this.birthPlace,
            weight = this.weight,
            height = this.height,
            uciRankingPosition = this.uciRankingPosition,
        )
    }

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
            startDateTime = Instant.fromEpochSeconds(this.startDateTime?.seconds ?: 0),
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
