package com.mindbloom.app.data.repository

import com.mindbloom.app.data.local.UserDao
import com.mindbloom.app.data.local.UserEntity
import com.mindbloom.app.data.prefs.AppPreferences
import com.mindbloom.app.util.Security
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Result of a login or registration attempt. */
sealed interface AuthResult {
    data class Success(val user: UserEntity) : AuthResult
    data class Failure(val message: String) : AuthResult
}

class UserRepository(
    private val userDao: UserDao,
    private val prefs: AppPreferences
) {

    fun observeCurrentUser(): Flow<UserEntity?> {
        val email = prefs.signedInEmail ?: return flowOf(null)
        return userDao.observeByEmail(email)
    }

    suspend fun currentUser(): UserEntity? =
        prefs.signedInEmail?.let { userDao.getByEmail(it) }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        wellnessGoal: String
    ): AuthResult {
        val normalised = email.trim().lowercase()
        if (userDao.countByEmail(normalised) > 0) {
            return AuthResult.Failure("An account already exists for this email")
        }
        val user = UserEntity(
            email = normalised,
            name = name.trim(),
            passwordHash = Security.hash(password),
            wellnessGoal = wellnessGoal
        )
        userDao.insert(user)
        prefs.signedInEmail = normalised
        return AuthResult.Success(user)
    }

    suspend fun login(email: String, password: String, rememberMe: Boolean): AuthResult {
        val normalised = email.trim().lowercase()
        val user = userDao.getByEmail(normalised)
            ?: return AuthResult.Failure("No account found for this email")
        if (user.passwordHash != Security.hash(password)) {
            return AuthResult.Failure("Incorrect password, try again")
        }
        prefs.rememberMe = rememberMe
        prefs.signedInEmail = normalised
        return AuthResult.Success(user)
    }

    /** Used by the "Continue with Google" button, which is mocked locally. */
    suspend fun continueWithGoogle(): AuthResult {
        val email = "punara@email.com"
        val existing = userDao.getByEmail(email)
        val user = existing ?: UserEntity(
            email = email,
            name = "Punara Seniduni",
            passwordHash = Security.hash("google-sign-in"),
            wellnessGoal = "Reduce Stress"
        ).also { userDao.insert(it) }
        prefs.signedInEmail = email
        return AuthResult.Success(user)
    }

    suspend fun updateProfile(name: String, wellnessGoal: String) {
        val user = currentUser() ?: return
        userDao.update(user.copy(name = name.trim(), wellnessGoal = wellnessGoal))
    }

    suspend fun addExperience(points: Int) {
        val user = currentUser() ?: return
        var xp = user.experience + points
        var level = user.level
        val perLevel = 2000
        while (xp >= perLevel) {
            xp -= perLevel
            level++
        }
        userDao.update(user.copy(experience = xp, level = level))
    }

    fun signOut() = prefs.signOut()
}
