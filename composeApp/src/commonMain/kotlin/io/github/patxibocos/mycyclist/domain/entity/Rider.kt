package io.github.patxibocos.mycyclist.domain.entity

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

internal data class Rider(
    val id: String,
    val firstName: String,
    val lastName: String,
    val photo: String,
    val country: String,
    val website: String?,
    val birthDate: LocalDate?,
    val birthPlace: String?,
    val weight: Int?,
    val height: Int?,
    val uciRankingPosition: Int?,
) {
    fun fullName(): String {
        return "$firstName $lastName"
    }

    fun age(): Int? {
        if (birthDate == null) {
            return null
        }
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val baseAge = today.year - birthDate.year

        val birthdayPassedThisYear = today.monthNumber > birthDate.monthNumber ||
            (today.monthNumber == birthDate.monthNumber && today.dayOfMonth >= birthDate.dayOfMonth)

        return if (birthdayPassedThisYear) baseAge else baseAge - 1
    }
}
