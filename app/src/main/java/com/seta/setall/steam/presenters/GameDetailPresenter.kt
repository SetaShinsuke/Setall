package com.seta.setall.steam.presenters

import com.google.gson.Gson
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.mvpViews.GameDetailMvpView
import com.seta.setall.steam.utils.SteamException
import com.seta.setall.steam.utils.SteamUtilMethods
import rx.Observable
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/7/28.
 */
class GameDetailPresenter : BasePresenter<GameDetailMvpView>() {

    fun loadGameDetails(gameIds: List<*>) {
        val ids = gameIds.filterIsInstance<Int>()
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

        observable.doSubscribe(object : Subscriber<List<GameDetailBean>>() {
            override fun onCompleted() {
                logD("onCompleted")
            }

            override fun onNext(t: List<GameDetailBean>?) {
                logD("onNext : $t")
                if (t == null) {
                    return onError(SteamException(t))
                }
                mvpView?.onGameDetailLoad(t)
            }

            override fun onError(e: Throwable) {
                logD("loadGameDetails onError : ${e.message}")
                mvpView?.onGameDetailLoadFail(e)
            }

        })
    }

    fun loadGameDetail(gameId: Int) {
        Network.steamGameApi.getGameDetail(gameId)
                .map {
                    val gson = Gson()
                    val json = gson.toJsonTree(it).asJsonObject
                    val data = json.entrySet().iterator().next().toPair().second.asJsonObject["data"]
                    val gameDetailBean = gson.fromJson<GameDetailBean>(data, GameDetailBean::class.java)
                    logD("Game name : ${gameDetailBean.name}")
                    return@map gameDetailBean
                }
                .doSubscribe(object : Subscriber<GameDetailBean>() {
                    override fun onCompleted() {
                        logD("onCompleted")
                    }

                    override fun onError(e: Throwable?) {
                        logD("onError : ${e?.message}")
                    }

                    override fun onNext(t: GameDetailBean?) {
                        logD("onNext : $t")
                    }

                })
    }
}