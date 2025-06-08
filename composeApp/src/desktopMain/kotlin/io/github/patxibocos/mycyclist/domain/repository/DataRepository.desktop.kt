package io.github.patxibocos.mycyclist.domain.repository

import com.google.auth.oauth2.GoogleCredentials
import io.github.patxibocos.mycyclist.data.gzip.unGZip
import io.github.patxibocos.mycyclist.data.mapper.RaceMapper.toRaces
import io.github.patxibocos.mycyclist.data.mapper.RiderMapper.toRiders
import io.github.patxibocos.mycyclist.data.mapper.TeamMapper.toTeams
import io.github.patxibocos.mycyclist.data.protobuf.CyclingDataDto
import io.github.patxibocos.mycyclist.domain.entity.CyclingData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.io.encoding.Base64

internal actual val cyclingDataRepository: CyclingDataRepository = object : CyclingDataRepository {
    private val _cyclingData = MutableSharedFlow<CyclingData>(replay = 1)
    override val cyclingData: Flow<CyclingData> = _cyclingData

    override fun initialize() {
        MainScope().launch {
            refresh()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun refresh(): Boolean {
        val projectId = "pcs-scraper"
        val serviceAccountFile =
            this::class.java.getResourceAsStream("/firebase-remote-config-service-account.json")
                ?: error("Service account file not found")
        val credentials = GoogleCredentials
            .fromStream(serviceAccountFile)
            .createScoped("https://www.googleapis.com/auth/firebase.remoteconfig")
        credentials.refreshIfExpired()
        val accessToken = credentials.accessToken.tokenValue
        val client = HttpClient(CIO)
        val url = "https://firebaseremoteconfig.googleapis.com/v1/projects/$projectId/remoteConfig"
        val response: String = client.get(url) {
            bearerAuth(accessToken)
        }.body()
        val remoteConfigValue = Json.parseToJsonElement(response)
            .jsonObject["parameters"]
            ?.jsonObject["cycling_data"]
            ?.jsonObject["defaultValue"]
            ?.jsonObject["value"]
            ?.jsonPrimitive?.content ?: return false
        val unzipped = unGZip(Base64.decode(remoteConfigValue))
        val cyclingDataDto = ProtoBuf.decodeFromByteArray<CyclingDataDto>(unzipped)
        val teams = cyclingDataDto.teams.toTeams()
        val riders = cyclingDataDto.riders.toRiders()
        val races = cyclingDataDto.races.toRaces()
        val cyclingData = CyclingData(races, teams, riders)
        _cyclingData.emit(cyclingData)
        return true
    }
}
internal actual val messagingRepository: MessagingRepository = object : MessagingRepository {}
