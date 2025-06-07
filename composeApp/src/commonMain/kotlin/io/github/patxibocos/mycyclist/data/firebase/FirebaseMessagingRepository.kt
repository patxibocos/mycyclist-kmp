package io.github.patxibocos.mycyclist.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.FirebaseMessaging
import dev.gitlive.firebase.messaging.messaging
import io.github.patxibocos.mycyclist.domain.repository.MessagingRepository

internal class FirebaseMessagingRepository(
    private val firebaseMessaging: FirebaseMessaging = Firebase.messaging,
) : MessagingRepository {

    override fun initialize() {
        firebaseMessaging.subscribeToTopic("stage-results")
    }
}
