package io.github.patxibocos.mycyclist.data.gzip

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.patxibocos.mycyclist.domain.repository.DataRepository
import io.github.patxibocos.mycyclist.domain.repository.firebaseDataRepository
import io.github.patxibocos.mycyclist.ui.notification.NotificationBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
internal class MyCyclistFirebaseMessagingService(
    private val dataRepository: DataRepository = firebaseDataRepository,
    private val notificationBuilder: NotificationBuilder = NotificationBuilder(),
    private val mainScope: CoroutineScope = MainScope(),
) : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        mainScope.launch {
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
