package io.github.patxibocos.mycyclist.domain

internal sealed class RiderResult(open val race: Race, open val position: Int) {

    data class RaceResult(override val race: Race, override val position: Int) :
        RiderResult(race, position)

    data class StageResult(
        override val race: Race,
        val stage: Stage,
        val stageNumber: Int,
        override val position: Int,
    ) : RiderResult(race, position)
}
