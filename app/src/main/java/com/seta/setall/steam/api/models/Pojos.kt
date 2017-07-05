package com.seta.setall.steam.api.models

/**
 * Created by SETA_WORK on 2017/7/4.
 */

data class SteamLoginPojo(val response: SteamLoginBean)

//success = 1 表示成功
data class SteamLoginBean(val steamid: String, val success: Int, val message: String)

data class SteamGamePojo(val games: List<SteamGameBean>)
data class SteamGameBean(val steam_appid: Long, val name: String, val type: String)


