package com.seta.setall.steam.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.api.models.PackageDetailBean

/**
 * Created by SETA_WORK on 2017/7/31.
 */
object SteamUtilMethods {
    fun createGameDetailBean(map: Any): GameDetailBean? {
        val gson = GsonBuilder().create()
        val json = gson.toJsonTree(map).asJsonObject
        val data = json.entrySet().iterator().next().toPair().second.asJsonObject["data"]
        data ?: return null
        return gson.fromJson<GameDetailBean>(data, GameDetailBean::class.java)
    }

    fun createPackageDetailBean(map: Any): PackageDetailBean {
        val gson = GsonBuilder().create()
        val json = gson.toJsonTree(map).asJsonObject
        val pair = json.entrySet().iterator().next().toPair()
        val data = pair.second.asJsonObject["data"]
        data.asJsonObject.add("id", JsonParser().parse(pair.first))
        val packageDetailBean = gson.fromJson<PackageDetailBean>(data, PackageDetailBean::class.java)
//        packageDetailBean.id = pair.first.toInt()
        return packageDetailBean
    }

    fun getTypeByString(type: String) = when (type) {
        "dlc" -> SteamConstants.TYPE_DLC
        "game" -> SteamConstants.TYPE_GAME
        else -> SteamConstants.TYPE_UNKNOWN
    }
}