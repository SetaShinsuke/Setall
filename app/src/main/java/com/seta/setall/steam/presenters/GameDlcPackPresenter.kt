package com.seta.setall.steam.presenters

import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.logW
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.api.models.GameDlcPackBean
import com.seta.setall.steam.api.models.PackageDetailBean
import com.seta.setall.steam.mvpViews.GameDlcPackMvpView
import com.seta.setall.steam.utils.SteamUtilMethods
import rx.Observable
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/7/31.
 */
class GameDlcPackPresenter : BasePresenter<GameDlcPackMvpView>() {

    fun loadGames(gameIds: List<*>) {
        val ids = gameIds.filterIsInstance<Int>()
//        val reqs: ArrayList<Observable<GameDlcPackBean>> = ArrayList()
//        ids.forEach {
//            val o: Observable<GameDetailBean> = Network.steamGameApi.getGameDetail(it)
//                    .map {
//                        return@map SteamUtilMethods.createGameDetailBean(it)
//                    }.doSubscribe(object : Subscriber<GameDetailBean>() {
//                override fun onNext(t: GameDetailBean) {
//                    logD("LoadGame detail onNext : $t")
//                    val reqGetDlc: Observable<List<GameDetailBean>>? = getReqGameDetails(t.dlc?.toList()) //获取DLC的Request
//                    val reqGetPack: Observable<List<PackageDetailBean>>? = getReqPackageDetails(t.packages?.toList())
//                    val observable: Observable<GameDlcPackBean> = Observable.zip(reqGetDlc, reqGetPack) {
//                        dlc, packs ->
//                        GameDlcPackBean(t, dlc, packs)
//                    }
//                    reqs.add(observable)
//                }
//
//                override fun onError(e: Throwable) {
//                    logW("LoadGame detail onError : e")
//                    mvpView?.onGameLoadFail(e)
//                }
//
//                override fun onCompleted() {
//                    logD("LoadGame detail onCompleted")
//                }
//
//            })
//        }
//        val o: Observable<List<Any>> = Observable.zip(reqs) {
//            array: Array<Any> ->
//            val list = array.toList()
////                    .filterIsInstance<GameDlcPackBean>()
//            return@zip list
//        }
//        o.doSubscribe(object : Subscriber<List<Any>>() {
//            override fun onError(e: Throwable?) {
//                logW("loadGames final error : ${e?.message}")
//            }
//
//            override fun onNext(t: List<Any>?) {
//                logD("loadGames final onNext : $t")
//            }
//
//            override fun onCompleted() {
//                logD("loadGames final onCompleted.")
//            }
//
//        })

//        val reqs:ArrayList<GameDlcPackBean> = ArrayList()
//        ids.forEach {
//            val req:Observable<GameDlcPackBean> = Observable.zip(getReqGameDetails(it))
//        }
    }

    /**
     * 拉取单个游戏的详情、DLC、Bundle
     */
    fun loadGameDlcPack(gameId: Int) {
        Network.steamGameApi.getGameDetail(gameId)
                .map {
                    return@map SteamUtilMethods.createGameDetailBean(it)
                }.doSubscribe(object : Subscriber<GameDetailBean?>() {
            override fun onNext(t: GameDetailBean?) {
                logD("LoadGame detail onNext : $t")
                val reqGetDlc: Observable<List<GameDetailBean>>? = getReqGameDetails(t?.dlc?.toList()) //获取DLC的Request
                val reqGetPack: Observable<List<PackageDetailBean>>? = getReqPackageDetails(t?.packages?.toList())
                Observable.zip(reqGetDlc, reqGetPack) {
                    dlc, packs ->
                    t?.let{
                        GameDlcPackBean(t, dlc, packs)
                    }
                }.doSubscribe(object : Subscriber<GameDlcPackBean?>() {
                    override fun onCompleted() {
                        logD("loadGameDlcPack onCompleted")
                    }

                    override fun onNext(t: GameDlcPackBean?) {
                        logD("loadGameDlcPack onNext : $t")
                        t?.let {
                            mvpView?.onGameLoad(t)
                        }
                    }

                    override fun onError(e: Throwable) {
                        logW("loadGameDlcPack onError : ${e?.message}")
                        mvpView?.onGameLoadFail(e)
                    }

                })
            }

            override fun onError(e: Throwable) {
                logW("LoadGame detail onError : e")
                mvpView?.onGameLoadFail(e)
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
                SteamUtilMethods.createGameDetailBean(it)?.let {
                    gameDetails.add(it)
                }
            }
            return@zip gameDetails
        }
        return observable
    }

    fun getReqPackageDetails(packIds: List<*>?): Observable<List<PackageDetailBean>>? {
        if (packIds == null) {
            return null
        }
        val ids = packIds.filterIsInstance<Int>()
        val reqArray = ids.map { Network.steamGameApi.getBundlePackDetail(it) }
        val observalbe: Observable<List<PackageDetailBean>> = Observable.zip(reqArray) {
            val packDetails = ArrayList<PackageDetailBean>()
            it.forEach { packDetails.add(SteamUtilMethods.createPackageDetailBean(it)) }
            return@zip packDetails
        }
        return observalbe
    }

}