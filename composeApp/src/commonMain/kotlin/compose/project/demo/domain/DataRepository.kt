package compose.project.demo.domain

import compose.project.demo.data.firebase.FirebaseDataRepository
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    val teams: Flow<List<Team>>
    val riders: Flow<List<Rider>>
    val races: Flow<List<Race>>

    suspend fun refresh(): Boolean
}

val firebaseDataRepository: DataRepository = FirebaseDataRepository()