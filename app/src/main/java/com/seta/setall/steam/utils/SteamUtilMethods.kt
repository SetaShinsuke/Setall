package com.seta.setall.steam.utils

import com.google.gson.GsonBuilder
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
        if(data==null){
            return null
        }
        return gson.fromJson<GameDetailBean>(data, GameDetailBean::class.java)
    }

    fun createPackageDetailBean(map: Any): PackageDetailBean {
        val gson = GsonBuilder().create()
        val json = gson.toJsonTree(map).asJsonObject
        val data = json.entrySet().iterator().next().toPair().second.asJsonObject["data"]
        return gson.fromJson<PackageDetailBean>(data, GameDetailBean::class.java)
    }
}