package com.seta.setall.common.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.http.Network
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.extensions.DelegateSteam
import kotlinx.android.synthetic.main.activity_steam_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SteamMainActivity : AppCompatActivity() {

    var userId: String? by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steam_main)
        if (userId == "") {
            logD("userId : $userId")
            startActivity<SteamLoginActivity>()
            finish()
        }
        mTvUserInfo.text = "User id : $userId"
        Network.steamUserApi.getOwnedGames(SteamConstants.STEAM_API_KEY, userId)
                .map { it.response }
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .subscribe(object : Subscriber<OwnedGameBean>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: OwnedGameBean) {
                        var content: String = "All games : "
                        t.games.forEach { content += "\n${it.appid}" }
                        mTvUserInfo.text = content
                    }

                    override fun onError(e: Throwable) {
                    }

                })

        Network.steamGameApi.getGameDetail(listOf(105600, 218620))
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .subscribe(object : Subscriber<Any>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: Any) {
                        toast("onNext.")
                    }

                    override fun onError(e: Throwable) {
                        toast("onError.")
                    }

                })
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnLogout -> {
                userId = null
                finish()
                toast("已注销！")
                startActivity<SteamLoginActivity>()
            }
        }
    }
}
