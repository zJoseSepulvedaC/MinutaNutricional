package com.sepulveda.minutanutricional.data

import java.security.MessageDigest
import java.security.SecureRandom

object HashUtil {
    fun randomSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, saltHex: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val saltBytes = hexStringToByteArray(saltHex)
        md.update(saltBytes)
        val hashed = md.digest(password.toByteArray(Charsets.UTF_8))
        return hashed.joinToString("") { "%02x".format(it) }
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}
