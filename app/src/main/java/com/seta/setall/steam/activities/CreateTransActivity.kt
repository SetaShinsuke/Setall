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
import com.seta.setall.steam.adapters.AppItemLongClickListener
import com.seta.setall.steam.adapters.SteamAppAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.api.models.GameDlcPackBean
import com.seta.setall.steam.api.models.PlayerInfoBean
import com.seta.setall.steam.db.SteamDb
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
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class CreateTransActivity : BaseActivity(),
        PlayerInfoMvpView, GameDlcPackMvpView, GameDetailMvpView, SteamAppDetailMvpView {

    var steamUserId: String by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    val games: List<SteamApp> = ArrayList()
    val playerInfoPresenter: PlayerInfoPresenter = PlayerInfoPresenter()
    val gameDetailPresenter: GameDetailPresenter = GameDetailPresenter()
    val gameDlcPackPresenter: GameDlcPackPresenter = GameDlcPackPresenter()
    val steamAppDetailPresenter: SteamAppDetailPresenter = SteamAppDetailPresenter()

    val apps = ArrayList<SteamApp>()
    var adapter: SteamAppAdapter by Delegates.notNull<SteamAppAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_trans)
        setSwipeBackEnable(false)
        playerInfoPresenter.attachView(this)
        if (steamUserId != "") {
            playerInfoPresenter.loadPlayerInfo(steamUserId)
        }
        gameDetailPresenter.attachView(this)
        gameDlcPackPresenter.attachView(this)
        steamAppDetailPresenter.attachView(this)
        mRvApps.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        initData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData()
    }

    fun initData() {
        apps.addAll(TransManager.steamApps)
        TransManager.steamApps.clear()
//        TransManager.tranTmp = Transaction()
        if (TransManager.tranTmp.date == null) {
            TransManager.tranTmp.date = Date()
        }
        logD("Trans tmp date : ${TransManager.tranTmp.date}")
        logD("Steam apps selected : ${apps.map { "${it.name}-${it.type}-[${it.games?.size}]" }}")

        mRvApps.setHasFixedSize(false)
        adapter = SteamAppAdapter(apps, object : AppItemLongClickListener {
            override fun onItemLongClick(steamApp: SteamApp, adapterPosition: Int): Boolean {
                alert {
                    titleResource = R.string.remove_app_confirm
                    positiveButton(R.string.confirm) {
                        logD("Remove item $adapterPosition")
                        apps.remove(steamApp)
                        adapter.notifyItemRemoved(adapterPosition)
                        adapter.notifyItemRangeChanged(adapterPosition, apps.size)
//                        adapter.refreshData(apps)
                    }
                    negativeButton(R.string.cancel) {
                        it.dismiss()
                    }
                    show()
                }
                return true
            }
        })
        mRvApps.adapter = adapter
        postEvent(CreateStartEvent())
        loadingDialog?.show()
        steamAppDetailPresenter.loadSteamApps(apps)
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
                startActivity<OwnedGamesActivity>()
            }
        }
    }

    override fun onAppsLoad(apps: ArrayList<SteamApp>) {
        if (this.apps != apps) {
            this.apps.clear()
            this.apps.addAll(apps)
        }
        loadingDialog?.hide()
        adapter.refreshData(apps)
    }

    override fun onAppsLoadFail(t: Throwable) {
        loadingDialog?.hide()
        toast("加载失败 : ${t.message}")
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
                apps.firstOrNull {
                    it.initPrice == null
                            || it.purchasedPrice == null
                }?.let {
                    //有价格没填
                    toast(R.string.price_null_not_allowed)
                    return super.onOptionsItemSelected(item)
                }
                alert {
                    titleResource = R.string.hint
                    messageResource = R.string.save_confirm_msg
                    negativeButton(R.string.cancel) { it.dismiss() }
                    positiveButton(R.string.save) {
                        it.dismiss()
                        //TODO:保存条目
                        toast("保存")
                        apps.forEach {
                            it.apply {
                                purchasedDate = TransManager.tranTmp.date
                            }
                        }
                        SteamDb.instance.saveTransaction(TransManager.tranTmp.copy(steamApps = apps))
                        logD("保存订单 : ${TransManager.tranTmp.copy(steamApps = apps)}")
                        TransManager.tranTmp = Transaction()
                        finish()
                    }
                    show()
                }
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
