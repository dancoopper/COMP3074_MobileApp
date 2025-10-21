package ca.gbc.comp3074.mobileapp_tmwa.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PROFILE_PREFS = "profile_prefs"
val Context.profileDataStore by preferencesDataStore(name = PROFILE_PREFS)

object ProfileKeys {
    val FULL_NAME = stringPreferencesKey("full_name")
    val USERNAME = stringPreferencesKey("username")
    val DOB = stringPreferencesKey("date_of_birth")
    val GENDER = stringPreferencesKey("gender")
    val PROFESSION = stringPreferencesKey("profession")
    val EMAIL = stringPreferencesKey("email")
    val PHONE = stringPreferencesKey("phone")
    val IMAGE_URI = stringPreferencesKey("image_uri")
}

data class ProfileUi(
    val fullName: String = "John Doe",
    val username: String = "@john_doe",
    val dateOfBirth: String = "12 Mar 1995",
    val gender: String = "Male",
    val profession: String = "UI/UX Designer",
    val email: String = "john.doe@email.com",
    val phone: String = "+1 (416) 555-7842",
    val imageUri: String? = null
)

class ProfileRepository(private val context: Context) {

    val profileFlow: Flow<ProfileUi> = context.profileDataStore.data.map { prefs ->
        ProfileUi(
            fullName = prefs[ProfileKeys.FULL_NAME] ?: "John Doe",
            username = prefs[ProfileKeys.USERNAME] ?: "@john_doe",
            dateOfBirth = prefs[ProfileKeys.DOB] ?: "12 Mar 1995",
            gender = prefs[ProfileKeys.GENDER] ?: "Male",
            profession = prefs[ProfileKeys.PROFESSION] ?: "UI/UX Designer",
            email = prefs[ProfileKeys.EMAIL] ?: "john.doe@email.com",
            phone = prefs[ProfileKeys.PHONE] ?: "+1 (416) 555-7842",
            imageUri = prefs[ProfileKeys.IMAGE_URI]
        )
    }

    suspend fun saveProfile(p: ProfileUi) {
        context.profileDataStore.edit { prefs ->
            prefs[ProfileKeys.FULL_NAME] = p.fullName
            prefs[ProfileKeys.USERNAME] = p.username
            prefs[ProfileKeys.DOB] = p.dateOfBirth
            prefs[ProfileKeys.GENDER] = p.gender
            prefs[ProfileKeys.PROFESSION] = p.profession
            prefs[ProfileKeys.EMAIL] = p.email
            prefs[ProfileKeys.PHONE] = p.phone
            if (p.imageUri == null) {
                prefs.remove(ProfileKeys.IMAGE_URI)
            } else {
                prefs[ProfileKeys.IMAGE_URI] = p.imageUri
            }
        }
    }
}