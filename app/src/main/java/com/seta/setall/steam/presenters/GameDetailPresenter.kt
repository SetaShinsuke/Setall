package com.seta.setall.steam.presenters

import com.google.gson.Gson
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.GameDetailPojo
import com.seta.setall.steam.mvpViews.GameDetailMvpView
import rx.Observable
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/7/28.
 */
class GameDetailPresenter : BasePresenter<GameDetailMvpView>() {

    fun loadGameDetails(gameIds: List<*>) {
        val reqArray = ArrayList<Observable<*>>()
        val ids = gameIds.filterIsInstance<Int>()
        ids.forEach { reqArray.add(Network.steamGameApi.getGameDetail(it)) }
        val observable: Observable<Any> = Observable.zip(reqArray) {
            args: Array<out Any>? ->
            {
                args?.forEachIndexed { index, any ->
                    logD("$index : $any")
                }
            }
        }
        observable.doSubscribe(object : Subscriber<Any>() {
            override fun onCompleted() {
                logD("onCompleted")
            }

            override fun onNext(t: Any?) {
                logD("onNext : $t")
            }

            override fun onError(e: Throwable?) {
                logD("onError : ${e?.message}")
            }

        })
    }

    fun loadGameDetail(gameId: Int) {
        Network.steamGameApi.getGameDetail(gameId)
                .doSubscribe(object : Subscriber<Any>() {
                    override fun onCompleted() {
                        logD("onCompleted")
                    }

                    override fun onError(e: Throwable?) {
                        logD("onError : ${e?.message}")
                    }

                    override fun onNext(t: Any?) {
                        logD("onNext : $t")
                        val gson = Gson()
                        val json = gson.toJsonTree(t).asJsonObject
                        val key = json.entrySet().iterator().next().toPair().first
                        val gameDetailPojo = gson.fromJson<GameDetailPojo>(json, GameDetailPojo::class.java)
                    }

                })
    }
}