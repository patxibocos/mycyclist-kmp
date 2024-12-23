package io.github.patxibocos.mycyclist.data.protobuf

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RiderDto(
    @ProtoNumber(1)
    val id: String,
    @ProtoNumber(2)
    val firstName: String,
    @ProtoNumber(3)
    val lastName: String,
    @ProtoNumber(4)
    val country: String,
    @ProtoNumber(5)
    val birthDate: TimestampDto? = null,
    @ProtoNumber(6)
    val photo: String,
    @ProtoNumber(7)
    val website: String? = null,
    @ProtoNumber(8)
    val birthPlace: String? = null,
    @ProtoNumber(9)
    val weight: Int? = null,
    @ProtoNumber(10)
    val height: Int? = null,
    @ProtoNumber(11)
    val uciRankingPosition: Int? = null,
)
