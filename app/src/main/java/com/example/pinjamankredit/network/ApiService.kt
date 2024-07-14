package com.example.pinjamankredit.network

import com.example.pinjamankredit.api.Api
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val baseUrl = "http://192.30.35.208:8080/api/"

object ApiService {
    const val imageURL = "http://192.30.35.208:8080/uploads/"
    const val baseURL = "http://192.30.35.208:8080/"

    fun getClient(): Api {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .build()
                chain.proceed(request)
            }
            .build()

        val gson = GsonBuilder().serializeNulls().create()

        return Retrofit.Builder()
            .baseUrl( baseUrl )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(Api::class.java)
    }


}