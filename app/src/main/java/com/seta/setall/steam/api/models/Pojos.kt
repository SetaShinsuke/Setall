package com.seta.setall.steam.api.models

import com.seta.setall.steam.domain.models.SteamApp

/**
 * Created by SETA_WORK on 2017/7/4.
 */

data class AppRestoredBean(val steamApp: SteamApp,
                           val gameDetailBean: GameDetailBean? = null,
                           val packageDetailBean: PackageDetailBean? = null)

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
data class PlayerBean(val players: List<PlayerInfoBean> = ArrayList())

data class OwnedGamePojo(val response: OwnedGameBean)
data class OwnedGameBean(val game_count: Int
                         , val games: List<GameBean> = ArrayList())

data class GameBean(val appid: Int
                    , val name: String
                    , val playtime_forever: Long
                    , val img_icon_url: String
                    , val img_logo_url: String) : Comparable<GameBean> {
    override fun compareTo(other: GameBean): Int {
        val result = name.compareTo(other.name)
        return result
    }

    fun coverUrl() = "http://media.steampowered.com/steamcommunity/public/images/apps/$appid/$img_icon_url.jpg"
    fun logoUrl() = "http://media.steampowered.com/steamcommunity/public/images/apps/$appid/$img_logo_url.jpg"
//    fun coverUrl() = "http://media.steampowered.com/steamcommunity/public/images/appSimples/$appid/$img_icon_url.jpg"
//    fun logoUrl() = "http://media.steampowered.com/steamcommunity/public/images/appSimples/$appid/$img_logo_url.jpg"
}

data class GameDetailPojo(val games: List<GameDetailBean> = ArrayList())
data class GameDetailBean(val steam_appid: Int,
                          val name: String,
                          val type: String,
                          val dlc: List<Int>? = ArrayList(),
                          val packages: List<Int>? = ArrayList(),
                          val package_groups: List<PackageGroupPojo> = ArrayList(),
                          val price_overview: PriceBean?,
                          val required_age: Int,
                          val is_free: Boolean = false,
                          val short_description: String,
                          val fullgame: AppSimpleBean?,
                          val header_image: String,
                          val website: String,
                          val developers: List<String> = ArrayList(),
                          val publishers: List<String> = ArrayList(),
                          val release_date: ReleaseDateBean
) : Comparable<GameDetailBean> {
    override fun compareTo(other: GameDetailBean): Int {
        if (type == other.type) {
            return name.compareTo(other.name)
        }
        val typeAdv = (type == "game" && other.type == "dlc")
        if (typeAdv) {
            return -1
        } else {
            return 1
        }
    }
}

data class PriceBean(val currency: String, val initial: Int, val final: Int)
data class PackageGroupPojo(val name: String, val title: String, val description: String, val subs: List<PackageGroupBean> = ArrayList())
data class PackageGroupBean(val packageid: Int,
                            val percent_saving: Int,
                            val option_text: String,
                            val option_description: String,
                            val price_in_cents_with_discount: Int) {
    val price: Int
        get() = price_in_cents_with_discount * 100
}

data class AppSimpleBean(val id: Int, val name: String)
data class ReleaseDateBean(val date: String)
data class PackageDetailBean(val id: Int, val name: String, val page_image: String, val small_logo: String, val apps: ArrayList<AppSimpleBean>,
                             val price: PriceBean, val release_date: ReleaseDateBean)

data class GameDlcPackBean(val fullGame: GameDetailBean,
                           val dlc: List<GameDetailBean>,
                           val bundlePacks: List<PackageDetailBean>
)


