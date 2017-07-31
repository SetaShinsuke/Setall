package com.seta.setall.steam.presenters

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.mvpViews.GameDlcPackMvpView
import rx.Observable
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/7/31.
 */
class GameDlcPackPresenter : BasePresenter<GameDlcPackMvpView>() {

    fun loadGameDlcPack(gameId: Int) {
        Network.steamGameApi.getGameDetail(gameId)
                .map {
                    val gson = Gson()
                    val json = gson.toJsonTree(it).asJsonObject
                    val data = json.entrySet().iterator().next().toPair().second.asJsonObject["data"]
                    val gameDetailBean = gson.fromJson<GameDetailBean>(data, GameDetailBean::class.java)
                    logD("Game name : ${gameDetailBean.name}")
                    return@map gameDetailBean
                }.doSubscribe(object : Subscriber<GameDetailBean>() {
            override fun onCompleted() {
                logD("ReqGetGame onCompleted")
            }

            override fun onError(e: Throwable?) {
                logD("ReqGetGame onError : ${e?.message}")
            }

            override fun onNext(t: GameDetailBean?) {
                logD("ReqGetGame onNext : $t")
            }
        })
    }

    fun loadDlcPack(gameDetailBean: GameDetailBean) {
        val ids = gameDetailBean.dlc
        val reqArray = ids?.map { Network.steamGameApi.getGameDetail(it) }
        Observable.zip(reqArray) {
            val gameDetails = ArrayList<GameDetailBean>()
            it.forEach {
                val gson = GsonBuilder().create()
                val json = gson.toJsonTree(it).asJsonObject
                val data = json.entrySet().iterator().next().toPair().second.asJsonObject["data"]
                val gameDetailBean = gson.fromJson<GameDetailBean>(data, GameDetailBean::class.java)
                gameDetails.add(gameDetailBean)
            }
            return@zip gameDetails
        }.doSubscribe(object : Subscriber<ArrayList<GameDetailBean>>() {
            override fun onError(e: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCompleted() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onNext(t: ArrayList<GameDetailBean>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}