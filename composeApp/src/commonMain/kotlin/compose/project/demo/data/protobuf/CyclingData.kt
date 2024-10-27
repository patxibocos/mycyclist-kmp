package compose.project.demo.data.protobuf

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CyclingData(
    @ProtoNumber(1)
    val teams: List<Team> = emptyList(),
    @ProtoNumber(2)
    val riders: List<Rider> = emptyList(),
    @ProtoNumber(3)
    val races: List<Race> = emptyList()
)