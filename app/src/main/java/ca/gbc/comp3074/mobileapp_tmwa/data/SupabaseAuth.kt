package ca.gbc.comp3074.mobileapp_tmwa.data

import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.OTP
//import io.github.jan.supabase.auth.providers.builtin.OtpType
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

sealed class AuthResult {
    data class Success(val data: JSONObject?) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

object SupabaseAuth {

    private fun getBuildConfigString(fieldName: String): String {
        return try {
            val cls = Class.forName("ca.gbc.comp3074.mobileapp_tmwa.BuildConfig")
            val f = cls.getField(fieldName)
            (f.get(null) as? String) ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    // Initialize Client
    val supabase = createSupabaseClient(
        supabaseUrl = getBuildConfigString("SUPABASE_URL"),
        supabaseKey = getBuildConfigString("SUPABASE_KEY")
    ) {
        install(Postgrest)
        install(Auth) // Correctly refers to io.github.jan.supabase.auth.Auth
    }

    // Helper to convert Supabase Session to your JSON format
    private fun createSessionJson(): JSONObject? {
        val session = supabase.auth.currentSessionOrNull() ?: return null
        return JSONObject().apply {
            put("access_token", session.accessToken)
            put("refresh_token", session.refreshToken)
            put("user_id", session.user?.id)
            put("email", session.user?.email)
        }
    }

    // ------------------------------------------------------------------
    // FUNCTIONS FOR LoginScreen.kt and RegisterScreen.kt (Email/Password)
    // ------------------------------------------------------------------

    suspend fun signUp(email: String, pass: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = pass
                }

                // Check if session exists (it might be null if email confirmation is required)
                val json = createSessionJson()

                // Even if json is null (waiting for email confirm), we consider it a success
                // so the UI can navigate or show a "Check your email" message.
                AuthResult.Success(json)
            } catch (e: Exception) {
                Log.e("SupabaseAuth", "SignUp Error", e)
                AuthResult.Error(e.message ?: "Sign up failed")
            }
        }
    }

    suspend fun signIn(email: String, pass: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = pass
                }

                val json = createSessionJson()
                if (json != null) {
                    AuthResult.Success(json)
                } else {
                    AuthResult.Error("Login failed: No session created")
                }
            } catch (e: Exception) {
                Log.e("SupabaseAuth", "SignIn Error", e)
                // Simple error mapping
                val msg = when {
                    e.message?.contains("Invalid login credentials") == true -> "Invalid email or password"
                    else -> e.message ?: "Login failed"
                }
                AuthResult.Error(msg)
            }
        }
    }

    // ------------------------------------------------------------------
    // OPTIONAL: OTP FUNCTIONS (From your previous requests)
    // ------------------------------------------------------------------

    suspend fun sendEmailOtp(email: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.signInWith(OTP) {
                    this.email = email
                }
                AuthResult.Success(null)
            } catch (e: Exception) {
                Log.e("SupabaseAuth", "SendOTP Error", e)
                AuthResult.Error(e.message ?: "Failed to send OTP")
            }
        }
    }

    suspend fun verifyEmailOtp(email: String, token: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.verifyEmailOtp(
                    type = OtpType.Email.EMAIL,
                    email = email,
                    token = token
                )
                val json = createSessionJson()
                if (json != null) {
                    AuthResult.Success(json)
                } else {
                    AuthResult.Error("Session creation failed")
                }
            } catch (e: Exception) {
                Log.e("SupabaseAuth", "VerifyOTP Error", e)
                AuthResult.Error(if (e.message?.contains("Token has expired") == true) "OTP has expired" else "Invalid code")
            }
        }
    }
}



