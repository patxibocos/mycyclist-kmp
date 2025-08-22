package io.github.patxibocos.mycyclist.data.protobuf

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class TimestampDto(
    @ProtoNumber(1)
    val seconds: Long = 0,
    @ProtoNumber(2)
    val nanos: Int = 0,
)
