package io.github.patxibocos.mycyclist.domain

import kotlinx.collections.immutable.ImmutableList

internal data class RiderParticipations(
    val pastParticipations: ImmutableList<Participation>,
    val currentParticipation: Participation?,
    val futureParticipations: ImmutableList<Participation>,
)
