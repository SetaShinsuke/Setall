package com.seta.setall.common.http

import com.seta.setall.steam.api.SteamServer
import com.seta.setall.steam.api.SteamUserApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by SETA_WORK on 2017/7/3.
 */
object Network {

    val gsonConverterFactory = GsonConverterFactory.create()!!
    val rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create()!!

    val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    val client: OkHttpClient by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        clientBuilder
                .addInterceptor(interceptor)
                .build()
    }
    val steamRetrofit: Retrofit by lazy {
        Retrofit.Builder()
                .client(client)
                .baseUrl(SteamServer.STEAM_API_HOST)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build()
    }
    val steamUserApi: SteamUserApi by lazy { steamRetrofit.create(SteamUserApi::class.java) }
}