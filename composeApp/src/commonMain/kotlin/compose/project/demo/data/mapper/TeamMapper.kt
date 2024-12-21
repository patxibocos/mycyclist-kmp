package compose.project.demo.data.mapper

import compose.project.demo.data.protobuf.TeamDto
import compose.project.demo.domain.Team
import compose.project.demo.domain.TeamStatus

internal object TeamMapper {

    internal fun List<TeamDto>.toTeams(): List<Team> =
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
}
