package io.github.patxibocos.mycyclist.ui.util

internal object CodePointUtil {

    private const val MIN_HIGH_SURROGATE = '\uD800'
    private const val MAX_HIGH_SURROGATE = '\uDBFF'
    private const val MIN_LOW_SURROGATE = '\uDC00'
    private const val MAX_LOW_SURROGATE = '\uDFFF'
    private const val MIN_SUPPLEMENTARY_CODE_POINT = 0x010000
    private const val MAX_NON_SUPPLEMENTARY_CODE_POINT = 0xFFFF
    private const val TEN = 10
    private const val LOW_10_BIT_MASK = 0x3FF

    internal fun buildStringFromCodePoints(codePoints: IntArray): String = buildString {
        codePoints.forEach { codePoint ->
            if (codePoint in 0x0000..MAX_NON_SUPPLEMENTARY_CODE_POINT) {
                append(codePoint.toChar())
            } else {
                val highSurrogate =
                    ((codePoint - MIN_SUPPLEMENTARY_CODE_POINT) shr TEN) + MIN_HIGH_SURROGATE.code
                val lowSurrogate =
                    ((codePoint - MIN_SUPPLEMENTARY_CODE_POINT) and LOW_10_BIT_MASK) + MIN_LOW_SURROGATE.code
                append(highSurrogate.toChar(), lowSurrogate.toChar())
            }
        }
    }

    internal fun codePointAt(seq: CharSequence, index: Int): Int {
        val high = seq[index]
        val low = seq.getOrNull(index + 1) ?: return high.code
        return if ((isHighSurrogate(high) && isLowSurrogate(low))) {
            toCodePoint(high, low)
        } else {
            high.code
        }
    }

    private fun isHighSurrogate(ch: Char): Boolean =
        ch in MIN_HIGH_SURROGATE..MAX_HIGH_SURROGATE

    private fun isLowSurrogate(ch: Char): Boolean =
        ch in MIN_LOW_SURROGATE..MAX_LOW_SURROGATE

    private fun toCodePoint(high: Char, low: Char): Int =
        ((high.code - MIN_HIGH_SURROGATE.code) shl TEN) +
            (low.code - MIN_LOW_SURROGATE.code) +
            MIN_SUPPLEMENTARY_CODE_POINT
}
