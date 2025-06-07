package io.github.patxibocos.mycyclist.domain.repository

import io.github.patxibocos.mycyclist.data.mapper.RaceMapper.toRaces
import io.github.patxibocos.mycyclist.data.mapper.RiderMapper.toRiders
import io.github.patxibocos.mycyclist.data.mapper.TeamMapper.toTeams
import io.github.patxibocos.mycyclist.data.protobuf.CyclingDataDto
import io.github.patxibocos.mycyclist.domain.entity.CyclingData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

internal actual val cyclingDataRepository: CyclingDataRepository = object : CyclingDataRepository {
    private val _cyclingData = MutableSharedFlow<CyclingData>(replay = 1)
    override val cyclingData: Flow<CyclingData> = _cyclingData

    override fun initialize() {
        MainScope().launch {
            refresh()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun refresh(): Boolean {
        val file = this::class.java.getResourceAsStream("/cycling_data_2024.pb") ?: return false
        val cyclingDataDto = ProtoBuf.decodeFromByteArray<CyclingDataDto>(file.readAllBytes())
        val teams = cyclingDataDto.teams.toTeams()
        val riders = cyclingDataDto.riders.toRiders()
        val races = cyclingDataDto.races.toRaces()
        val cyclingData = CyclingData(races, teams, riders)
        _cyclingData.emit(cyclingData)
        return true
    }
}
internal actual val messagingRepository: MessagingRepository = object : MessagingRepository {
    override fun initialize() {}
}