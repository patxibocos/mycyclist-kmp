package io.github.patxibocos.mycyclist.domain.entity

internal data class CyclingData(
    val races: List<Race>,
    val teams: List<Team>,
    val riders: List<Rider>,
)
