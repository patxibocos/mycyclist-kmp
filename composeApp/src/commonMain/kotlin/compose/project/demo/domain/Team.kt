package compose.project.demo.domain

internal data class Team(
    val id: String,
    val name: String,
    val status: TeamStatus,
    val abbreviation: String?,
    val country: String,
    val bike: String,
    val jersey: String,
    val website: String?,
    val riderIds: List<String>,
)

internal enum class TeamStatus {
    WORLD_TEAM,
    PRO_TEAM,
}
