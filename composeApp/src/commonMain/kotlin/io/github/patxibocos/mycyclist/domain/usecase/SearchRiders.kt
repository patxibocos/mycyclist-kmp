package io.github.patxibocos.mycyclist.domain.usecase

import io.github.patxibocos.mycyclist.domain.entity.Rider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class SearchRiders(private val defaultDispatcher: CoroutineContext = Dispatchers.Default) {

    internal suspend operator fun invoke(
        riders: List<Rider>,
        query: String,
    ): List<Rider> = withContext(defaultDispatcher) {
        if (query.isBlank()) {
            return@withContext riders
        }
        val querySplits = query.trim().split(" ").map { it.trim() }
        riders.filter { rider ->
            // For each of the split, it should be contained either on first or last name
            querySplits.all { q ->
                rider.firstName.contains(
                    q,
                    ignoreCase = true,
                ) || rider.lastName.contains(q, ignoreCase = true)
            }
        }
    }
}
