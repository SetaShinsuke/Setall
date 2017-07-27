package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameBean
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.mvpViews.OwnedGamesView
import com.seta.setall.steam.presenters.OwnedGamesPresenter
import kotlinx.android.synthetic.main.activity_owned_games.*
import kotlinx.android.synthetic.main.item_owned_games.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class OwnedGamesActivity : BaseActivity(), OwnedGamesView {
    val ownedGamesPresenter: OwnedGamesPresenter = OwnedGamesPresenter()
    var userId: String by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    var adapter by Delegates.notNull<BasicAdapter<GameBean>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owned_games)
        mRvOwnedGames.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        ownedGamesPresenter.attachView(this)
        ownedGamesPresenter.loadOwnedGames(userId)
    }

    override fun onGamesLoad(ownedGameBean: OwnedGameBean) {
        adapter = BasicAdapter(R.layout.item_owned_games, ownedGameBean.games) { view, data ->
            view.mTvGameName.text = data.name
//            view.mainView.onClick {
//                toast("点击${data.name}")
//            }
            view.onClick {
                toast("点击${data.name}")
            }
        }
        mRvOwnedGames.adapter = adapter
    }

    override fun onGamesLoadFail(t: Throwable) {
        toast(R.string.games_load_fail)
    }
}
