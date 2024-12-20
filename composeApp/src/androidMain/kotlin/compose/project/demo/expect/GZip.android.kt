package compose.project.demo.expect

import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

internal actual fun unGZip(buf: ByteArray): ByteArray =
    ByteArrayInputStream(buf).use { inputStream -> GZIPInputStream(inputStream).use { it.readBytes() } }