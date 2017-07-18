package com.seta.setall.common.http

import android.util.Log
import com.seta.setall.common.utils.Constants
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.SteamGameApi
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

    private val gsonConverterFactory = GsonConverterFactory.create()!!
    private val rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create()!!

    private val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    private val client: OkHttpClient by lazy {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Log.d(Constants.LOG_TAG_NETWORT, it) })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder
                .addInterceptor(interceptor)
                .build()
    }

    private val steamRetrofit: Retrofit by lazy {
        Retrofit.Builder()
                .client(client)
                .baseUrl(SteamConstants.STEAM_API_HOST)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build()
    }
    private val steamStoreRetrofit: Retrofit by lazy {
        Retrofit.Builder()
                .client(client)
                .baseUrl(SteamConstants.STEAM_STORE_API_HOST)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build()
    }
    val steamUserApi: SteamUserApi by lazy { steamRetrofit.create(SteamUserApi::class.java) }
    val steamGameApi: SteamGameApi by lazy { steamStoreRetrofit.create(SteamGameApi::class.java) }
}