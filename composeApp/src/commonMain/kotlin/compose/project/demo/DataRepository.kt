package compose.project.demo

import compose.project.demo.domain.Race
import compose.project.demo.domain.Rider
import compose.project.demo.domain.Team
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    val teams: Flow<List<Team>>
    val riders: Flow<List<Rider>>
    val races: Flow<List<Race>>

    suspend fun refresh(): Boolean
}

val dataRepository by lazy {
    FirebaseDataRepository()
}