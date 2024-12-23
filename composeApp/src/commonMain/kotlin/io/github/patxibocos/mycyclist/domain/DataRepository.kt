package io.github.patxibocos.mycyclist.domain

import io.github.patxibocos.mycyclist.data.firebase.FirebaseDataRepository
import kotlinx.coroutines.flow.Flow

internal interface DataRepository {
    val teams: Flow<List<Team>>
    val riders: Flow<List<Rider>>
    val races: Flow<List<Race>>

    suspend fun refresh(): Boolean
}

internal val firebaseDataRepository: DataRepository = FirebaseDataRepository()
