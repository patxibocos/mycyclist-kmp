package io.github.patxibocos.mycyclist.ui.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.number
import kotlin.math.roundToInt

private const val DaysInWeek = 7
private const val DaysInMonth = 30
private const val MonthsInYear = 12

fun humanDatesDiff(start: LocalDate, end: LocalDate): String {
    val days = start.daysUntil(end).toDouble()

    return when {
        days < DaysInWeek -> {
            val value = days.roundToInt()
            "$value day${if (value != 1) "s" else ""}"
        }

        days < DaysInMonth -> {
            val weeks = (days / DaysInWeek).roundToInt()
            "$weeks week${if (weeks != 1) "s" else ""}"
        }

        else -> {
            val months =
                (
                    (
                        (end.year - start.year) * MonthsInYear +
                            (end.month.number - start.month.number)
                        ).toDouble()
                    ).roundToInt()
            "$months month${if (months != 1) "s" else ""}"
        }
    }
}
