package io.github.patxibocos.mycyclist.data.protobuf

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CyclingDataDto(
    @ProtoNumber(1)
    val teams: List<TeamDto> = emptyList(),
    @ProtoNumber(2)
    val riders: List<RiderDto> = emptyList(),
    @ProtoNumber(3)
    val races: List<RaceDto> = emptyList()
)
