package com.seta.setall.common.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.logs.LogX
import com.seta.setall.common.utils.UtilMethods
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameBean
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.db.SteamDbHelper
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.domain.models.Transaction
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.mvpViews.OwnedGamesView
import com.seta.setall.steam.presenters.OwnedGamesPresenter
import kotlinx.android.synthetic.main.activity_steam_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList

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
        var content: String = "Total : ${ownedGameBean.game_count}\nAll steamAppDbs : "
        games.forEachIndexed { index, gameBean -> content += "\n$index ${gameBean.name}" }
        mTvUserInfo.text = content

        val steamApps = ArrayList<SteamApp>()
        games.forEachIndexed { i, (appid, name) ->
            steamApps.add(SteamApp(appid, name, "CNY", 100 * i, 10 * i, Date(), 0, null))
        }

        //伪造数据
        LogX.mark()
        LogX.fastLog("开始保存 记录!!")
        val steamDb = SteamDb.instance

        val transList = ArrayList<Transaction>()
        for (i in 0..5) {
            val trans = Transaction(i, Date(), "buyer_$i", "owner_$i", ArrayList<SteamApp>())
            if (i < games.size && 3 * i < games.size) {
                with(games[i]) {
                    val containedGame = ArrayList<SteamApp>()
                    containedGame.add(SteamApp(games[3 * i].appid, games[3 * i].name, "CNY", 10 * i, i, Date(), 0, null))
                    (trans.steamApps as ArrayList).add(SteamApp(appid, name, "CNY", 100 * i, 10 * i, Date(), 0, containedGame))
                }
                with(games[2 * i]) {
                    (trans.steamApps as ArrayList).add(SteamApp(appid, name, "CNY", 200 * i, 20 * i, Date(), 0, null))
                }
            }
            transList.add(trans)
            LogX.fastLog("===========\nItem NO. $i: \n$trans\n==================")
        }
        steamDb.saveTransactions(transList)
//        steamDb.saveSteamApps(steamAppDbs)
        UtilMethods.exportDb(this, SteamDbHelper.STEAM_DB_NAME)
    }

    override fun onGamesLoadFail(t: Throwable) {
        mTvUserInfo.text = ""
    }
}
