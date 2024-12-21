package compose.project.demo.ui.emoji

import compose.project.demo.util.CodePointUtil
import compose.project.demo.util.CodePointUtil.buildStringFromCodePoints

internal object EmojiUtil {

    private const val UNICODE_REGIONAL_INDICATOR_OFFSET = 127397

    internal fun getCountryEmoji(countryCode: String): String {
        val codePoints = countryCode.uppercase()
            .map { char ->
                UNICODE_REGIONAL_INDICATOR_OFFSET + CodePointUtil.codePointAt(
                    char.toString(),
                    0
                )
            }
            .toIntArray()
        return buildStringFromCodePoints(codePoints)
    }
}
