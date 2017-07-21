package com.seta.setall.steam.domain.models

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
                    val currency: String,
                    val initPrice: Int,
                    val purchasedPrice: Int,
                    val purchasedDate: Date,
                    val type: Int,
                    val games: List<SteamApp>?)