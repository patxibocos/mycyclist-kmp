package io.github.patxibocos.mycyclist.domain

import io.github.patxibocos.mycyclist.data.firebase.FirebaseDataRepository
import io.github.patxibocos.mycyclist.data.firebase.FirebaseMessaging
import kotlinx.coroutines.flow.Flow

internal interface DataRepository {
    val cyclingData: Flow<CyclingData>

    fun initialize()
    suspend fun refresh(): Boolean
}

internal val firebaseDataRepository: DataRepository = FirebaseDataRepository()
internal val firebaseMessaging: FirebaseMessaging = FirebaseMessaging()
