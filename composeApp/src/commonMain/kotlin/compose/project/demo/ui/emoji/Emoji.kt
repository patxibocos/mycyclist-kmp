package compose.project.demo.ui.emoji

import compose.project.demo.util.CodePointUtil
import compose.project.demo.util.CodePointUtil.Companion.buildStringFromCodePoints

fun getCountryEmoji(countryCode: String): String {
    val codePoints = countryCode.uppercase()
        .map { char -> 127397 + CodePointUtil.codePointAt(char.toString(), 0) }
        .toIntArray()
    return buildStringFromCodePoints(codePoints)
}