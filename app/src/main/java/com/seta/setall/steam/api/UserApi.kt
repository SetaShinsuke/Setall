package com.seta.setall.steam.api

import com.seta.setall.steam.api.models.OwnedGamePojo
import com.seta.setall.steam.api.models.PlayerPojo
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
    fun getIdByVanityUrl(@Query("vanityurl") vanityUrlName: String
                         , @Query("key") key: String = SteamConstants.STEAM_API_KEY): Observable<SteamLoginPojo>

    @GET("/ISteamUser/GetPlayerSummaries/v0002")
    fun getPlayerSummaries(@Query("steamids") steamId: String, @Query("key") key: String = SteamConstants.STEAM_API_KEY): Observable<PlayerPojo>

    /**
     *  withInfo 是否拉取详情信息
     *  include_played_free_games 是否包含免费游戏
     */
    @GET("/IPlayerService/GetOwnedGames/v0001?include_appinfo=1&include_played_free_games=1")
    fun getOwnedGames(@Query("steamid") steamId: String?, @Query("key") key: String = SteamConstants.STEAM_API_KEY): Observable<OwnedGamePojo>
}