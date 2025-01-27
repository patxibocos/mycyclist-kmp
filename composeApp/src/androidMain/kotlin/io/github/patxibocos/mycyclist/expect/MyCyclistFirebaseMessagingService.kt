package io.github.patxibocos.mycyclist.expect

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.patxibocos.mycyclist.domain.DataRepository
import io.github.patxibocos.mycyclist.domain.firebaseDataRepository
import io.github.patxibocos.mycyclist.notification.NotificationBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
internal class MyCyclistFirebaseMessagingService(
    private val dataRepository: DataRepository = firebaseDataRepository,
    private val notificationBuilder: NotificationBuilder = NotificationBuilder(),
) : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        MainScope().launch {
            if (!dataRepository.refresh()) {
                // If data fails to refresh, we skip the rest
                return@launch
            }
            val data = message.data
            val (title, subtitle) = notificationBuilder.buildNotificationFromPayload(data)
        }
        super.onMessageReceived(message)
    }

}