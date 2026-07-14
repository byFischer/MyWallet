package com.example.mywallet.data.remote

import android.net.Uri
import com.example.mywallet.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class BrandSearchResult(
    val brandId: String,
    val name: String,
    val domain: String,
    val iconUrl: String?
)

class BrandfetchClient(
    private val clientId: String = BuildConfig.BRANDFETCH_CLIENT_ID
) {
    val isConfigured: Boolean
        get() = clientId.isNotBlank()

    suspend fun search(query: String): List<BrandSearchResult> = withContext(Dispatchers.IO) {
        if (!isConfigured || query.isBlank()) return@withContext emptyList()

        val encodedQuery = Uri.encode(query.trim())
        val encodedClientId = Uri.encode(clientId)
        val connection = URL(
            "https://api.brandfetch.io/v2/search/$encodedQuery?c=$encodedClientId"
        ).openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.setRequestProperty("Accept", "application/json")

            if (connection.responseCode !in 200..299) {
                throw IOException("Brandfetch request failed: ${connection.responseCode}")
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }
            val json = JSONArray(response)

            buildList {
                for (index in 0 until json.length()) {
                    val item = json.optJSONObject(index) ?: continue
                    if (item.isNull("name") || item.isNull("domain")) continue

                    val name = item.optString("name").trim()
                    val domain = item.optString("domain").trim()
                    if (name.isBlank() || domain.isBlank()) continue

                    add(
                        BrandSearchResult(
                            brandId = item.optString("brandId"),
                            name = name,
                            domain = domain,
                            iconUrl = item.optString("icon")
                                .takeIf { it.isNotBlank() && it != "null" }
                        )
                    )
                }
            }
        } finally {
            connection.disconnect()
        }
    }
}

fun brandfetchLogoUrl(domain: String?): String? {
    val clientId = BuildConfig.BRANDFETCH_CLIENT_ID
    val cleanDomain = domain?.trim()?.takeIf { it.isNotBlank() }
    if (clientId.isBlank() || cleanDomain == null) return null

    return "https://cdn.brandfetch.io/domain/${Uri.encode(cleanDomain)}" +
        "/w/512/h/512/fallback/404/type/icon?c=${Uri.encode(clientId)}"
}
