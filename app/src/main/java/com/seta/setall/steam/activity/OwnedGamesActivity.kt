package com.seta.setall.steam.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.seta.setall.R
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.mvpViews.OwnedGamesView
import com.seta.setall.steam.presenters.OwnedGamesPresenter

class OwnedGamesActivity : AppCompatActivity(), OwnedGamesView {
    val ownedGamesPresenter: OwnedGamesPresenter = OwnedGamesPresenter()
    var userId: String by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owned_games)
        ownedGamesPresenter.attachView(this)
        ownedGamesPresenter.loadOwnedGames(userId)
    }

    override fun onGamesLoad(ownedGameBean: OwnedGameBean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGamesLoadFail(t: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
