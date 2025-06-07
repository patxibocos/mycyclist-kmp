package io.github.patxibocos.mycyclist.domain.repository

import io.github.patxibocos.mycyclist.data.firebase.FirebaseDataRepository
import io.github.patxibocos.mycyclist.data.firebase.FirebaseMessagingRepository

internal actual val cyclingDataRepository: CyclingDataRepository = FirebaseDataRepository()
internal actual val messagingRepository: MessagingRepository = FirebaseMessagingRepository()