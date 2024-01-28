package queue.file.modified

import java.security.MessageDigest


actual fun calculatePlatformMD5(input: String): String {
    val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}