package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.extensions.setVisible
import com.seta.setall.common.extensions.toast
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.extensions.loadImg
import com.seta.setall.steam.mvpViews.GameDetailMvpView
import com.seta.setall.steam.presenters.GameDetailPresenter
import kotlinx.android.synthetic.main.activity_game_list.*
import kotlinx.android.synthetic.main.item_game_detail.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import kotlin.properties.Delegates

class GameListActivity : AppCompatActivity(), GameDetailMvpView {

    val gameDetailPresenter: GameDetailPresenter = GameDetailPresenter()
    var adapter by Delegates.notNull<BasicAdapter<GameDetailBean>>()
    val selectedIds = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)
        gameDetailPresenter.attachView(this)
        mRvGames.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = BasicAdapter(R.layout.item_game_detail) {
            view, position, data ->
            with(view) {
                dlcBadge.setVisible(data.type == "dlc")
                mTvGameName.text = data.name
                mIvHeader.loadImg(data.header_image)
                mIvHeaderCheck.setVisible(selectedIds.contains(data.steam_appid))
                onClick {
                    if (selectedIds.contains(data.steam_appid)) {
                        selectedIds.remove(data.steam_appid)
                    } else {
                        selectedIds.add(data.steam_appid)
                    }
                    adapter.notifyItemChanged(data)
                }
            }
        }
        mRvGames.adapter = adapter

        val fullGameIds: List<Int> = intent.getIntegerArrayListExtra(SteamConstants.GAME_IDS).toList()
        gameDetailPresenter.loadGameDetails(fullGameIds)
    }

    override fun onGameDetailLoad(gameDetails: List<GameDetailBean>) {
        adapter.refreshData(gameDetails)
    }

    override fun onGameDetailLoadFail(t: Throwable) {
        toast(R.string.games_load_fail)
    }
}
