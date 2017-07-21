package com.seta.setall.steam.api.models

/**
 * Created by SETA_WORK on 2017/7/4.
 */

data class SteamLoginPojo(val response: SteamLoginBean)

//success = 1 表示成功
data class SteamLoginBean(val steamid: String, val success: Int, val message: String)

/**
 * @param profilestate: 资料公开状态
 * @param personastate: 在线状态 0 - Offline, 1 - Online, 2 - Busy, 3 - Away, 4 - Snooze, 5 - looking to trade, 6 - looking to play
 */
data class PlayerInfoBean(val steamid: String,
                          val profilestate: Int,
                          val communityvisibilitystate: Int,
                          val personaname: String,
                          val profileurl: String,
                          val avatarfull: String,
                          val personastate: Int,
                          val timecreated: Long = 0)

data class PlayerPojo(val response: PlayerBean)
data class PlayerBean(val players: List<PlayerInfoBean>)

data class OwnedGamePojo(val response: OwnedGameBean)
data class OwnedGameBean(val game_count: Int
                         , val games: List<GameBean>)

data class GameBean(val appid: Int
                    , val name: String
                    , val playtime_forever: Long
                    , val img_icon_url: String
                    , val img_logo_url: String) : Comparable<GameBean> {
    override fun compareTo(other: GameBean): Int {
        val result = name.compareTo(other.name)
        return result
    }

}

data class GameDetailPojo(val games: List<GameDetailBean>)
data class GameDetailBean(val steam_appid: Long, val name: String, val type: String)