//package ca.gbc.comp3074.mobileapp_tmwa.data
//
//import android.util.Log
//import io.github.jan.supabase.auth.Auth
//import io.github.jan.supabase.createSupabaseClient
//import io.github.jan.supabase.postgrest.Postgrest
//import io.ktor.websocket.WebSocketDeflateExtension.Companion.install
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import org.json.JSONObject
//import java.io.BufferedReader
//import java.io.InputStreamReader
//import java.io.OutputStreamWriter
//import java.net.HttpURLConnection
//import java.net.URL
//import java.net.URLEncoder
//
//
//import io.github.jan.supabase.gotrue.Auth
//import io.github.jan.supabase.gotrue.auth
//import io.github.jan.supabase.gotrue.providers.builtin.OTP
//import io.github.jan.supabase.gotrue.providers.builtin.OtpType
//
//
//sealed class AuthResult {
//    data class Success(val data: JSONObject?) : AuthResult()
//    data class Error(val message: String) : AuthResult()
//}
//
//object SupabaseAuth {
//    // Read BuildConfig fields via reflection on the generated BuildConfig class name
//    // This avoids importing BuildConfig directly which may not be available to the static analyzer
//    private fun getBuildConfigString(fieldName: String): String {
//        return try {
//            val cls = Class.forName("ca.gbc.comp3074.mobileapp_tmwa.BuildConfig")
//            val f = cls.getField(fieldName)
//            (f.get(null) as? String) ?: ""
//        } catch (e: Exception) {
//            ""
//        }
//    }
//
//
//    val supabase = createSupabaseClient(
//        supabaseUrl = getBuildConfigString("SUPABASE_URL"),
//        supabaseKey = getBuildConfigString("SUPABASE_KEY")
//    ){
//        install(Postgrest)
//        install(Auth)
//    }
//    // 1. Request the OTP code to be sent to the user's email
//    suspend fun sendEmailOtp(email: String): AuthResult {
//        return withContext(Dispatchers.IO) {
//            try {
//                // This triggers Supabase to send a 6-digit code to the email
//                supabase.auth.signInWith(OTP) {
//                    this.email = email
//                }
//                AuthResult.Success(null)
//            } catch (e: Exception) {
//                Log.e("SupabaseAuth", "sendOtp", e)
//                AuthResult.Error(e.localizedMessage ?: "Failed to send OTP")
//            }
//        }
//    }
//
//    // 2. Verify the code the user typed in
//    suspend fun verifyEmailOtp(email: String, token: String): AuthResult {
//        return withContext(Dispatchers.IO) {
//            try {
//                // Verify the token (code)
//                supabase.auth.verifyEmailOtp(
//                    type = OtpType.Email.EMAIL,
//                    email = email,
//                    token = token
//                )
//
//                // If successful, the SDK automatically saves the session.
//                // We can retrieve it to return consistent data to your UI.
//                val session = supabase.auth.currentSessionOrNull()
//
//                // Convert session to JSONObject to match your existing AuthResult pattern
//                val jsonResponse = if (session != null) {
//                    JSONObject().apply {
//                        put("access_token", session.accessToken)
//                        put("refresh_token", session.refreshToken)
//                        put("user_id", session.user?.id)
//                    }
//                } else {
//                    null
//                }
//
//                AuthResult.Success(jsonResponse)
//            } catch (e: Exception) {
//                Log.e("SupabaseAuth", "verifyOtp", e)
//                AuthResult.Error(e.localizedMessage ?: "Invalid code")
//            }
//        }
//    }
//
//    private fun authUrl(path: String): String {
//        val base = getBuildConfigString("SUPABASE_URL").trimEnd('/')
//        return if (base.isNotBlank()) "$base/auth/v1/$path" else ""
//    }
//
//
//    private fun extractErrorFromBody(body: String?): String? {
//        if (body.isNullOrBlank()) return null
//        try {
//            val json = JSONObject(body)
//            val keys = listOf("error_description", "msg", "message", "error", "details")
//            for (k in keys) {
//                val v = json.optString(k, null)
//                if (!v.isNullOrBlank()) return v
//            }
//            val errObj = json.optJSONObject("error")
//            if (errObj != null) {
//                val m = errObj.optString("message", null)
//                if (!m.isNullOrBlank()) return m
//            }
//        } catch (_: Exception) {
//            // not JSON, fall back to raw body
//        }
//        return body.trim().takeIf { it.isNotBlank() }
//    }
//
//    private fun mapToFriendlyMessage(httpCode: Int?, raw: String?, fallback: String): String {
//        val extracted = extractErrorFromBody(raw)?.lowercase() ?: raw?.lowercase()
//        when {
//            httpCode == 0 -> return "Network error. Check your connection."
//            httpCode == 401 && (extracted?.contains("invalid") == true || extracted?.contains("credentials") == true || extracted?.contains("grant") == true) ->
//                return "Invalid email or password."
//            httpCode == 403 -> return "Access denied."
//            httpCode == 409 || extracted?.contains("already") == true || extracted?.contains("duplicate") == true ->
//                return "Email already registered."
//            (httpCode == 422) || (extracted?.contains("password") == true && extracted.contains("weak")) ->
//                return "Password does not meet requirements."
//            httpCode != null && httpCode in 500..599 -> return "Server error. Please try again later."
//        }
//
//        extracted?.let {
//            val sanitized = it
//                .replace(Regex("\\s+at\\s+.*\\(.*\\)"), "")
//                .replace(Regex("\\s+\\n.*"), "")
//                .trim()
//            if (sanitized.length <= 200) return sanitized.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
//        }
//        return fallback
//    }
//
//    suspend fun signUp(email: String, password: String): AuthResult {
//        return withContext(Dispatchers.IO) {
//            try {
//                val urlString = authUrl("signup")
//                if (urlString.isBlank()) return@withContext AuthResult.Error("Supabase URL not configured")
//
//                val url = URL(urlString)
//                val conn = (url.openConnection() as HttpURLConnection).apply {
//                    requestMethod = "POST"
//                    doOutput = true
//                    setRequestProperty("Content-Type", "application/json")
//                    setRequestProperty("apikey", getBuildConfigString("SUPABASE_KEY"))
//                }
//
//                val payload = JSONObject().apply {
//                    put("email", email)
//                    put("password", password)
//                }.toString()
//
//                OutputStreamWriter(conn.outputStream).use { it.write(payload) }
//
//                val code = conn.responseCode
//                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
//                val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
//
//                return@withContext if (code in 200..299) {
//                    val json = if (body.isNotBlank()) JSONObject(body) else null
//                    AuthResult.Success(json)
//                } else {
//                    val friendly = mapToFriendlyMessage(code, body, conn.responseMessage ?: "Sign up failed")
//                    AuthResult.Error(friendly)
//                }
//            } catch (e: Exception) {
//                Log.e("SupabaseAuth", "signUp", e)
//                AuthResult.Error(mapToFriendlyMessage(0, e.localizedMessage, "Unknown error"))
//            }
//        }
//    }
//
//    suspend fun signIn(email: String, password: String): AuthResult {
//        return withContext(Dispatchers.IO) {
//            try {
//                val projectUrl = getBuildConfigString("SUPABASE_URL")
//                val anonKey = getBuildConfigString("SUPABASE_KEY")
//                val urlString = "$projectUrl/auth/v1/token?grant_type=password"
//
//                if (urlString.isBlank() || anonKey.isBlank()) {
//                    return@withContext AuthResult.Error("Supabase credentials not configured")
//                }
//
//                val url = URL(urlString)
//                val conn = (url.openConnection() as HttpURLConnection).apply {
//                    requestMethod = "POST"
//                    doOutput = true
//                    setRequestProperty("Content-Type", "application/json") // ✅ Use JSON instead of form-urlencoded
//                    setRequestProperty("apikey", anonKey)
//                    setRequestProperty("Accept", "application/json")
//                }
//
//                val payload = JSONObject().apply {
//                    put("email", email)
//                    put("password", password)
//                }
//
//                OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }
//
//                val code = conn.responseCode
//                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
//                val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
//
//                return@withContext if (code in 200..299) {
//                    val json = JSONObject(body)
//                    AuthResult.Success(json)
//                } else {
//                    val friendly = mapToFriendlyMessage(code, body, conn.responseMessage ?: "Sign in failed")
//                    AuthResult.Error(friendly)
//                }
//            } catch (e: Exception) {
//                Log.e("SupabaseAuth", "signIn", e)
//                AuthResult.Error(mapToFriendlyMessage(0, e.localizedMessage, "Unknown error"))
//            }
//        }
//    }

