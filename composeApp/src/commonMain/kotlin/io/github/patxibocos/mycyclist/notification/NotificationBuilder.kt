package io.github.patxibocos.mycyclist.notification

import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.Race
import io.github.patxibocos.mycyclist.domain.Stage
import io.github.patxibocos.mycyclist.domain.StageType
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import kotlinx.coroutines.flow.first
import mycyclist.composeapp.generated.resources.Res
import mycyclist.composeapp.generated.resources.notifications_gc_results
import mycyclist.composeapp.generated.resources.notifications_race_results
import mycyclist.composeapp.generated.resources.notifications_stage_results
import org.jetbrains.compose.resources.getString

internal class NotificationBuilder(private val dataRepository: DataRepository = firebaseDataRepository) {

    suspend fun buildNotificationFromPayload(data: Map<String, String>): Pair<String, String?> {
        val (race, stage) = getRaceAndStage(data)
        val winner = stage.stageResults.time.first().participantId
        val stageWinnerName = if (stage.stageType == StageType.TEAM_TIME_TRIAL) {
            requireNotNull(dataRepository.cyclingData.first().teams.find { it.id == winner }).name
        } else {
            requireNotNull(dataRepository.cyclingData.first().riders.find { it.id == winner }).fullName()
        }
        val gcFirstName = requireNotNull(
            dataRepository.cyclingData.first().riders.find { it.id == stage.generalResults.time.first().participantId },
        ).fullName()
        val stageNumber = race.stages.indexOfFirst { it.id == stage.id } + 1
        val notificationText: String
        val notificationSubtext: String?
        if (race.isSingleDay()) {
            notificationText = getString(Res.string.notifications_race_results, stageWinnerName)
            notificationSubtext = null
        } else {
            notificationText =
                getString(Res.string.notifications_stage_results, stageWinnerName, stageNumber)
            notificationSubtext = getString(Res.string.notifications_gc_results, gcFirstName)
        }
        return notificationText to notificationSubtext
    }

    private suspend fun getRaceAndStage(messageData: Map<String, String>): Pair<Race, Stage> {
        val raceId = messageData["race-id"]
        val stageId = messageData["stage-id"]
        val race = requireNotNull(dataRepository.cyclingData.first().races.find { it.id == raceId })
        val stage = requireNotNull(race.stages.find { it.id == stageId })
        return race to stage
    }
}
