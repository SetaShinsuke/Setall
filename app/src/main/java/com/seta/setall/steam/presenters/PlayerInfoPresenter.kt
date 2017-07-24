package com.seta.setall.steam.presenters

import com.seta.setall.common.extensions.logD
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.PlayerBean
import com.seta.setall.steam.mvpViews.PlayerInfoMvpView
import com.seta.setall.steam.utils.SteamException
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/7/24.
 */
class PlayerInfoPresenter : BasePresenter<PlayerInfoMvpView>() {

    fun loadPlayerInfo(playerId: String) {
        Network.steamUserApi.getPlayerSummaries(playerId)
                .map { it.response }
                .doSubscribe(object : Subscriber<PlayerBean>() {
                    override fun onError(e: Throwable) {
                        mvpView?.onPlayerInfoLoadFail(e)
                    }

                    override fun onCompleted() {
                        logD("onCompleted")
                    }

                    override fun onNext(t: PlayerBean) {
                        logD("onNext : player info get, player count : ${t.players.size}")
                        if (t.players.isNotEmpty()) {
                            mvpView?.onPlayerInfoLoad(t.players[0])
                        } else {
                            mvpView?.onPlayerInfoLoadFail(SteamException("Player info null."))
                        }
                    }

                })
    }
}