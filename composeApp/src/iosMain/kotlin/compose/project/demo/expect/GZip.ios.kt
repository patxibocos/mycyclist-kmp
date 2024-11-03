package compose.project.demo.expect

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toCValues
import platform.darwin.COMPRESSION_ZLIB
import platform.darwin.compression_decode_buffer

@OptIn(ExperimentalForeignApi::class)
actual fun unGZip(buf: ByteArray): ByteArray {
    val fixedBuf = buf.sliceArray(10 until buf.size - 8)
    memScoped {
        val destinationBuffer = allocArray<UByteVar>(CAPACITY)

        val oldSize = compression_decode_buffer(
            destinationBuffer, CAPACITY.convert(),
            fixedBuf.toUByteArray().toCValues(), fixedBuf.size.convert(),
            null,
            COMPRESSION_ZLIB
        )

        return destinationBuffer.readBytes(oldSize.convert())
    }
}

private const val CAPACITY: Long = 10_000_000