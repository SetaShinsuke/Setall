package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.toast
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameBean
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.domain.TransManager
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.events.CreateStartEvent
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.extensions.loadImg
import com.seta.setall.steam.mvpViews.GameDetailMvpView
import com.seta.setall.steam.mvpViews.OwnedGamesView
import com.seta.setall.steam.presenters.GameDetailPresenter
import com.seta.setall.steam.presenters.OwnedGamesPresenter
import kotlinx.android.synthetic.main.activity_owned_games.*
import kotlinx.android.synthetic.main.item_owned_games.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class OwnedGamesActivity : BaseActivity(), OwnedGamesView, GameDetailMvpView {

    var userId: String by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    var adapter by Delegates.notNull<BasicAdapter<GameBean>>()
    //    val selectedIds = ArrayList<Int>()
    val selectedGameBeans = ArrayList<GameBean>()

    val ownedGamesPresenter: OwnedGamesPresenter = OwnedGamesPresenter()
    val gameDetailPresenter: GameDetailPresenter = GameDetailPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owned_games)
        setSwipeBackEnable(false)
        mRvOwnedGames.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        ownedGamesPresenter.attachView(this)
        gameDetailPresenter.attachView(this)
        ownedGamesPresenter.loadOwnedGames(userId)
        adapter = BasicAdapter(R.layout.item_owned_games) { view, position, data ->
            with(view) {
                mTvGameName.text = data.name
                logD("Cover url : ${data.coverUrl()}")
                if (selectedGameBeans.contains(data)) {
                    mIvHeader.loadImg(R.mipmap.ic_check)
                    view.isSelected = true
                } else {
                    mIvHeader.loadImg(data.coverUrl())
//                    mTvGameName.isSelected = false
                    view.isSelected = false
                }
                onClick {
                    if (selectedGameBeans.contains(data)) {
                        selectedGameBeans.remove(data)
                    } else {
                        selectedGameBeans.add(data)
                    }
                    adapter.notifyItemChanged(position)
                }
            }
        }
        mRvOwnedGames.adapter = adapter
        TransManager.steamApps.clear()
        registerBus()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: CreateStartEvent) {
        finish()
    }

    override fun onGamesLoad(ownedGameBean: OwnedGameBean) {
        adapter.refreshData(ownedGameBean.games.sorted())
    }

    override fun onGamesLoadFail(t: Throwable) {
        toast(R.string.games_load_fail)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.go_next, menu)
        return true
    }

    override fun onGameDetailLoad(gameDetails: List<GameDetailBean>) {
        loadingDialog?.dismiss()
        val packIds = ArrayList<Int>() //包含所选游戏的包
        val gameIds = ArrayList<Int>() //选择的游戏的id
        gameDetails.forEach {
            it.packages?.let {
                packIds.addAll(it)
            }
            gameIds.add(it.steam_appid)
            it.dlc?.let { gameIds.addAll(it) }
        }
        startActivity<PackageListActivity>(
                SteamConstants.PACK_IDS to packIds.distinct(),
                SteamConstants.GAME_IDS to gameIds
        )
    }

    override fun onGameDetailLoadFail(t: Throwable) {
        toast(R.string.games_load_fail, t.message)
        loadingDialog?.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            R.id.menu_commit -> {
                if (selectedGameBeans.isEmpty()) {
                    toast(R.string.no_item_selected)
                    return super.onOptionsItemSelected(item)
                }
                selectedGameBeans.forEach {
                    TransManager.steamApps.clear()
                    TransManager.steamApps.addAll(
                            selectedGameBeans.map {
                                SteamApp(it)
                            })
                }
                loadingDialog?.show()
                gameDetailPresenter.loadGameDetails(selectedGameBeans.map { it.appid })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (selectedGameBeans.isNotEmpty()) {
            alert {
                titleResource = R.string.hint
                message = String.format(getString(R.string.cancel_game_select_hint), selectedGameBeans.size)
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

    override fun onDestroy() {
        super.onDestroy()
        ownedGamesPresenter.detachView()
        gameDetailPresenter.detachView()
    }
}