//    suspend fun signIn(email: String, password: String): AuthResult {
//        return withContext(Dispatchers.IO) {
//            try {
////                val urlString = authUrl("token")
////                if (urlString.isBlank()) return@withContext AuthResult.Error("Supabase URL not configured")
//
//                val projectUrl = getBuildConfigString("SUPABASE_URL")
//                val anonKey = getBuildConfigString("SUPABASE_KEY")
//                val urlString = "$projectUrl/auth/v1/token?grant_type=password"
//
//                val url = URL(urlString)
//                val conn = (url.openConnection() as HttpURLConnection).apply {
//                    requestMethod = "POST"
//                    doOutput = true
//
//                    setRequestProperty("Content-Type", "application/json") // ✅ Use JSON instead of form-urlencoded
//                    setRequestProperty("apikey", anonKey)
//                    setRequestProperty("Accept", "application/json")
//
//                }
//
//                val form = listOf(
////                    "grant_type" to "password",
//                    "email" to email,
//                    "password" to password
//                ).joinToString("&") { (k, v) -> "${URLEncoder.encode(k, "utf-8")}=${URLEncoder.encode(v, "utf-8")}" }
//
//                OutputStreamWriter(conn.outputStream).use { it.write(form) }
//
//                val code = conn.responseCode
//                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
//                val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
//
//                return@withContext if (code in 200..299) {
//                    val json = if (body.isNotBlank()) JSONObject(body) else null
//                    AuthResult.Success(json)
//                } else {
//                    val msg = try {
//                        if (body.isNotBlank()) JSONObject(body).optString("error_description", body) else conn.responseMessage
//                    } catch (e: Exception) {
//                        conn.responseMessage
//                    }
//                    AuthResult.Error("Sign in failed: $msg")
//                }
//            } catch (e: Exception) {
//                Log.e("SupabaseAuth", "signIn", e)
//                AuthResult.Error(e.localizedMessage ?: "Unknown error")
//            }
//        }
//    }
//}
