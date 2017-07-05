package com.seta.setall.steam.api

import com.seta.setall.steam.api.models.OwnedGamePojo
import com.seta.setall.steam.api.models.SteamLoginPojo
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by SETA_WORK on 2017/7/3.
 */
interface SteamUserApi {

//    @GET("/ISteamUser/ResolveVanityURL/v0001") //?key=XXXXXXXXXXXXXXXXXXXXXXX&vanityurl=userVanityUrlName")

    @GET("/ISteamUser/ResolveVanityURL/v0001")
    fun getIdByVanityUrl(@Query("key") key: String, @Query("vanityurl") vanityUrlName: String): Observable<SteamLoginPojo>

    @GET("/IPlayerService/GetOwnedGames/v0001")
    fun getOwnedGames(@Query("key") key: String, @Query("steamid") steamId: String?): Observable<OwnedGamePojo>
}