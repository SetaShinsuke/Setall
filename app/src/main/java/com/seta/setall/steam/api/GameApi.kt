package com.seta.setall.steam.api

import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by SETA_WORK on 2017/7/5.
 */
interface SteamGameApi {

    @GET("/api/appdetails")
    fun getGameDetail(@Query("appids") appid: Int): Observable<Any>

    @GET("/api/packagedetails")
    fun getBundlePackDetail(@Query("packageids") bundlePackId: Int): Observable<Any>
}