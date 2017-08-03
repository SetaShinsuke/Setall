package com.seta.setall.steam.presenters

import com.seta.setall.common.extensions.logD
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.mvpViews.OwnedGamesView
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/7/6.
 */
class OwnedGamesPresenter : BasePresenter<OwnedGamesView>() {

    fun loadOwnedGames(userid: String?) {
        Network.steamUserApi.getOwnedGames(userid)
                .map { it.response }
                .doSubscribe(object : Subscriber<OwnedGameBean>() {
                    override fun onCompleted() {
                        logD("onComplete.")
                    }

                    override fun onError(e: Throwable) {
                        mvpView?.onGamesLoadFail(e)
                    }

                    override fun onNext(t: OwnedGameBean) {
                        logD("loadOwnedGames onNext : $t")
                        mvpView?.onGamesLoad(t)
                    }
                })

    }
}