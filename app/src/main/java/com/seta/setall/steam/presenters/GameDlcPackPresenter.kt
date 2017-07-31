package com.seta.setall.steam.presenters

import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.logW
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.mvpViews.GameDlcPackMvpView
import com.seta.setall.steam.utils.SteamUtilMethods
import rx.Observable
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/7/31.
 */
class GameDlcPackPresenter : BasePresenter<GameDlcPackMvpView>() {

    fun loadGameDlcPack(gameId: Int) {
        Network.steamGameApi.getGameDetail(gameId)
                .map {
                    return@map SteamUtilMethods.createGameDetailBean(it)
                }.doSubscribe(object : Subscriber<GameDetailBean>() {
            override fun onNext(t: GameDetailBean?) {
                logD("LoadGame detail onNext : $t")
                val reqGetDlc: Observable<List<GameDetailBean>>? = getReqGameDetails(t?.dlc?.toList())
            }

            override fun onError(e: Throwable?) {
                logW("LoadGame detail onError : e")
            }

            override fun onCompleted() {
                logD("LoadGame detail onCompleted")
            }

        })
    }

    fun getReqGameDetails(gameIds: List<*>?): Observable<List<GameDetailBean>>? {
        if (gameIds == null) {
            return null
        }
        val ids = gameIds.filterIsInstance<Int>()
        val reqArray = ids.map { Network.steamGameApi.getGameDetail(it) }
        val observable: Observable<List<GameDetailBean>> = Observable.zip(reqArray) {
            val gameDetails = ArrayList<GameDetailBean>()
            it.forEach {
                //                val gson = GsonBuilder().create()
//                val json = gson.toJsonTree(it).asJsonObject
//                val data = json.entrySet().iterator().next().toPair().second.asJsonObject["data"]
//                val gameDetailBean = gson.fromJson<GameDetailBean>(data, GameDetailBean::class.java)
//                gameDetails.add(gameDetailBean)
                gameDetails.add(SteamUtilMethods.createGameDetailBean(it))
            }
            return@zip gameDetails
        }
        return observable
    }
}