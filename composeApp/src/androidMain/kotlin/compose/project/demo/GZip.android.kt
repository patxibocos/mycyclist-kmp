package compose.project.demo

import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

actual fun unGZip(buf: ByteArray): ByteArray {
    return ByteArrayInputStream(buf).use { inputStream -> GZIPInputStream(inputStream).use { it.readBytes() } }
}