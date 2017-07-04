package com.seta.setall.steam.models

/**
 * Created by SETA_WORK on 2017/7/4.
 */
data class SteamLoginPojo(val response: SteamLoginBean)

//success = 1 表示成功
data class SteamLoginBean(val steamid: String, val success: Int, val message: String)
