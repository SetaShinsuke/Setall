package com.seta.setall.steam.domain.models

import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameBean
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.api.models.PackageDetailBean
import com.seta.setall.steam.utils.SteamUtilMethods
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
                    val type: String? = SteamConstants.TYPE_UNKNOWN,
                    val iconImgUrl: String? = null,
                    val logoImgUrl: String? = null, // GameDetailBean 里的 header_image
                    val games: List<SteamApp>? = null) { //只有 type = BUNDEL_PACK 时不为空
    //从 GameBean 创建
    constructor(gameBean: GameBean) : this(
            gameBean.appid,
            gameBean.name,
            iconImgUrl = gameBean.img_icon_url,
            logoImgUrl = gameBean.img_logo_url,
            games = null)

    //从 PackageDetailBean 创建
    constructor(packBean: PackageDetailBean) : this(
            packBean.id,
            packBean.name,
            currency = packBean.price.currency,
            initPrice = packBean.price.initial,
            type = SteamConstants.TYPE_BUNDLE_PACK,
            games = packBean.apps.map { SteamApp(it.id, it.name) }
    )

    constructor(g: GameDetailBean, coverUrl: String?) : this(
            g.steam_appid,
            g.name,
            g.price_overview?.currency,
            g.price_overview?.initial,
            type = SteamUtilMethods.getTypeByString(g.type),
            iconImgUrl = coverUrl,
            logoImgUrl = g.header_image
    )
}