package com.angleone.bmical.utils

import com.angleone.bmical.config.CONFIG_FILE_URL
import com.angleone.bmical.config.HOST_URL
import com.angleone.bmical.config.TARGET_COUNTRY
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object AppInfoUtils {

    fun readAppInfo() {
        val result: String? = getResponseFromUrl(CONFIG_FILE_URL)
        if (!result.isNullOrEmpty()) {
            try {
                JSONObject(result).let {
                    val host = it.getString("privacyPolicy")
                    HOST_URL = host
                    val targetCountry = it.getString("target")
                    TARGET_COUNTRY = targetCountry
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun readLocInfo(): String? {
        var result: String? = getResponseFromUrl("https://ipinfo.io/json")
        if (result.isNullOrEmpty() || !result.contains("country")) {
            //如果这个接口访问失败或者没获取到，用另一个接口访问
            result = getResponseFromUrl("https://www.cloudflare.com/cdn-cgi/trace")
        }
        return result
    }

    private fun getResponseFromUrl(urlString: String): String? {
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        val input = connection.inputStream
        val reader = BufferedReader(InputStreamReader(input))
        val response = StringBuilder()
        reader.use {
            reader.forEachLine {
                response.append(it)
            }
        }
        val result = response.toString()
        print(result)
        return result
    }
}