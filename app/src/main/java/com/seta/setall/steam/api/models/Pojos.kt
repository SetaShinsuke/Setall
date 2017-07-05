package com.seta.setall.steam.api.models

/**
 * Created by SETA_WORK on 2017/7/4.
 */

data class SteamLoginPojo(val response: SteamLoginBean)

//success = 1 表示成功
data class SteamLoginBean(val steamid: String, val success: Int, val message: String)

data class OwnedGamePojo(val response: OwnedGameBean)
data class OwnedGameBean(val game_count: Int, val games: List<GameBean>)
data class GameBean(val appid: Int, val playtime_forever: Long)
data class GameDetailPojo(val games: List<GameDetailBean>)
data class GameDetailBean(val steam_appid: Long, val name: String, val type: String)


