package com.android.multimedia.api

import com.android.multimedia.Albom
import com.android.multimedia.BuildConfig.BASE_URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.suspendCoroutine


class APIService {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()


    suspend inline fun <Albom> OkHttpClient.makeRequest(url: String, gson: Gson = Gson()): Albom =
        suspendCoroutine { continuation ->
            Request.Builder()
                .url("${url}sng1.json")
                .build()
                .let {
                    newCall(it)
                }.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resumeWith(Result.failure(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.string()?.let {
                            gson.fromJson<Albom>(
                                it,
                                TypeToken.getParameterized(com.android.multimedia.Albom::class.java).type
                            )
                        }?.also {
                            continuation.resumeWith(Result.success(it))
                        } ?: continuation.resumeWith(Result.failure(IOException("body bad")))
                    }
                })
        }


    fun getAlbom(): Albom {
        return runBlocking {
            return@runBlocking client.makeRequest(BASE_URL)
        }

    }

}