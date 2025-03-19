package io.github.patxibocos.mycyclist.gzip

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
internal actual fun unGZip(buf: ByteArray): ByteArray {
    val fixedBuf = buf.sliceArray(GzipHeaderSize until buf.size - GzipFooterSize)
    memScoped {
        val destinationBuffer = allocArray<UByteVar>(BufferCapacity)

        val oldSize = compression_decode_buffer(
            dst_buffer = destinationBuffer,
            dst_size = BufferCapacity.convert(),
            src_buffer = fixedBuf.toUByteArray().toCValues(),
            src_size = fixedBuf.size.convert(),
            scratch_buffer = null,
            algorithm = COMPRESSION_ZLIB,
        )

        return destinationBuffer.readBytes(oldSize.convert())
    }
}

private const val BufferCapacity: Long = 10_000_000
private const val GzipHeaderSize: Int = 10
private const val GzipFooterSize = 8
