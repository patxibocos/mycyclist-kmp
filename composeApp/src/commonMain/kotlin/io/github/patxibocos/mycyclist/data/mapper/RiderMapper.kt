package io.github.patxibocos.mycyclist.data.mapper

import io.github.patxibocos.mycyclist.data.protobuf.RiderDto
import io.github.patxibocos.mycyclist.domain.Rider
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal object RiderMapper {

    internal fun List<RiderDto>.toRiders(): List<Rider> =
        map { it.toDomain() }

    private fun RiderDto.toDomain(): Rider {
        return Rider(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            photo = this.photo,
            country = this.country,
            website = this.website,
            birthDate = this.birthDate?.let {
                Instant.Companion.fromEpochSeconds(it.seconds)
                    .toLocalDateTime(TimeZone.Companion.currentSystemDefault()).date
            },
            birthPlace = this.birthPlace,
            weight = this.weight,
            height = this.height,
            uciRankingPosition = this.uciRankingPosition,
        )
    }
}
