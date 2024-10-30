package compose.project.demo.domain

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class Rider(
    val id: String,
    val firstName: String,
    val lastName: String,
    val photo: String,
    val country: String,
    val website: String,
    val birthDate: LocalDate,
    val birthPlace: String,
    val weight: Int,
    val height: Int,
    val uciRankingPosition: Int,
) {
    fun fullName(): String {
        return "$firstName $lastName"
    }
}
