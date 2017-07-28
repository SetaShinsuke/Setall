package com.seta.setall.steam.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameBean
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.extensions.loadImg
import com.seta.setall.steam.mvpViews.OwnedGamesView
import com.seta.setall.steam.presenters.OwnedGamesPresenter
import kotlinx.android.synthetic.main.activity_owned_games.*
import kotlinx.android.synthetic.main.item_owned_games.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class OwnedGamesActivity : BaseActivity(), OwnedGamesView {
    val ownedGamesPresenter: OwnedGamesPresenter = OwnedGamesPresenter()
    var userId: String by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    var adapter by Delegates.notNull<BasicAdapter<GameBean>>()
    val selectedIds = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owned_games)
        setSwipeBackEnable(false)
        mRvOwnedGames.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        ownedGamesPresenter.attachView(this)
        ownedGamesPresenter.loadOwnedGames(userId)
        selectedIds.addAll(intent.getIntegerArrayListExtra(SteamConstants.SELECTED_IDS))
        logD("Selected Ids : $selectedIds")
        adapter = BasicAdapter(R.layout.item_owned_games) { view, position, data ->
            with(view) {
                mTvGameName.text = data.name
                logD("Cover url : ${data.coverUrl()}")
                if (selectedIds.contains(data.appid)) {
                    mIvCover.loadImg(R.mipmap.ic_check)
                    view.isSelected = true
                } else {
                    mIvCover.loadImg(data.coverUrl())
//                    mTvGameName.isSelected = false
                    view.isSelected = false
                }
                onClick {
                    if (selectedIds.contains(data.appid)) {
                        selectedIds.remove(data.appid)
                    } else {
                        selectedIds.add(data.appid)
                    }
                    adapter.notifyItemChanged(position)
                }
            }
        }
        mRvOwnedGames.adapter = adapter
    }

    override fun onBackPressed() {
        if (selectedIds.isNotEmpty()) {
            alert {
                titleResource = R.string.hint
                message = String.format(getString(R.string.cancel_game_select_hint), selectedIds.size)
                positiveButton(R.string.confirm) { dialog ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
                negativeButton(R.string.cancel) { dialog -> dialog.dismiss() }
                show()
            }
            return
        }
        super.onBackPressed()
    }

    override fun onGamesLoad(ownedGameBean: OwnedGameBean) {
        adapter.refreshData(ownedGameBean.games)
    }

    override fun onGamesLoadFail(t: Throwable) {
        toast(R.string.games_load_fail)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.select_games_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            R.id.menu_commit -> {
                val intent = Intent()
                intent.putIntegerArrayListExtra(SteamConstants.SELECTED_IDS, selectedIds)
                setResult(SteamConstants.CODE_SELECT_GAMES, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
