package com.seta.setall.steam.domain.models

import com.seta.setall.steam.api.models.GameBean
import java.util.*

/**
 * Created by SETA_WORK on 2017/7/18.
 */

data class Transaction(val transId: Int,
                       val date: Date,
                       val buyerId: String,
                       val ownerId: String,
                       val extraMsg: String?,
                       val steamApps: List<SteamApp>)

data class SteamApp(val appId: Int,
                    val name: String,
                    val currency: String? = null,
                    val initPrice: Int? = null,
                    val purchasedPrice: Int? = null,
                    val purchasedDate: Date? = null,
                    val type: Int? = null,
                    val iconImgId: String? = null,
                    val logoImgId: String? = null,
                    val games: List<SteamApp>? = null) {
    constructor(gameBean: GameBean) : this(gameBean.appid, gameBean.name, iconImgId = gameBean.img_icon_url, logoImgId = gameBean.img_logo_url, games = null)
}