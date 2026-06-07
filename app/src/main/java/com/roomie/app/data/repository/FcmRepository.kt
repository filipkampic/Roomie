package com.roomie.app.data.repository

import android.util.Base64
import android.util.Log
import com.roomie.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmRepository @Inject constructor() {

    private val projectId = BuildConfig.FCM_PROJECT_ID
    private val clientEmail = BuildConfig.FCM_CLIENT_EMAIL
    private val privateKeyId = BuildConfig.FCM_PRIVATE_KEY_ID

    private fun getPrivateKey(): PrivateKey {
        val privateKeyPem = String(Base64.decode(BuildConfig.FCM_PRIVATE_KEY_BASE64, Base64.DEFAULT))
        val cleaned = privateKeyPem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .trim()

        val keyBytes = Base64.decode(cleaned, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }

    private fun generateOAuthToken(): String {
        val now = System.currentTimeMillis() / 1000
        val expiry = now + 3600

        val header = """{"alg":"RS256","typ":"JWT","kid":"$privateKeyId"}"""
        val payload = """{"iss":"$clientEmail","sub":"$clientEmail","aud":"https://oauth2.googleapis.com/token","iat":$now,"exp":$expiry,"scope":"https://www.googleapis.com/auth/firebase.messaging"}"""

        val headerEncoded = Base64.encodeToString(
            header.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        )
        val payloadEncoded = Base64.encodeToString(
            payload.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        )

        val signingInput = "$headerEncoded.$payloadEncoded"

        val signature = java.security.Signature.getInstance("SHA256withRSA").apply {
            initSign(getPrivateKey())
            update(signingInput.toByteArray(Charsets.UTF_8))
        }.sign()

        val signatureEncoded = Base64.encodeToString(
            signature, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        )

        return "$signingInput.$signatureEncoded"
    }

    private suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        val jst = generateOAuthToken()

        val url = URL("https://oauth2.googleapis.com/token")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        connection.doOutput = true

        val body = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$jst"
        connection.outputStream.write(body.toByteArray())

        val responseCode = connection.responseCode

        if (responseCode != 200) {
            val error = connection.errorStream?.bufferedReader()?.readText()
            throw Exception("OAuth failed: $error")
        }

        val response = connection.inputStream.bufferedReader().readText()
        val json = JSONObject(response)
        json.getString("access_token")
    }

    suspend fun sendNotificationToTokens(
        tokens: List<String>,
        title: String,
        body: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val accessToken = getAccessToken()

            tokens.forEach { token ->
                val url = URL("https://fcm.googleapis.com/v1/projects/$projectId/messages:send")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Bearer $accessToken")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val message = JSONObject().apply {
                    put("message", JSONObject().apply {
                        put("token", token)
                        put("data", JSONObject().apply {
                            put("title", title)
                            put("body", body)
                        })
                    })
                }

                connection.outputStream.write(message.toString().toByteArray())

                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    connection.errorStream?.bufferedReader()?.readText()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
