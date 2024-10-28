package compose.project.demo

import compose.project.demo.data.protobuf.Race
import compose.project.demo.data.protobuf.Rider
import compose.project.demo.data.protobuf.Team
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