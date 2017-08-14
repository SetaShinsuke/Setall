package com.seta.setall.steam.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.toast
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.adapters.SteamAppAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.api.models.GameDlcPackBean
import com.seta.setall.steam.api.models.PlayerInfoBean
import com.seta.setall.steam.domain.TransManager
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.domain.models.Transaction
import com.seta.setall.steam.events.CreateStartEvent
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.mvpViews.GameDetailMvpView
import com.seta.setall.steam.mvpViews.GameDlcPackMvpView
import com.seta.setall.steam.mvpViews.PlayerInfoMvpView
import com.seta.setall.steam.mvpViews.SteamAppDetailMvpView
import com.seta.setall.steam.presenters.GameDetailPresenter
import com.seta.setall.steam.presenters.GameDlcPackPresenter
import com.seta.setall.steam.presenters.PlayerInfoPresenter
import com.seta.setall.steam.presenters.SteamAppDetailPresenter
import kotlinx.android.synthetic.main.activity_create_trans.*
import kotlinx.android.synthetic.main.item_owned_games.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*

class CreateTransActivity : BaseActivity(),
        PlayerInfoMvpView, GameDlcPackMvpView, GameDetailMvpView, SteamAppDetailMvpView {

    var steamUserId: String by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    val games: List<SteamApp> = ArrayList()
    val playerInfoPresenter: PlayerInfoPresenter = PlayerInfoPresenter()
    val gameDetailPresenter: GameDetailPresenter = GameDetailPresenter()
    val gameDlcPackPresenter: GameDlcPackPresenter = GameDlcPackPresenter()
    val steamAppDetailPresenter: SteamAppDetailPresenter = SteamAppDetailPresenter()

    val apps = ArrayList<SteamApp>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_trans)
        playerInfoPresenter.attachView(this)
        if (steamUserId != "") {
            playerInfoPresenter.loadPlayerInfo(steamUserId)
        }
        gameDetailPresenter.attachView(this)
        gameDlcPackPresenter.attachView(this)
        steamAppDetailPresenter.attachView(this)
        mRvApps.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        apps.addAll(TransManager.steamApps)
        TransManager.steamApps.clear()
        TransManager.tranTmp = Transaction()
        TransManager.tranTmp.date = Date()
        logD("Steam apps selected : ${apps.map { "${it.name}-${it.type}-[${it.games?.size}]" }}")

        mRvApps.setHasFixedSize(false)
        mRvApps.adapter = SteamAppAdapter(apps)
        postEvent(CreateStartEvent())
        steamAppDetailPresenter.loadSteamApps(apps)
        loadingDialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameDetailPresenter.detachView()
        gameDlcPackPresenter.detachView()
        steamAppDetailPresenter.detachView()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnAddApp -> {
                val gameIds: List<Int> = games.map { it.appId }
                startActivityForResult<OwnedGamesActivity>(SteamConstants.CODE_SELECT_GAMES)
            }
        }
    }

    override fun onAppsLoad(apps: List<SteamApp>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAppsLoadFail(t: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayerInfoLoad(playerInfoBean: PlayerInfoBean) {
        with(TransManager.tranTmp) {
            if (buyerId == null || buyerId == "") {
                buyerId = playerInfoBean.personaname
            }
            if (ownerId == null || ownerId == "") {
                ownerId = playerInfoBean.personaname
            }
        }
        mRvApps.adapter.notifyDataSetChanged()
    }

    override fun onPlayerInfoLoadFail(t: Throwable) {
        logD("Load player info fail : ${t.message}")
    }


    override fun onGameDetailLoad(gameDetails: List<GameDetailBean>) {
        loadingDialog?.dismiss()
        val ids = ArrayList<Int>()
        gameDetails.forEach {
            ids.add(it.steam_appid)
            if (it.dlc != null) {
                ids.addAll(it.dlc.toList())
            }
        }
        if (ids.isNotEmpty()) {
            startActivity<GameListActivity>(SteamConstants.GAME_IDS to ids.distinct())
        }
        mRvApps.adapter = BasicAdapter(R.layout.item_owned_games, gameDetails) {
            view, position, game ->
            view.mTvGameName.text = game.name
        }
    }

    override fun onGameDetailLoadFail(t: Throwable) {
        toast(R.string.games_load_fail, t.message)
        loadingDialog?.dismiss()
    }


    override fun onGameLoad(gameDlcPackBean: GameDlcPackBean) {
        toast("onGameLoad")
        loadingDialog?.dismiss()
    }

    override fun onGamesLoad(games: List<GameDlcPackBean>) {
        toast("onGamesLoad")
        loadingDialog?.dismiss()
    }

    override fun onGameLoadFail(t: Throwable) {
        toast("onGameLoadFail")
        loadingDialog?.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val itemId = item?.itemId
        when (itemId) {
            R.id.menu_save -> {
                //TODO:保存条目
                toast("保存")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SteamConstants.CODE_SELECT_GAMES -> {
                val selectedIds = data?.extras?.get(SteamConstants.SELECTED_IDS)
                if (selectedIds != null && (selectedIds as List<*>).isNotEmpty()) {
                    mRvApps.adapter = BasicAdapter(R.layout.item_owned_games, selectedIds) {
                        view, position, i ->
                        view.mTvGameName.text = i.toString()
                    }
                    loadingDialog?.show()
                    gameDetailPresenter.loadGameDetails(selectedIds)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (apps.isNotEmpty()) {
            alert {
                titleResource = R.string.hint
                message = String.format(getString(R.string.cancel_game_select_hint), apps.size)
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
}
