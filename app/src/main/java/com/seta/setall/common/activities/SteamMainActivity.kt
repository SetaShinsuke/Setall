package com.seta.setall.common.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameBean
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.mvpViews.OwnedGamesView
import com.seta.setall.steam.presenters.OwnedGamesPresenter
import kotlinx.android.synthetic.main.activity_steam_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SteamMainActivity : AppCompatActivity(), OwnedGamesView {
    var userId: String? by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    val ownedGamePresenter: OwnedGamesPresenter = OwnedGamesPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steam_main)
        if (userId == "") {
            logD("userId : $userId")
            startActivity<SteamLoginActivity>()
            finish()
        }
        mTvUserInfo.text = "User id : $userId"
        ownedGamePresenter.attachView(this)
        ownedGamePresenter.loadOwnedGames(userId)
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

    override fun onGamesLoad(ownedGameBean: OwnedGameBean) {
        val games: List<GameBean> = ownedGameBean.games.sortedWith(compareBy({ it.name }, { it.appid }))
        var content: String = "Total : ${ownedGameBean.game_count}\nAll steamApps : "
        games.forEachIndexed { index, gameBean -> content += "\n$index ${gameBean.name}" }
        mTvUserInfo.text = content
    }

    override fun onGamesLoadFail(t: Throwable) {
        mTvUserInfo.text = ""
    }
}
