package com.mindbloom.app.util

import java.security.MessageDigest

/**
 * Passwords are never stored in plain text, even in this local-only build.
 * A real deployment would use a salted KDF such as Argon2 or bcrypt; SHA-256
 * keeps the sample self-contained with no extra dependencies.
 */
object Security {

    fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, hash: String): Boolean = hash(password) == hash
}
