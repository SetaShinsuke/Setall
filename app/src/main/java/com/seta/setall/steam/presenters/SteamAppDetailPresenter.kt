package com.seta.setall.steam.presenters

import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.logW
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.mvpViews.SteamAppDetailMvpView
import com.seta.setall.steam.utils.SteamUtilMethods
import rx.Observable
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/8/14.
 */
class SteamAppDetailPresenter : BasePresenter<SteamAppDetailMvpView>() {

    fun loadSteamApps(apps: ArrayList<SteamApp>) {
        val packs = apps.filter { it.type == SteamConstants.TYPE_BUNDLE_PACK }
        val allIds = ArrayList<Int>()
        packs.forEach {
            if (it.games != null) {
                allIds.addAll(it.games.map { it.appId })
            }
        }
        val ids = allIds.distinct()

        val reqArray = ids.map { Network.steamGameApi.getGameDetail(it) }
        val observable: Observable<List<GameDetailBean>> = Observable.zip(reqArray) {
            val gameDetails = ArrayList<GameDetailBean>()
            it.forEach {
                SteamUtilMethods.createGameDetailBean(it)?.let {
                    gameDetails.add(it)
                }
            }
            return@zip gameDetails.distinct()
        }
        observable.map {
            gameDetails ->
            //            apps.removeAll(packs)
//            val inflatedGames = ArrayList<SteamApp>()
            packs.forEach {
                pack ->
                pack.games?.forEach {
                    game ->
                    gameDetails.find { it.steam_appid == game.appId }.let {
                        if (it != null) {
                            (pack.games as ArrayList).add(SteamApp(it, null))
//                            pack.games.remove(game)
                        }
                    }
                }
            }
            logD("Packs : $packs")
            return@map apps
        }.doSubscribe(object : Subscriber<ArrayList<SteamApp>>() {
            override fun onError(e: Throwable) {
                logW("On error", e)
                mvpView?.onAppsLoadFail(e)
            }

            override fun onNext(t: ArrayList<SteamApp>) {
                mvpView?.onAppsLoad(t)
            }

            override fun onCompleted() {
                logD("onCompleted")
            }

        })
    }
}