package io.github.patxibocos.mycyclist.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.FirebaseMessaging
import dev.gitlive.firebase.messaging.messaging

internal class FirebaseMessaging(
    private val firebaseMessaging: FirebaseMessaging = Firebase.messaging,
) {

    internal fun initialize() {
        firebaseMessaging.subscribeToTopic("stage-results")
    }
}
