package io.github.patxibocos.mycyclist.ui.preview

import io.github.patxibocos.mycyclist.domain.entity.GeneralResults
import io.github.patxibocos.mycyclist.domain.entity.ProfileType
import io.github.patxibocos.mycyclist.domain.entity.Race
import io.github.patxibocos.mycyclist.domain.entity.Stage
import io.github.patxibocos.mycyclist.domain.entity.StageResults
import io.github.patxibocos.mycyclist.domain.entity.StageType
import io.github.patxibocos.mycyclist.domain.entity.TeamParticipation
import kotlinx.datetime.Instant

internal fun aRace(
    id: String = "tour-de-france",
    name: String = "Tour de France",
    stages: List<Stage> = listOf(aStage()),
    country: String = "FR",
    website: String = "https://www.letour.com",
    teamParticipations: List<TeamParticipation> = emptyList(),
): Race {
    return Race(
        id = id,
        name = name,
        stages = stages,
        country = country,
        website = website,
        teamParticipations = teamParticipations,
    )
}

internal fun aStage(): Stage {
    return Stage(
        id = "tour-de-france-stage-1",
        distance = 184.9F,
        startDateTime = Instant.DISTANT_FUTURE,
        departure = "Nantua",
        arrival = "Pontarlier",
        profileType = ProfileType.HILLS_FLAT_FINISH,
        stageType = StageType.REGULAR,
        stageResults = StageResults(
            time = emptyList(),
            youth = emptyList(),
            teams = emptyList(),
            kom = emptyList(),
            points = emptyList(),
        ),
        generalResults = GeneralResults(
            time = emptyList(),
            youth = emptyList(),
            teams = emptyList(),
            kom = emptyList(),
            points = emptyList(),
        ),
    )
}
