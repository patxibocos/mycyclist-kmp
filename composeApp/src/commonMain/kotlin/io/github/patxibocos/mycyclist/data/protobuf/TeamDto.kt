package io.github.patxibocos.mycyclist.data.protobuf

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class TeamDto(
    @ProtoNumber(1)
    val id: String,
    @ProtoNumber(2)
    val name: String,
    @ProtoNumber(3)
    val status: Status = Status.Unspecified,
    @ProtoNumber(4)
    val abbreviation: String? = null,
    @ProtoNumber(5)
    val country: String,
    @ProtoNumber(6)
    val bike: String,
    @ProtoNumber(7)
    val jersey: String,
    @ProtoNumber(8)
    val year: Int,
    @ProtoNumber(9)
    val riderIds: List<String> = emptyList(),
    @ProtoNumber(10)
    val website: String? = null,
) {
    enum class Status {
        @ProtoNumber(0)
        Unspecified,

        @ProtoNumber(1)
        WorldTeam,

        @ProtoNumber(2)
        ProTeam,
    }
}
