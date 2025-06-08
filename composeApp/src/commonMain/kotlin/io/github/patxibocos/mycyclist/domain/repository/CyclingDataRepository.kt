package io.github.patxibocos.mycyclist.domain.repository

import io.github.patxibocos.mycyclist.domain.entity.CyclingData
import kotlinx.coroutines.flow.Flow

internal interface CyclingDataRepository {
    val cyclingData: Flow<CyclingData>

    fun initialize()
    suspend fun refresh(): Boolean
}

internal interface MessagingRepository {
    fun initialize() {}
}

internal expect val cyclingDataRepository: CyclingDataRepository
internal expect val messagingRepository: MessagingRepository
